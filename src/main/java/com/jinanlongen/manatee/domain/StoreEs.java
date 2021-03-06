package com.jinanlongen.manatee.domain;

public class StoreEs {
  private String id;
  private String code;
  private String name;

  public StoreEs() {

  }

  public StoreEs(String id, String code, String name) {
    super();
    this.id = id;
    this.code = code;
    this.name = name;
  }

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

  public static StoreEs build(Store store) {
    StoreEs storeEs = new StoreEs();
    storeEs.setId(store.getId());
    storeEs.setCode(store.getCode());
    storeEs.setName(store.getName());
    return storeEs;
  }

}
