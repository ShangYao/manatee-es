package com.jinanlongen.manatee.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.jd.open.api.sdk.domain.category.Category;
import com.jd.open.api.sdk.domain.list.CategoryAttrReadService.CategoryAttr;
import com.jd.open.api.sdk.domain.list.CategoryAttrValueReadService.CategoryAttrValueJos;
import com.jinanlongen.manatee.domain.CategoryDoc;
import com.jinanlongen.manatee.domain.CategoryStoreDoc;
import com.jinanlongen.manatee.domain.ParDoc;
import com.jinanlongen.manatee.domain.ParValueDoc;
import com.jinanlongen.manatee.domain.Store;
import com.jinanlongen.manatee.repository.CategoryRep;
import com.jinanlongen.manatee.repository.CategoryStoreRep;
import com.jinanlongen.manatee.repository.ParRep;
import com.jinanlongen.manatee.repository.ParValueRep;
import com.jinanlongen.manatee.repository.ShopRep;
import com.jinanlongen.manatee.utils.JdUtils;

@Service
public class JdService {
  private Logger logger = LoggerFactory.getLogger(JdService.class);
  @Autowired
  private ShopRep shopRep;
  @Autowired
  private CategoryRep categoryRep;
  @Autowired
  private ParRep parRep;
  @Autowired
  private ParValueRep valueRep;
  @Autowired
  private CategoryStoreRep categoryStoreRep;



  // jd saleAttr
  public void synSaleAttr() {
    Map<String, List<ParDoc>> salePars = featchSalePars();
    Set<String> categorys = salePars.keySet();

    Map<String, List<CategoryStoreDoc>> categoryStores = featchCategoryStores();
    Set<String> storeIds = categoryStores.keySet();

    List<CategoryStoreDoc> csOfStore;
    JdUtils jdu;
    for (String id : storeIds) {
      jdu = new JdUtils(shopRep.findById(id).get());
      csOfStore = categoryStores.get(id);
      for (CategoryStoreDoc categoryStoreDoc : csOfStore) {
        if (categorys.contains(categoryStoreDoc.getCategory_id())) {


          saveAttrValues(jdu, salePars.get(categoryStoreDoc.getCategory_id()), id);

        }
      }
    }
  }

  private void saveAttrValues(JdUtils jdu, List<ParDoc> list, String storeid) {
    // TODO Auto-generated method stub
    logger.info("保存类目属性{}", list.size());
    List<CategoryAttrValueJos> CategoryAttrValues;
    for (ParDoc parDoc : list) {
      CategoryAttrValues = jdu.findValuesByAttrIdJos(Long.parseLong(parDoc.getCode()));
      saveAttrValues(CategoryAttrValues, storeid);
    }
  }

  private void saveAttrValues(List<CategoryAttrValueJos> CategoryAttrValues) {
    ParValueDoc value;
    for (CategoryAttrValueJos categoryAttrValueJos : CategoryAttrValues) {
      value = new ParValueDoc().parseFromJdAttrsValue(categoryAttrValueJos);
      valueRep.save(value);
    }
  }

  private void saveAttrValues(List<CategoryAttrValueJos> CategoryAttrValues, String storeid) {
    ParValueDoc value;
    for (CategoryAttrValueJos categoryAttrValueJos : CategoryAttrValues) {
      value = new ParValueDoc().parseFromJdAttrsValue(categoryAttrValueJos, storeid);
      valueRep.save(value);
    }
  }

  private Map<String, List<CategoryStoreDoc>> featchCategoryStores() {
    Iterable<CategoryStoreDoc> cs = categoryStoreRep.findAll();
    Iterator<CategoryStoreDoc> it = cs.iterator();
    List<CategoryStoreDoc> list = new ArrayList<CategoryStoreDoc>();
    while (it.hasNext()) {
      list.add(it.next());
    }
    Map<String, List<CategoryStoreDoc>> csm =
        list.stream().collect(Collectors.groupingBy(i -> i.getStore_id()));
    return csm;
  }

  private Map<String, List<ParDoc>> featchSalePars() {
    Pageable pageable = PageRequest.of(0, 2000);
    List<ParDoc> pars = parRep.getSaleAttr(pageable).getContent();
    Map<String, List<ParDoc>> salePars =
        pars.stream().collect(Collectors.groupingBy(i -> i.getCategory_id()));
    return salePars;
  }

  // 仅 保存 par ,par_value
  @Async
  public void synJdCategoryAttrs() {
    List<CategoryDoc> docs = getJdAllCategoryDoc(false);// 不保存店铺类目关系
    Map<String, List<CategoryDoc>> map =
        docs.stream().collect(Collectors.groupingBy(i -> i.getStore_id()));
    Set<String> keys = map.keySet();
    for (String storeId : keys) {
      synCategoryAttrsByShop(storeId, map.get(storeId));
    }
  }

  private void synCategoryAttrsByShop(String storeId, List<CategoryDoc> list) {
    Store shop = shopRep.findById(storeId).get();
    logger.info("店铺{},同步类目{}", shop.getName(), list.size());
    JdUtils jdu = new JdUtils(shop);
    ParDoc par;
    List<CategoryAttrValueJos> AttrValues;
    for (CategoryDoc categoryDoc : list) {
      if (!categoryDoc.isIs_leaf()) {
        continue;
      }
      List<CategoryAttr> attrs = jdu.findAttrsByCategoryId(Long.parseLong(categoryDoc.getCode()));
      for (CategoryAttr categoryAttr : attrs) {
        par = new ParDoc().parsFromJdAttrs(categoryAttr);
        parRep.save(par);
        if (categoryAttr.getInputType() != 3 && categoryAttr.getAttributeType() != 4) {// 非可输入类型，非销售属性
          AttrValues = jdu.findValuesByAttrIdJos(Long.parseLong(par.getCode()));
          saveAttrValues(AttrValues);
        }
      }
    }
    logger.info("同步京东类目属性完成");
  }



  // 同步jd所有店铺的类目
  @Async
  public void synAllJdCategory() {
    logger.info(Thread.currentThread().getName() + "-----jd-----异步：>");
    List<CategoryDoc> categoryDocs = getJdAllCategoryDoc(true);
    for (CategoryDoc categoryDoc : categoryDocs) {
      categoryRep.save(categoryDoc);
    }
  }

  private List<CategoryDoc> getJdAllCategoryDoc(boolean saveCategoryStoreDoc) {
    logger.info("开始同步京东所有店铺的类目信息");
    List<Store> list = shopRep.findJdShop();
    JdUtils jdu;
    CategoryStoreDoc categoryStore;
    List<Category> categoryList;
    List<CategoryDoc> allCategory = new ArrayList<CategoryDoc>();
    List<Integer> categoryIds = new ArrayList<Integer>();
    for (Store shop : list) {
      jdu = new JdUtils(shop);
      categoryList = jdu.getCategory();
      logger.info("店铺{}开通类目{}", shop.getName(), categoryList.size());
      for (Category jdcategory : categoryList) {

        if (saveCategoryStoreDoc && !jdcategory.isParent()) {// 保存店铺关系，最底层级类目
          categoryStore = new CategoryStoreDoc().paseFromCategoryAndStore(jdcategory, shop);
          categoryStoreRep.save(categoryStore);// 保存店铺类目关系
        }
        if (!categoryIds.contains(jdcategory.getId())) {
          allCategory.add(new CategoryDoc().parseFromJdCategory(jdcategory, shop.getId()));
          categoryIds.add(jdcategory.getId());
        }
      }
    }
    logger.info("共需同步京东类目{}", allCategory.size());
    return allCategory;
  }



}
