package com.jinanlongen.manatee.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import com.jd.open.api.sdk.domain.list.CategoryAttrReadService.CategoryAttr;
import com.suning.api.entity.item.ItemparametersQueryResponse.ItemparametersQuery;
import com.suning.api.entity.item.ItemparametersQueryResponse.ParOption;

@Document(indexName = "par", type = "par")
public class ParDoc {
  @Id
  private String id;
  private String code;
  private String name;
  private String par_type;// 1,关键属性(jd) ；2，不变属性(jd) ；3，可变属性(jd) ； 4，销售属性(jd) ；X，必填属性（sn）；可为空(sn)
  private String input_type;// 1,单选； 2，多选； 3，可输入
  private String unit;

  private List<Category> category;
  private StoreEs store;
  private Ecp ecp;
  private List<ParValue> values;

  /**
   * by jd
   * 
   * @param categoryAttr
   * @param store
   * @param categoryDoc
   * @return
   */

  public ParDoc parsFromJdAttrs(CategoryAttr categoryAttr, Store store, CategoryDoc categoryDoc) {
    this.id = "JD#" + categoryAttr.getCategoryId() + "#" + categoryAttr.getCategoryAttrId();
    this.code = categoryAttr.getCategoryAttrId() + "";
    this.name = categoryAttr.getAttName();
    this.input_type = categoryAttr.getInputType() + "";
    this.par_type = categoryAttr.getAttributeType() + "";
    this.ecp = new Ecp("JD", "京东");
    this.store = new StoreEs(store.getCode(), store.getName());
    this.category = Arrays.asList(new Category().setCode(categoryDoc.getCode())
        .setName(categoryDoc.getName()).setLevel(categoryDoc.getLevel())
        .setPcode(categoryDoc.getPcode()).setPath(categoryDoc.getPath()));
    return this;
  }

  public ParDoc generateSalePar(ParDoc par, Store store) {
    this.id = par.getId().replace(par.getCode(), store.getCode() + "#" + par.getCode());
    this.code = par.getCode();
    this.name = par.getName();
    this.input_type = par.getInput_type();
    this.par_type = par.getPar_type();
    this.ecp = par.getEcp();
    this.store = new StoreEs(store.getCode(), store.getName());
    this.category = par.getCategory();

    return this;
  }

  /**
   * by suning
   * 
   * @param categoryAttr
   * @param store
   * @param categoryDoc
   * @return
   */
  public ParDoc parsFromSnAttrs(ItemparametersQuery categoryAttr, Store store,
      CategoryDoc categoryDoc) {
    this.id = "SN#" + categoryAttr.getCategoryCode() + "#" + categoryAttr.getParCode();
    this.code = categoryAttr.getParCode() + "";
    this.name = categoryAttr.getParName();
    this.input_type = categoryAttr.getParType();
    this.par_type = categoryAttr.getIsMust();
    this.unit = categoryAttr.getParUnit();
    this.ecp = new Ecp("SN", "苏宁");
    if (categoryAttr.getParType().equals("3")) {
      this.values = generateParvalues(categoryAttr.getParOption());
    }
    this.store = new StoreEs(store.getCode(), store.getName());
    this.category = Arrays.asList(new Category().setCode(categoryDoc.getCode())
        .setName(categoryDoc.getName()).setLevel(categoryDoc.getLevel())
        .setPcode(categoryDoc.getPcode()).setPath(categoryDoc.getPath()));
    return this;
  }

  private List<ParValue> generateParvalues(List<ParOption> parOptions) {
    if (parOptions == null || parOptions.size() == 0) {
      return null;
    }
    List<ParValue> values = new ArrayList<ParValue>();
    ParValue parvalue;
    for (ParOption option : parOptions) {
      parvalue = new ParValue().generate(option);
      values.add(parvalue);
    }
    return values;
  }

  public List<Category> getCategory() {
    return category;
  }

  public void setCategory(List<Category> category) {
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

  public List<ParValue> getValues() {
    return values;
  }

  public void setValues(List<ParValue> values) {
    this.values = values;
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



}
