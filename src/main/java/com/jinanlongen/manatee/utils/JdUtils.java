package com.jinanlongen.manatee.utils;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.domain.category.Category;
import com.jd.open.api.sdk.domain.list.CategoryAttrReadService.CategoryAttr;
import com.jd.open.api.sdk.domain.list.CategoryAttrValueReadService.CategoryAttrValueJos;
import com.jd.open.api.sdk.request.category.CategorySearchRequest;
import com.jd.open.api.sdk.request.list.CategoryReadFindAttrsByCategoryIdRequest;
import com.jd.open.api.sdk.request.list.CategoryReadFindValuesByAttrIdJosRequest;
import com.jd.open.api.sdk.request.list.CategoryReadFindValuesByIdJosRequest;
import com.jd.open.api.sdk.request.list.PopVenderCenerVenderBrandQueryRequest;
import com.jd.open.api.sdk.request.ware.WareWriteUpdateWareRequest;
import com.jd.open.api.sdk.response.category.CategorySearchResponse;
import com.jd.open.api.sdk.response.list.CategoryReadFindAttrsByCategoryIdResponse;
import com.jd.open.api.sdk.response.list.CategoryReadFindValuesByAttrIdJosResponse;
import com.jd.open.api.sdk.response.list.CategoryReadFindValuesByIdJosResponse;
import com.jd.open.api.sdk.response.list.PopVenderCenerVenderBrandQueryResponse;
import com.jd.open.api.sdk.response.list.VenderBrandPubInfo;
import com.jd.open.api.sdk.response.ware.WareWriteUpdateWareResponse;
import com.jinanlongen.manatee.domain.Store;

/**
 * 类说明
 * 
 * @author shangyao
 * @date 2017年11月22日
 */

public class JdUtils {
  private static Logger logger = LoggerFactory.getLogger(JdUtils.class);
  public static final String SERVER_URL = "https://api.jd.com/routerjson";

  public JdUtils(Store shop) {
    this.accessToken = shop.getAccessToken();
    this.appKey = shop.getAppKey();
    this.appSecret = shop.getAppSecret();
    this.client = new DefaultJdClient(SERVER_URL, accessToken, appKey, appSecret);
  }

  private String accessToken;
  private String appKey;
  private String appSecret;
  private JdClient client;

  public void updateWare(WareWriteUpdateWareRequest request) {

    WareWriteUpdateWareResponse response = null;
    try {
      response = client.execute(request);
      System.out.println(response.getMsg());
    } catch (JdException e) {
      e.printStackTrace();
    }
  }

  // 查询商家已授权的品牌
  public List<VenderBrandPubInfo> queryBrand() {
    PopVenderCenerVenderBrandQueryRequest request = new PopVenderCenerVenderBrandQueryRequest();
    PopVenderCenerVenderBrandQueryResponse response = null;
    try {
      response = client.execute(request);
    } catch (JdException e) {
      e.printStackTrace();
    }
    return response.getBrandList();
  }

  // 获取商家类目信息
  public List<Category> getCategory() {
    CategorySearchRequest request = new CategorySearchRequest();
    CategorySearchResponse response = null;
    try {
      response = client.execute(request);
    } catch (JdException e) {
      e.printStackTrace();
    }
    if (response == null) {
      logger.info("获取商家类目失败");
      return new ArrayList<Category>();
    }
    return response.getCategory();

  }



  // 获取类目属性列表
  public List<CategoryAttr> findAttrsByCategoryId(Long id) {
    CategoryReadFindAttrsByCategoryIdRequest request =
        new CategoryReadFindAttrsByCategoryIdRequest();
    request.setCid(id);
    request.setField("categoryAttrs");
    CategoryReadFindAttrsByCategoryIdResponse response = null;
    try {
      response = client.execute(request);
      // System.out.println(response.getMsg());
    } catch (JdException e) {
      e.printStackTrace();
    }
    if (response != null && response.getCategoryAttrs() != null) {
      return response.getCategoryAttrs();
    } else {
      logger.info("获取类目属性列表,id为{}的类目无返回值", id);
      return new ArrayList<CategoryAttr>();
    }
  }

  // 获取类目属性值liebiao
  public List<CategoryAttrValueJos> findValuesByAttrIdJos(Long id) {
    CategoryReadFindValuesByAttrIdJosRequest request =
        new CategoryReadFindValuesByAttrIdJosRequest();
    CategoryReadFindValuesByAttrIdJosResponse response = null;
    request.setCategoryAttrId(id);
    try {
      response = client.execute(request);
    } catch (JdException e) {
      e.printStackTrace();
    }
    if (response != null && response.getCategoryAttrValues() != null) {
      return response.getCategoryAttrValues();
    } else {
      logger.info("获取类目属性值列表,id为{}的类目无返回值", id);
      return new ArrayList<CategoryAttrValueJos>();
    }
  }

  // 获取一个属性值
  public CategoryAttrValueJos findValuesByIdJos(Long id) {
    CategoryReadFindValuesByIdJosRequest request = new CategoryReadFindValuesByIdJosRequest();
    CategoryReadFindValuesByIdJosResponse response = null;
    request.setId(id);
    try {
      response = client.execute(request);
    } catch (JdException e) {
      e.printStackTrace();
    }
    if (response != null) {
      return response.getCategoryAttrValue();
    } else {
      logger.info("获取一个属性值,id为{}无返回值", id);
      return null;
    }
  }
}
