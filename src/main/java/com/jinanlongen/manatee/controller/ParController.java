package com.jinanlongen.manatee.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jinanlongen.manatee.domain.ParDoc;
import com.jinanlongen.manatee.repository.ParRep;
import com.jinanlongen.manatee.service.JdService;
import com.jinanlongen.manatee.service.SnService;

@EnableAsync
@RestController
public class ParController {
  private Logger logger = LoggerFactory.getLogger(ParController.class);
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
    return "synAll执行中.........";
  }

  @RequestMapping("par/synSn")
  public String synSn() {
    sn.synSnCategoryAttrs();
    return "synSn执行中.........";
  }

  @RequestMapping("par/synJd")
  public String synJd() {
    jd.synJdCategoryAttrs();
    return "synJd执行中.........";
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

  @RequestMapping("par/deleteAll")
  public long deleteAll() {
    parRep.deleteAll();
    return parRep.count();
  }

  @RequestMapping("par/getSaleAttr")
  public List<ParDoc> getSaleAttr() {
    Pageable pageable = PageRequest.of(0, 1500);
    return parRep.getSaleAttr(pageable).getContent();
  }

  @RequestMapping("par/{id}")
  public ParDoc getPar(@PathVariable String id) {
    id = id.replace("+", "#");
    logger.info("get par id:{}", id);
    return parRep.findById(id).get();
  }



}
