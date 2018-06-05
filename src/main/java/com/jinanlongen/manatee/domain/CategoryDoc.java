package com.jinanlongen.manatee.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import com.jd.open.api.sdk.domain.category.Category;
import com.suning.api.entity.item.CategoryQueryResponse.CategoryQuery;

@Document(indexName = "partest", type = "category")
public class CategoryDoc {
  @Id
  private String id;
  private Ecp ecp;
  private String name;
  private String code;
  private String pcode;
  private int level;
  private int idx;
  private boolean is_leaf;
  private String status;
  private String path;

  public CategoryDoc parseFromJdCategory(Category category) {
    this.id = "JD#" + category.getId();
    this.name = category.getName();
    this.ecp = new Ecp("JD", "JD", "京东");
    this.code = category.getId() + "";
    this.pcode = category.getFid() == 0 ? null : "" + category.getFid();
    this.level = category.getLev();
    this.idx = category.getIndexId();
    this.status = category.getStatus();
    this.is_leaf = (category.isParent() == true) ? false : true;
    return this;

  }


  public CategoryDoc parseFromSnCategory(CategoryQuery category) {
    this.id = "SN#" + category.getCategoryCode();
    this.name = category.getCategoryName();
    this.code = category.getCategoryCode();
    this.ecp = new Ecp("SN", "SN", "苏宁");
    this.level = Integer.parseInt(category.getGrade());
    this.is_leaf = category.getIsBottom().equals("X") ? true : false;
    this.path = category.getDescPath();
    return this;
  }



  public Ecp getEcp() {
    return ecp;
  }

  public void setEcp(Ecp ecp) {
    this.ecp = ecp;
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



  public String getPcode() {
    return pcode;
  }

  public void setPcode(String pcode) {
    this.pcode = pcode;
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



  public boolean isIs_leaf() {
    return is_leaf;
  }

  public void setIs_leaf(boolean is_leaf) {
    this.is_leaf = is_leaf;
  }



}
