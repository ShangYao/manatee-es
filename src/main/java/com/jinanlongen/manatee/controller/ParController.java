package com.jinanlongen.manatee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jinanlongen.manatee.repository.ParRep;
import com.jinanlongen.manatee.service.JdService;
import com.jinanlongen.manatee.service.SnService;

@EnableAsync
@RestController
public class ParController {
  @Autowired
  private ParRep parRep;
  @Autowired
  private JdService jd;
  @Autowired
  private SnService sn;

  @RequestMapping("par/synAll")
  public String synAll() {
    jd.synJdCategoryAttrs();
    sn.synSnCategoryAttrs();
    return "执行中.........";
  }



  @RequestMapping("par/saleAttr")
  public String synSaleAttr() {

    jd.synSaleAttr();
    return "京东销售属性同步中........";
  }

  @RequestMapping("par/count")
  public long count() {
    return parRep.count();
  }



}
