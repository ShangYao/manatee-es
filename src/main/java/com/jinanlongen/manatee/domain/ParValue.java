package com.jinanlongen.manatee.domain;

import com.jd.open.api.sdk.domain.list.CategoryAttrValueReadService.CategoryAttrValueJos;
import com.suning.api.entity.item.ItemparametersQueryResponse.ParOption;

public class ParValue {
  private String code;
  private String name;
  private int idx;

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



  public int getIdx() {
    return idx;
  }

  public void setIdx(int idx) {
    this.idx = idx;
  }

  /**
   * by suning
   * 
   * @param option
   * @return ParValue
   */
  public ParValue generate(ParOption option) {
    this.code = option.getParOptionCode();
    this.name = option.getParOptionDesc();
    return this;
  }

  /**
   * by jd
   * 
   * @param categoryAttrValueJos
   * @return
   */
  public ParValue generate(CategoryAttrValueJos categoryAttrValueJos) {
    this.code = categoryAttrValueJos.getId() + "";
    this.name = categoryAttrValueJos.getValue();
    this.idx = categoryAttrValueJos.getIndexId();

    return this;
  }

}
