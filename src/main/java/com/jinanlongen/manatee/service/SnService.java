package com.jinanlongen.manatee.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.jinanlongen.manatee.domain.CategoryDoc;
import com.jinanlongen.manatee.domain.ParDoc;
import com.jinanlongen.manatee.domain.ParValueDoc;
import com.jinanlongen.manatee.domain.Store;
import com.jinanlongen.manatee.repository.CategoryRep;
import com.jinanlongen.manatee.repository.ParRep;
import com.jinanlongen.manatee.repository.ParValueRep;
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
  private ParValueRep valueRep;

  // sn 类目属性
  @Async
  public void synSnCategoryAttrs() {
    List<CategoryDoc> docs = getSnAllCategoryDoc();
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
    SnUtils sn = new SnUtils(shop);
    List<ItemparametersQuery> parameters;
    List<String> parCodes = list.stream().map(i -> i.getCode()).collect(Collectors.toList());
    for (String code : parCodes) {
      parameters = sn.itemparametersQuery(code);
      for (ItemparametersQuery parameter : parameters) {
        parameter.setCategoryCode(code);// api不返回分类code
        synOneItemparameter(parameter);
      }
    }
    logger.info("苏宁类目属性及属性值同步完成");
  }

  private void synOneItemparameter(ItemparametersQuery parameter) {
    ParDoc par = new ParDoc().parsFromSnAttrs(parameter);
    parRep.save(par);
    if (!par.getInput_type().equals("3")) {
      List<ParValueDoc> values = new ParValueDoc().parsFromSnAttrs(parameter);
      for (ParValueDoc value : values) {
        valueRep.save(value);
      }
    }
  }

  // 同步sn所有店铺的类目
  @Async
  public void synAllSnCategory() {
    logger.info(Thread.currentThread().getName() + "----sn------异步：>");
    List<CategoryDoc> categoryDocs = getSnAllCategoryDoc();
    for (CategoryDoc categoryDoc : categoryDocs) {
      categoryRep.save(categoryDoc);
    }
  }

  private List<CategoryDoc> getSnAllCategoryDoc() {
    logger.info("开始同步sn所有店铺的类目信息");
    List<Store> list = shopRep.findSnShop();
    SnUtils sn;
    List<CategoryQuery> categoryList;
    List<CategoryDoc> allCategory = new ArrayList<CategoryDoc>();
    List<String> categoryIds = new ArrayList<String>();
    CategoryQueryRequest request;
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
          if (!categoryIds.contains(category.getCategoryCode())) {
            allCategory.add(new CategoryDoc().parseFromSnCategory(category, shop.getId()));
            categoryIds.add(category.getCategoryCode());
          }
        }
      }
    }
    return allCategory;
  }

  @Async
  public void synAllSn() {
    logger.info(Thread.currentThread().getName() + "----sn------异步：>");
    synSnCategoryAttrs();
  }

}
