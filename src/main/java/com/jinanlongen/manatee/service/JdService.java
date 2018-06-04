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
import com.jinanlongen.manatee.domain.ParValue;
import com.jinanlongen.manatee.domain.Store;
import com.jinanlongen.manatee.repository.CategoryRep;
import com.jinanlongen.manatee.repository.CategoryStoreRep;
import com.jinanlongen.manatee.repository.ParRep;
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
  private CategoryStoreRep categoryStoreRep;

  // 同步jd所有店铺的类目
  @Async
  public void synAllJdCategory() {
    logger.info(Thread.currentThread().getName() + "异步：同步jd所有店铺的类目>");
    List<CategoryDoc> categoryDocs = getJdAllCategoryDoc(true);
    for (CategoryDoc categoryDoc : categoryDocs) {
      categoryDoc = generatePath(categoryDoc, categoryDocs);
      categoryRep.save(categoryDoc);
    }
    logger.info(Thread.currentThread().getName() + "同步jd所有店铺的类目完成");
  }

  // jd saleAttr
  @Async
  public void synSaleAttr() {
    logger.info("开始保存京东类目销售属性........");
    Map<String, List<ParDoc>> salePars = featchSalePars();// 销售属性按分类 分组
    Set<String> categorys = salePars.keySet();

    Map<String, List<CategoryStoreDoc>> categoryStores = featchCategoryStores();// 找出店铺开通的类目
    Set<String> storeIds = categoryStores.keySet();

    List<CategoryStoreDoc> csOfStore;
    JdUtils jdu;
    Store store;
    for (String id : storeIds) {
      store = shopRep.findById(id).get();
      jdu = new JdUtils(store);
      csOfStore = categoryStores.get(id);
      for (CategoryStoreDoc categoryStoreDoc : csOfStore) {
        if (categorys.contains(categoryStoreDoc.getCategory_code())) {
          saveAttrValues(jdu, salePars.get(categoryStoreDoc.getCategory_code()), store);
        }
      }
    }
    logger.info("保存类目销售属性完成");
  }

  private void saveAttrValues(JdUtils jdu, List<ParDoc> list, Store store) {
    ParDoc salePar;
    List<CategoryAttrValueJos> AttrValues;
    for (ParDoc par : list) {
      salePar = par.generateSalePar(par, store);
      AttrValues = jdu.findValuesByAttrIdJos(Long.parseLong(par.getCode()));
      par.setValues(generateValues(AttrValues));
      parRep.save(salePar);
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
        pars.stream().collect(Collectors.groupingBy(i -> (i.getCategory().get(0).getCode())));
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
    logger.info("同步京东类目属性，属性值完成");
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
        par = new ParDoc().parsFromJdAttrs(categoryAttr, shop, categoryDoc);
        if (categoryAttr.getInputType() != 3 && categoryAttr.getAttributeType() != 4) {// 非可输入类型，非销售属性
          AttrValues = jdu.findValuesByAttrIdJos(Long.parseLong(par.getCode()));
          par.setValues(generateValues(AttrValues));
        }
        parRep.save(par);
      }
    }

  }

  private List<ParValue> generateValues(List<CategoryAttrValueJos> CategoryAttrValues) {
    List<ParValue> values = new ArrayList<ParValue>();
    for (CategoryAttrValueJos categoryAttrValueJos : CategoryAttrValues) {
      values.add(new ParValue().generate(categoryAttrValueJos));
    }
    return values;
  }



  private CategoryDoc generatePath(CategoryDoc categoryDoc, List<CategoryDoc> categoryDocs) {
    if (categoryDoc.getLevel() == 1) {
      categoryDoc.setPath(categoryDoc.getName());
    } else if (categoryDoc.getLevel() == 2) {
      CategoryDoc fCategoryDoc = getFCategoryDoc(categoryDoc.getPcode(), categoryDocs);
      categoryDoc.setPath(fCategoryDoc.getName() + "|" + categoryDoc.getName());
    } else if (categoryDoc.getLevel() == 3) {
      CategoryDoc fCategoryDoc = getFCategoryDoc(categoryDoc.getPcode(), categoryDocs);
      CategoryDoc topCategoryDoc = getFCategoryDoc(fCategoryDoc.getPcode(), categoryDocs);
      categoryDoc.setPath(
          topCategoryDoc.getName() + "|" + fCategoryDoc.getName() + "|" + categoryDoc.getName());
    }

    return categoryDoc;
  }

  private CategoryDoc getFCategoryDoc(String pcode, List<CategoryDoc> categoryDocs) {
    for (CategoryDoc categoryDoc : categoryDocs) {
      if (categoryDoc.getCode().equals(pcode)) {
        return categoryDoc;
      }
    }
    return new CategoryDoc();
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
