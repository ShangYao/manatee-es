package com.jinanlongen.manatee.service;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.jd.open.api.sdk.response.list.VenderBrandPubInfo;
import com.jinanlongen.manatee.domain.BrandDoc;
import com.jinanlongen.manatee.domain.CategoryDoc;
import com.jinanlongen.manatee.domain.CategoryStoreDoc;
import com.jinanlongen.manatee.domain.ParDoc;
import com.jinanlongen.manatee.domain.ParValue;
import com.jinanlongen.manatee.domain.Store;
import com.jinanlongen.manatee.repository.BrandRepo;
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
  private BrandRepo brandRepo;
  @Autowired
  private CategoryStoreRep categoryStoreRep;

  // 同步品牌
  @Async
  public void synBrand() {
    List<Store> stores = shopRep.findJdShop();
    for (Store store : stores) {
      JdUtils jd = new JdUtils(store);
      List<VenderBrandPubInfo> jdBrands = jd.queryBrand();
      for (VenderBrandPubInfo venderBrandPubInfo : jdBrands) {
        BrandDoc brand = BrandDoc.build(venderBrandPubInfo, store);
        brandRepo.save(brand);
      }
    }
  }

  // 同步jd所有店铺的类目
  @Async
  public void synAllJdCategory() {
    logger.info(Thread.currentThread().getName() + "异步：同步jd所有店铺的类目");
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

    for (String id : storeIds) {
      Store store = shopRep.findById(id).get();
      JdUtils jdu = new JdUtils(store);
      List<CategoryStoreDoc> csOfStore = categoryStores.get(id);
      for (CategoryStoreDoc categoryStoreDoc : csOfStore) {
        if (categorys.contains(categoryStoreDoc.getCategory_code())) {
          saveAttrValues(jdu, salePars.get(categoryStoreDoc.getCategory_code()), store);
        }
      }
    }
    logger.info("保存类目销售属性完成");
  }

  private void saveAttrValues(JdUtils jdu, List<ParDoc> list, Store store) {
    for (ParDoc par : list) {
      ParDoc salePar = par.generateSalePar(par, store);
      List<CategoryAttrValueJos> AttrValues =
          jdu.findValuesByAttrIdJos(Long.parseLong(par.getCode()));
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
    Pageable pageable = PageRequest.of(0, 4000);
    List<ParDoc> pars = parRep.getSaleAttr(pageable).getContent();
    Map<String, List<ParDoc>> salePars =
        pars.stream().collect(Collectors.groupingBy(i -> (i.getCategory().getCode())));
    return salePars;
  }

  // 同步类目属性，非销售属性的属性值列表
  @Async
  public void synJdCategoryAttrs() {
    Pageable page = PageRequest.of(0, 2000);
    List<CategoryStoreDoc> cdList = categoryStoreRep.getJdCategoryStore(page).getContent();
    Map<String, List<CategoryDoc>> map = generateCategoryDocStore(cdList);
    Set<String> keys = map.keySet();
    for (String storeId : keys) {
      synCategoryAttrsByShop(storeId, map.get(storeId));
    }
    logger.info("同步京东类目属性，属性值完成");
  }

  private Map<String, List<CategoryDoc>> generateCategoryDocStore(List<CategoryStoreDoc> cdList) {
    // 去除重复分类
    List<CategoryStoreDoc> singleCategory = new ArrayList<>();
    List<String> categoryCodes = new ArrayList<>();
    for (CategoryStoreDoc doc : cdList) {
      if (!categoryCodes.contains(doc.getCategory_code())) {
        categoryCodes.add(doc.getCategory_code());
        singleCategory.add(doc);
      }
    }

    Map<String, List<CategoryDoc>> categoryMap = new HashMap<>();
    Map<String, List<CategoryStoreDoc>> map =
        singleCategory.stream().collect(Collectors.groupingBy(i -> i.getStore_id()));
    Set<String> storeCodes = map.keySet();
    for (String code : storeCodes) {
      List<CategoryDoc> CategoryDocs = new ArrayList<>();
      List<CategoryStoreDoc> CategoryStoreDocs = map.get(code);
      for (CategoryStoreDoc categoryStoreDoc : CategoryStoreDocs) {
        CategoryDocs.add((categoryRep.findById("JD#" + categoryStoreDoc.getCategory_code())).get());
      }
      categoryMap.put(code, CategoryDocs);
    }
    return categoryMap;
  }

  // 同步店铺的类目属性
  private void synCategoryAttrsByShop(String storeId, List<CategoryDoc> list) {
    Store shop = shopRep.findById(storeId).get();
    logger.info("店铺{},同步类目{}", shop.getName(), list.size());
    JdUtils jdu = new JdUtils(shop);
    for (CategoryDoc categoryDoc : list) {
      if (!categoryDoc.isIs_leaf()) {
        continue;
      }
      List<CategoryAttr> attrs = jdu.findAttrsByCategoryId(Long.parseLong(categoryDoc.getCode()));
      for (CategoryAttr categoryAttr : attrs) {
        ParDoc par = new ParDoc().parsFromJdAttrs(categoryAttr, shop, categoryDoc);
        if (categoryAttr.getInputType() != 3 && categoryAttr.getAttributeType() != 4) {// 非可输入类型，非销售属性
          List<CategoryAttrValueJos> AttrValues =
              jdu.findValuesByAttrIdJos(Long.parseLong(par.getCode()));
          par.setValues(generateValues(AttrValues));
        }
        parRep.save(par);
      }
    }

  }

  // 生成属性值列表
  private List<ParValue> generateValues(List<CategoryAttrValueJos> CategoryAttrValues) {
    List<ParValue> values = new ArrayList<>();
    for (CategoryAttrValueJos categoryAttrValueJos : CategoryAttrValues) {
      values.add(new ParValue().generate(categoryAttrValueJos));
    }
    return values;
  }


  // 生成分类path
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
    logger.info("获取京东所有店铺的类目信息");
    List<Store> list = shopRep.findJdShop();
    List<CategoryDoc> allCategory = new ArrayList<>();
    List<Integer> categoryIds = new ArrayList<>();
    int count = 0;
    for (Store shop : list) {
      JdUtils jdu = new JdUtils(shop);
      List<Category> categoryList = jdu.getCategory();
      count += categoryList.size();
      logger.info("店铺{}开通类目{}", shop.getName(), categoryList.size());
      for (Category jdcategory : categoryList) {
        if (saveCategoryStoreDoc && !jdcategory.isParent()) {// 保存店铺关系，最底层级类目
          CategoryStoreDoc categoryStore =
              new CategoryStoreDoc().paseFromCategoryAndStore(jdcategory, shop);
          categoryStoreRep.save(categoryStore);// 保存店铺类目关系
        }
        if (!categoryIds.contains(jdcategory.getId())) {
          allCategory.add(new CategoryDoc().parseFromJdCategory(jdcategory));
          categoryIds.add(jdcategory.getId());
        }
      }
    }
    logger.info("{}家店铺，共{}个类目，不重复的类目共{}个", list.size(), count, allCategory.size());
    return allCategory;
  }



}
