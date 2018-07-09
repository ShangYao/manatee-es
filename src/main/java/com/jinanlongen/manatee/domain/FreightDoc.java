package com.jinanlongen.manatee.domain;

import org.springframework.data.elasticsearch.annotations.Document;
import com.suning.api.entity.sale.FreighttemplateQueryResponse.QueryFreighttemplate;

@Document(indexName = "par", type = "freight")
public class FreightDoc {
  private String id;
  private String code;
  private String name;
  private Ecp ecp;
  private StoreEs store;

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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Ecp getEcp() {
    return ecp;
  }

  public void setEcp(Ecp ecp) {
    this.ecp = ecp;
  }

  public StoreEs getStore() {
    return store;
  }

  public void setStore(StoreEs store) {
    this.store = store;
  }

  public FreightDoc OfSn(QueryFreighttemplate template, Store store) {
    this.code = template.getFreighttemplateid();
    this.name = template.getFreighttemplatename();
    this.store = new StoreEs(store.getId(), store.getCode(), store.getName());
    this.id = store.getId() + "#" + this.code;
    this.ecp = new Ecp("SN", "SN", "苏宁");
    return this;
  }

}
