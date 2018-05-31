package com.jinanlongen.manatee.domain;

import javax.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import com.jd.open.api.sdk.domain.list.CategoryAttrReadService.CategoryAttr;
import com.jinanlongen.manatee.enums.EcpEnum;
import com.suning.api.entity.item.ItemparametersQueryResponse.ItemparametersQuery;

@Document(indexName = "manatee", type = "par", shards = 2, replicas = 1, refreshInterval = "-1")
public class ParDoc {
  @Id
  private String id;
  private String code;
  private String name;
  private String par_type;// 1,关键属性(jd) ；2，不变属性(jd) ；3，可变属性(jd) ； 4，销售属性(jd) ；X，必填属性（sn）；可为空(sn)
  private String input_type;// 1,单选； 2，多选； 3，可输入
  private String unit;
  private EcpEnum ecp_id;
  private String category_id;

  @Override
  public String toString() {
    return "ParDoc [id=" + id + ", code=" + code + ", name=" + name + ", par_type=" + par_type
        + ", input_type=" + input_type + ", unit=" + unit + ", ecp_id=" + ecp_id + ", category_id="
        + category_id + "]";
  }

  // private String status;
  public ParDoc parsFromJdAttrs(CategoryAttr categoryAttr) {
    this.id = "JD#" + categoryAttr.getCategoryAttrId();
    this.code = categoryAttr.getCategoryAttrId() + "";
    this.name = categoryAttr.getAttName();
    this.input_type = categoryAttr.getInputType() + "";
    this.par_type = categoryAttr.getAttributeType() + "";
    this.category_id = "JD#" + categoryAttr.getCategoryId();
    this.ecp_id = EcpEnum.JD;
    return this;
  }

  public ParDoc parsFromSnAttrs(ItemparametersQuery categoryAttr) {
    this.id = "SN#" + categoryAttr.getCategoryCode() + "#" + categoryAttr.getParCode();
    this.code = categoryAttr.getParCode() + "";
    this.name = categoryAttr.getParName();
    this.input_type = categoryAttr.getParType();
    this.par_type = categoryAttr.getIsMust();
    this.category_id = "SN#" + categoryAttr.getCategoryCode();
    this.ecp_id = EcpEnum.SN;
    this.unit = categoryAttr.getParUnit();
    return this;
  }

  public String getInput_type() {
    return input_type;
  }

  public void setInput_type(String input_type) {
    this.input_type = input_type;
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

  public String getPar_type() {
    return par_type;
  }

  public void setPar_type(String par_type) {
    this.par_type = par_type;
  }



  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }



  public EcpEnum getEcp_id() {
    return ecp_id;
  }

  public void setEcp_id(EcpEnum ecp_id) {
    this.ecp_id = ecp_id;
  }

  public String getCategory_id() {
    return category_id;
  }

  public void setCategory_id(String category_id) {
    this.category_id = category_id;
  }



}
