package com.jinanlongen.manatee.domain;

import javax.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import com.jd.open.api.sdk.response.list.VenderBrandPubInfo;
import com.suning.api.entity.custom.NewbrandQueryResponse.QueryNewbrand;

@Document(indexName = "par", type = "ecp_brand")
public class BrandDoc {
  @Id
  private String id;
  private String code;
  private String name;

  private Category category;
  private StoreEs store;
  private Ecp ecp;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public StoreEs getStore() {
    return store;
  }

  public void setStore(StoreEs store) {
    this.store = store;
  }

  public Ecp getEcp() {
    return ecp;
  }

  public void setEcp(Ecp ecp) {
    this.ecp = ecp;
  }

  public static BrandDoc build(VenderBrandPubInfo venderBrandPubInfo, Store store) {
    BrandDoc brand = new BrandDoc();
    brand.setId(store.getId() + "#" + venderBrandPubInfo.getErpBrandId());
    brand.setCode(String.valueOf(venderBrandPubInfo.getErpBrandId()));
    brand.setName(venderBrandPubInfo.getBrandName());
    brand.setEcp(new Ecp("JD", "JD", "京东"));
    brand.setStore(StoreEs.build(store));
    return brand;
  }

  public static BrandDoc build(QueryNewbrand queryNewbrand, Store store,
      CategoryStoreDoc categoryStoreDoc) {
    BrandDoc brand = new BrandDoc();
    brand.setId(
        store.getId() + "#" + queryNewbrand.getCategoryCode() + "#" + queryNewbrand.getBrandCode());
    brand.setCode(queryNewbrand.getBrandCode());
    brand.setName(queryNewbrand.getBrandName());
    brand.setEcp(new Ecp("SN", "SN", "苏宁"));
    brand.setStore(StoreEs.build(store));
    brand.setCategory(Category.build(categoryStoreDoc));
    return brand;
  }

}
