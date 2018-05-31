package com.jinanlongen.manatee.domain;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import com.jd.open.api.sdk.domain.list.CategoryAttrValueReadService.CategoryAttrValueJos;
import com.jinanlongen.manatee.enums.EcpEnum;
import com.suning.api.entity.item.ItemparametersQueryResponse.ItemparametersQuery;
import com.suning.api.entity.item.ItemparametersQueryResponse.ParOption;

@Document(indexName = "manatee", type = "par_value", shards = 2, replicas = 1,
    refreshInterval = "-1")
public class ParValueDoc {
  @Id
  private String id;
  private String name;
  private String code;
  private EcpEnum ecp_id;
  private String par_id;
  private int idx;
  private String store_id;

  @Override
  public String toString() {
    return "ParValueDoc [id=" + id + ", name=" + name + ", code=" + code + ", ecp_id=" + ecp_id
        + ", par_id=" + par_id + ", idx=" + idx + "]";
  }

  public ParValueDoc parseFromJdAttrsValue(CategoryAttrValueJos attrValue) {
    this.id = "JD#" + attrValue.getId();
    this.name = attrValue.getValue();
    this.code = attrValue.getId() + "";
    this.ecp_id = EcpEnum.JD;
    this.par_id = "JD#" + attrValue.getAttributeId();
    this.idx = attrValue.getIndexId();
    return this;

  }

  public ParValueDoc parseFromJdAttrsValue(CategoryAttrValueJos attrValue, String storeId) {
    this.parseFromJdAttrsValue(attrValue);
    this.store_id = storeId;
    return this;

  }

  public List<ParValueDoc> parsFromSnAttrs(ItemparametersQuery categoryAttr) {
    ParValueDoc value;
    List<ParValueDoc> values = new ArrayList<ParValueDoc>();
    List<ParOption> option = categoryAttr.getParOption();
    for (ParOption parOption : option) {
      value = new ParValueDoc();
      value.setId("SN#" + categoryAttr.getCategoryCode() + "#"
          + Base64.getEncoder().encodeToString(parOption.getParOptionDesc().getBytes()));
      value.setName(parOption.getParOptionDesc());
      value.setCode(parOption.getParOptionCode());
      value.setEcp_id(EcpEnum.SN);
      value.setPar_id("SN#" + categoryAttr.getCategoryCode() + "#" + categoryAttr.getParCode());
      values.add(value);
    }
    return values;
  }



  public String getStore_id() {
    return store_id;
  }

  public void setStore_id(String store_id) {
    this.store_id = store_id;
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

  public EcpEnum getEcp_id() {
    return ecp_id;
  }

  public void setEcp_id(EcpEnum ecp_id) {
    this.ecp_id = ecp_id;
  }

  public String getPar_id() {
    return par_id;
  }

  public void setPar_id(String par_id) {
    this.par_id = par_id;
  }

  public int getIdx() {
    return idx;
  }

  public void setIdx(int idx) {
    this.idx = idx;
  }

}
