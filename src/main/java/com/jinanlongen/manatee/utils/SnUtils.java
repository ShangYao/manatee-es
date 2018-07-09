package com.jinanlongen.manatee.utils;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.jinanlongen.manatee.domain.Store;
import com.jinanlongen.manatee.repository.ShopRep;
import com.suning.api.DefaultSuningClient;
import com.suning.api.entity.custom.NewbrandQueryRequest;
import com.suning.api.entity.custom.NewbrandQueryResponse;
import com.suning.api.entity.custom.NewbrandQueryResponse.QueryNewbrand;
import com.suning.api.entity.item.CategoryQueryRequest;
import com.suning.api.entity.item.CategoryQueryResponse;
import com.suning.api.entity.item.CategoryQueryResponse.CategoryQuery;
import com.suning.api.entity.item.ItemparametersQueryRequest;
import com.suning.api.entity.item.ItemparametersQueryResponse;
import com.suning.api.entity.item.ItemparametersQueryResponse.ItemparametersQuery;
import com.suning.api.entity.master.CityQueryRequest;
import com.suning.api.entity.master.CityQueryResponse;
import com.suning.api.entity.master.CityQueryResponse.City;
import com.suning.api.entity.master.NationQueryRequest;
import com.suning.api.entity.master.NationQueryResponse;
import com.suning.api.entity.master.NationQueryResponse.Nation;
import com.suning.api.entity.sale.FreighttemplateQueryRequest;
import com.suning.api.entity.sale.FreighttemplateQueryResponse;
import com.suning.api.entity.sale.FreighttemplateQueryResponse.QueryFreighttemplate;
import com.suning.api.exception.SuningApiException;

/**
 * 
 * @description
 * @author shangyao
 * @date 2018年4月11日
 */
public class SnUtils {
  private static Logger logger = LoggerFactory.getLogger(SnUtils.class);
  public static final String SERVER_URL = "https://open.suning.com/api/http/sopRequest";
  @Autowired
  ShopRep shopRep;

  public SnUtils(Store shop) {
    this.appKey = shop.getAppKey();
    this.appSecret = shop.getAppSecret();
    this.client = new DefaultSuningClient(SERVER_URL, appKey, appSecret);
  }

  public SnUtils() {
    this.client = new DefaultSuningClient(SERVER_URL, "5d051881034081888359fe086be163b9",
        "e06551b543ffca29e7686242e8a5838a");
  }

  private String appKey;
  private String appSecret;
  private DefaultSuningClient client;

  /**
   * 可以通过此接口获取苏宁授权的采购目录信息。 1、
   * 可以输入苏宁采购目录名称进行模糊查询，系统返回该商户操作权限下的采购目录名称均符合的所有采购目录信息。也可以不输入任何数据进行查询，系统返回商户操作权限下的所有采购目录信息。 2、
   * pageNo（页码）、pageSize（每页条数）必须组合查询，否则，系统默认返回第一页的10条数据。 3、返回结果分页展示，通过输入的页码可以定位到具体第几页，每页条数控制每页返回的条数。
   * http://open.suning.com/ospos/apipage/toApiMethodDetailMenu.do?interCode=suning.custom.category.query
   * 
   * @param pageSize
   * @param pageNo
   * @return
   * 
   *         "totalSize": 350,
   */
  public List<CategoryQuery> categoryQuery(CategoryQueryRequest request) {
    request.setTargetChannel("1");
    request.setPageNo(1);
    request.setPageSize(50);
    // api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
    request.setCheckParam(true);
    try {
      CategoryQueryResponse response = client.excute(request);
      // logger.info("获取苏宁授权的采购目录信息，共{}条信息", response.getSnhead().getPageTotal());
      if (response.getSnhead().getPageTotal() == 1) {
        return response.getSnbody().getCategoryQueries();
      }
      List<CategoryQuery> list = response.getSnbody().getCategoryQueries();
      for (int a = 2; a <= response.getSnhead().getPageTotal(); a++) {
        request.setPageNo(a);
        CategoryQueryResponse nResponse = client.excute(request);
        list.addAll(nResponse.getSnbody().getCategoryQueries());
      }

      return list;
    } catch (SuningApiException e) {
      e.printStackTrace();
    }

    return new ArrayList<CategoryQuery>();

  }

  /**
   * 获取商品品牌信息新接口
   * http://open.suning.com/ospos/apipage/toApiMethodDetailMenu.do?interCode=suning.custom.newbrand.query
   * categoryCode 必填
   * apiMethod=suning.custom.newbrand.query,signInfo=534ec5fdad1896342292a7731d047ea7,uuidVal=50984880-5337-4cd1-9d75-ca5558badffc,req={"sn_request":{"sn_body":{"queryNewbrand":{"categoryCode":"R9004656","pageNo":"1","pageSize":"50"}}}},resp={"sn_responseContent":{"sn_error":{"error_code":"sys.check.request-apprequesttime:error"}}}
   * 
   * @return
   */
  public List<QueryNewbrand> brandQuery(NewbrandQueryRequest request) {
    request.setPageNo(1);
    request.setPageSize(50);
    try {
      NewbrandQueryResponse response = client.excute(request);
      logger.info("获取{}商品品牌信息{}条", request.getCategoryCode(), response.getSnhead().getTotalSize());
      return response.getSnbody().getQueryNewbrand();
    } catch (SuningApiException e) {
      e.printStackTrace();
    }
    return new ArrayList<QueryNewbrand>();

  }

  /**
   * 获取商品参数模板
   * http://open.suning.com/ospos/apipage/toApiMethodDetailMenu.do?interCode=suning.custom.itemparameters.query
   * 
   * @param categoryCode
   * @return
   */
  public List<ItemparametersQuery> itemparametersQuery(String categoryCode) {
    ItemparametersQueryRequest request = new ItemparametersQueryRequest();
    request.setCategoryCode(categoryCode);
    request.setTargetChannel("1");
    request.setPageNo(1);
    request.setPageSize(50);
    // api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
    request.setCheckParam(true);
    try {
      ItemparametersQueryResponse response = client.excute(request);
      logger.info("获取{}商品参数模板{}条", request.getCategoryCode(), response.getSnhead().getTotalSize());
      return response.getSnbody().getItemparametersQueries();
    } catch (SuningApiException e) {
      e.printStackTrace();
    }

    return new ArrayList<ItemparametersQuery>();

  }

  /**
   * 通过此接口可在苏宁系统中获取国家代码。 1、仅可输入中文国家名称，可根据输入内容进行模糊查询。
   * 
   * @param nation
   * @return
   */
  public List<Nation> nation(String nation) {
    NationQueryRequest request = new NationQueryRequest();
    request.setNationName(nation);
    // api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
    request.setCheckParam(true);
    try {
      NationQueryResponse response = client.excute(request);
      System.out.println(response.getBody());
      return response.getSnbody().getNation();
    } catch (SuningApiException e) {
      e.printStackTrace();
    }
    return new ArrayList<Nation>();
  }

  /**
   * 通过此接口可获取苏宁系统中地区、省、市、区的代码。 1、国家代码可通过接口“suning.custom.nation.query”获取； 2、此接口可获取地区、省、市、区的代码及对应的名称；
   * 3、如需使用城市代码，可通过此接口获取。
   * 
   * @param code
   * @return
   */
  public List<City> city(String code) {
    CityQueryRequest request = new CityQueryRequest();
    request.setNationCode(code);
    // api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
    request.setCheckParam(true);
    try {
      CityQueryResponse response = client.excute(request);
      System.out.println(response.getBody());
      return response.getSnbody().getCity();
    } catch (SuningApiException e) {
      e.printStackTrace();
    }
    return new ArrayList<City>();
  }

  public List<QueryFreighttemplate> getFreightTemplate() {
    FreighttemplateQueryRequest request = new FreighttemplateQueryRequest();
    request.setPageNo(1);
    request.setPageSize(50);
    // api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
    request.setCheckParam(true);
    try {
      FreighttemplateQueryResponse response = client.excute(request);
      logger.info(response.getBody());
      return response.getSnbody().getQueryFreighttemplate();
    } catch (SuningApiException e) {
      e.printStackTrace();
    }

    return new ArrayList<QueryFreighttemplate>();
  }
}
