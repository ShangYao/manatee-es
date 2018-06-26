package com.jinanlongen.manatee.service;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.jinanlongen.manatee.domain.CategoryDoc;
import com.jinanlongen.manatee.domain.CategoryStoreDoc;
import com.jinanlongen.manatee.domain.ParDoc;
import com.jinanlongen.manatee.domain.Store;
import com.jinanlongen.manatee.repository.CategoryRep;
import com.jinanlongen.manatee.repository.CategoryStoreRep;
import com.jinanlongen.manatee.repository.ParRep;
import com.jinanlongen.manatee.repository.ShopRep;
import com.jinanlongen.manatee.utils.SnUtils;
import com.suning.api.entity.item.CategoryQueryRequest;
import com.suning.api.entity.item.CategoryQueryResponse.CategoryQuery;
import com.suning.api.entity.item.ItemparametersQueryResponse.ItemparametersQuery;

@Service
public class SnService {
  private Logger logger = LoggerFactory.getLogger(SnService.class);
  @Autowired
  private ShopRep shopRep;
  @Autowired
  private CategoryRep categoryRep;
  @Autowired
  private ParRep parRep;
  @Autowired
  private CategoryStoreRep categoryStoreRep;

  // 同步sn所有店铺的类目
  @Async
  public void synAllSnCategory() {
    logger.info(Thread.currentThread().getName() + "----sn 类目------：>");
    List<CategoryDoc> categoryDocs = getSnAllCategoryDoc();
    for (CategoryDoc categoryDoc : categoryDocs) {
      categoryRep.save(categoryDoc);
    }
    logger.info("苏宁类目同步完成");
  }

  // sn 类目属性
  @Async
  public void synSnCategoryAttrs() {
    logger.info(Thread.currentThread().getName() + "----开始同步所有苏宁类目属性------：>");
    Pageable page = PageRequest.of(0, 2000);
    List<CategoryStoreDoc> cdList = categoryStoreRep.getSnCategoryStore(page).getContent();
    Map<String, List<CategoryDoc>> map = generateCategoryDocStore(cdList);
    Set<String> keys = map.keySet();
    for (String storeId : keys) {
      synCategoryAttrsByShop(storeId, map.get(storeId));
    }
    logger.info("苏宁类目属性及属性值同步完成");
  }

  private Map<String, List<CategoryDoc>> generateCategoryDocStore(List<CategoryStoreDoc> cdList) {
    List<CategoryStoreDoc> singleCategory = new ArrayList<CategoryStoreDoc>();
    List<String> categoryCodes = new ArrayList<String>();
    for (CategoryStoreDoc doc : cdList) {
      if (!categoryCodes.contains(doc.getCategory_code())) {
        categoryCodes.add(doc.getCategory_code());
        singleCategory.add(doc);
      }
    }
    Map<String, List<CategoryStoreDoc>> map =
        singleCategory.stream().collect(Collectors.groupingBy(i -> i.getStore_id()));
    Map<String, List<CategoryDoc>> categoryMap = new HashMap<>();
    Set<String> storeCodes = map.keySet();
    List<CategoryDoc> CategoryDocs;// = new ArrayList<CategoryDoc>();
    List<CategoryStoreDoc> CategoryStoreDocs;// = new ArrayList<CategoryStoreDoc>();
    for (String code : storeCodes) {
      CategoryStoreDocs = map.get(code);
      CategoryDocs = new ArrayList<CategoryDoc>();
      for (CategoryStoreDoc categoryStoreDoc : CategoryStoreDocs) {
        CategoryDocs.add((categoryRep.findById("SN#" + categoryStoreDoc.getCategory_code())).get());
      }
      categoryMap.put(code, CategoryDocs);
    }
    return categoryMap;
  }

  private void synCategoryAttrsByShop(String storeId, List<CategoryDoc> list) {
    Store shop = shopRep.findById(storeId).get();
    logger.info("店铺{},同步类目{}", shop.getName(), list.size());
    SnUtils sn = new SnUtils(shop);
    List<ItemparametersQuery> parameters;
    ParDoc par;
    for (CategoryDoc categoryDoc : list) {
      parameters = sn.itemparametersQuery(categoryDoc.getCode());
      for (ItemparametersQuery parameter : parameters) {
        par = new ParDoc().parsFromSnAttrs(parameter, categoryDoc);
        parRep.save(par);
      }
    }

  }



  private List<CategoryDoc> getSnAllCategoryDoc() {
    List<Store> list = shopRep.findSnShop();
    SnUtils sn;
    List<CategoryQuery> categoryList;
    List<CategoryDoc> allCategory = new ArrayList<CategoryDoc>();
    List<String> categoryIds = new ArrayList<String>();
    CategoryQueryRequest request;
    CategoryStoreDoc categoryStore;
    for (Store shop : list) {
      sn = new SnUtils(shop);
      request = new CategoryQueryRequest();
      categoryList = sn.categoryQuery(request);
      logger.info("店铺{}开通类目{}", shop.getName(), categoryList.size());
      if (categoryList == null || categoryList.size() == 0) {
        logger.info("店铺{}无返回类目！", shop.getName());
        continue;
      } else {
        for (CategoryQuery category : categoryList) {
          categoryStore = new CategoryStoreDoc().paseFromCategoryAndStore(category, shop);
          categoryStoreRep.save(categoryStore);// 保存店铺类目关系
          if (!categoryIds.contains(category.getCategoryCode())) {
            allCategory.add(new CategoryDoc().parseFromSnCategory(category));
            categoryIds.add(category.getCategoryCode());
          }
        }
      }
    }
    return allCategory;
  }


}
