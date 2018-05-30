package com.jinanlongen.manatee.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jd.open.api.sdk.domain.category.Category;
import com.jd.open.api.sdk.domain.list.CategoryAttrReadService.CategoryAttr;
import com.jd.open.api.sdk.domain.list.CategoryAttrValueReadService.CategoryAttrValueJos;
import com.jinanlongen.manatee.domain.CategoryDoc;
import com.jinanlongen.manatee.domain.CategoryStoreDoc;
import com.jinanlongen.manatee.domain.ParDoc;
import com.jinanlongen.manatee.domain.ParValueDoc;
import com.jinanlongen.manatee.domain.Shop;
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

  // jd 类目属性
  public void synJdCategoryAttrs() {
    List<CategoryDoc> docs = getJdAllCategoryDoc(false);
    Map<String, List<CategoryDoc>> map =
        docs.stream().collect(Collectors.groupingBy(i -> i.getShopId()));
    Set<String> keys = map.keySet();
    for (String shopId : keys) {
      synCategoryAttrsByShop(shopId, map.get(shopId));
    }
  }

  private void synCategoryAttrsByShop(String shopId, List<CategoryDoc> list) {
    Shop shop = shopRep.findById(shopId).get();
    logger.info("店铺{},同步类目{}", shop.getName(), list.size());
    JdUtils jdu = new JdUtils(shop);
    ParDoc par;
    ParValueDoc value;
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
          for (CategoryAttrValueJos AttrValue : AttrValues) {
            value = new ParValueDoc().parseFromJdAttrsValue(AttrValue);
            valueRep.save(value);
          }
        }
      }
    }
    logger.info("同步京东类目属性完成");
  }



  // 同步jd所有店铺的类目
  public void synAllJdCategory() {
    List<CategoryDoc> categoryDocs = getJdAllCategoryDoc(true);
    for (CategoryDoc categoryDoc : categoryDocs) {
      categoryRep.save(categoryDoc);
    }
  }

  private List<CategoryDoc> getJdAllCategoryDoc(boolean saveCategoryStoreDoc) {
    logger.info("开始同步京东所有店铺的类目信息");
    List<Shop> list = shopRep.findJdShop();
    JdUtils jdu;
    CategoryStoreDoc categoryStore;
    List<Category> categoryList;
    List<CategoryDoc> allCategory = new ArrayList<CategoryDoc>();
    List<Integer> categoryIds = new ArrayList<Integer>();
    for (Shop shop : list) {
      jdu = new JdUtils(shop);
      categoryList = jdu.getCategory();
      logger.info("店铺{}开通类目{}", shop.getName(), categoryList.size());
      for (Category jdcategory : categoryList) {

        if (saveCategoryStoreDoc && !jdcategory.isParent()) {// 保存店铺关系，最底层级类目
          categoryStore = new CategoryStoreDoc().paseFromCategoryAndStore(jdcategory, shop);
          categoryStoreRep.save(categoryStore);// 保存店铺类目关系
        }
        if (!categoryIds.contains(jdcategory.getId())) {
          allCategory.add(new CategoryDoc().parseFromJdCategory(jdcategory, shop.getShopId()));
          categoryIds.add(jdcategory.getId());
        }
      }
    }
    logger.info("共需同步京东类目{}", allCategory.size());
    return allCategory;
  }



}
