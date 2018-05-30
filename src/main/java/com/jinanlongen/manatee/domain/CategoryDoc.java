package com.jinanlongen.manatee.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import com.jd.open.api.sdk.domain.category.Category;
import com.jinanlongen.manatee.enums.EcpEnum;
import com.suning.api.entity.item.CategoryQueryResponse.CategoryQuery;

@Document(indexName = "manatee", type = "category", shards = 2, replicas = 1,
    refreshInterval = "-1")
public class CategoryDoc {
  @Id
  private String id;
  private EcpEnum ecp_id;
  private String name;
  private String code;
  private String fid;
  private int level;
  private int idx;
  private boolean is_leaf;
  private String status;
  private String path;
  @Field(fielddata = false)
  private String shopId;

  public CategoryDoc parseFromJdCategory(Category category) {
    this.id = "JD#" + category.getId();
    this.ecp_id = EcpEnum.JD;
    this.name = category.getName();
    this.code = category.getId() + "";
    this.fid = category.getFid() == 0 ? null : "JD#" + category.getFid();
    this.level = category.getLev();
    this.idx = category.getIndexId();
    this.status = category.getStatus();
    this.is_leaf = (category.isParent() == true) ? false : true;
    return this;

  }

  public CategoryDoc parseFromJdCategory(Category category, String shopId) {
    this.parseFromJdCategory(category);
    this.setShopId(shopId);
    return this;

  }

  public CategoryDoc parseFromSnCategory(CategoryQuery category) {
    this.id = "SN#" + category.getCategoryCode();
    this.ecp_id = EcpEnum.SN;
    this.name = category.getCategoryName();
    this.code = category.getCategoryCode();
    // this.fid = category.getFid() == 0 ? null : "JD#" + category.getFid();
    this.level = Integer.parseInt(category.getGrade());
    // this.idx = category.get;
    // this.status = category.getStatus();
    this.is_leaf = category.getIsBottom().equals("X") ? true : false;
    this.path = category.getDescPath();
    return this;
  }

  public CategoryDoc parseFromSnCategory(CategoryQuery category, String shopId) {
    this.parseFromSnCategory(category);
    this.setShopId(shopId);
    return this;
  }


  public String getShopId() {
    return shopId;
  }

  public void setShopId(String shopId) {
    this.shopId = shopId;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }



  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getFid() {
    return fid;
  }

  public void setFid(String fid) {
    this.fid = fid;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public int getIdx() {
    return idx;
  }

  public void setIdx(int idx) {
    this.idx = idx;
  }

  public EcpEnum getEcp_id() {
    return ecp_id;
  }

  public void setEcp_id(EcpEnum ecp_id) {
    this.ecp_id = ecp_id;
  }

  public boolean isIs_leaf() {
    return is_leaf;
  }

  public void setIs_leaf(boolean is_leaf) {
    this.is_leaf = is_leaf;
  }



}
