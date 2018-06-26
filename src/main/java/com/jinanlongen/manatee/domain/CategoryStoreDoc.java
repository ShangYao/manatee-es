package com.jinanlongen.manatee.domain;

import javax.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import com.jd.open.api.sdk.domain.category.Category;
import com.jinanlongen.manatee.enums.EcpEnum;
import com.suning.api.entity.item.CategoryQueryResponse.CategoryQuery;

@Document(indexName = "par", type = "category_store")
public class CategoryStoreDoc {
  @Id
  private String id;
  private String store_id;
  private String store_name;
  private String category_code;
  private String category_Name;
  private EcpEnum ecp_id;

  public CategoryStoreDoc paseFromCategoryAndStore(Category jdcategory, Store store) {
    this.id = store.getId() + "#" + jdcategory.getId();
    this.store_id = store.getId();
    this.store_name = store.getName();
    this.category_code = "" + jdcategory.getId();
    this.category_Name = jdcategory.getName();
    this.ecp_id = EcpEnum.JD;
    return this;
  }

  public CategoryStoreDoc paseFromCategoryAndStore(CategoryQuery category, Store store) {
    this.id = store.getId() + "#" + category.getCategoryCode();
    this.store_id = store.getId();
    this.store_name = store.getName();
    this.category_code = "" + category.getCategoryCode();
    this.category_Name = category.getCategoryName();
    this.ecp_id = EcpEnum.SN;
    return this;
  }

  public String getCategory_Name() {
    return category_Name;
  }

  public void setCategory_Name(String category_Name) {
    this.category_Name = category_Name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getStore_id() {
    return store_id;
  }

  public void setStore_id(String store_id) {
    this.store_id = store_id;
  }

  public String getStore_name() {
    return store_name;
  }

  public void setStore_name(String store_name) {
    this.store_name = store_name;
  }



  public String getCategory_code() {
    return category_code;
  }

  public void setCategory_code(String category_code) {
    this.category_code = category_code;
  }

  public EcpEnum getEcp_id() {
    return ecp_id;
  }

  public void setEcp_id(EcpEnum ecp_id) {
    this.ecp_id = ecp_id;
  }



}
