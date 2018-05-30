package com.jinanlongen.manatee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jinanlongen.manatee.domain.ParDoc;
import com.jinanlongen.manatee.repository.ParRep;
import com.jinanlongen.manatee.repository.ParValueRep;
import com.jinanlongen.manatee.service.JdService;
import com.jinanlongen.manatee.service.SnService;

@RestController
public class ParController {
  @Autowired
  private ParRep parRep;
  @Autowired
  private ParValueRep parValueRep;
  @Autowired
  private JdService jd;
  @Autowired
  private SnService sn;

  @RequestMapping("par/all")
  public String syn() {
    jd.synJdCategoryAttrs();
    return "ok";
  }

  @RequestMapping("par/jd")
  public Iterable<ParDoc> all() {
    Pageable page = PageRequest.of(1, 1000);// new PageRequest(1, 1000);
    return parRep.findAll(page);
  }

  @RequestMapping("par/count")
  public long count() {
    return parRep.count();
  }

  @RequestMapping("par/sn")
  public long synSnCategoryAttrs() {
    sn.synSnCategoryAttrs();
    return parValueRep.count();
  }

  @RequestMapping("par/delete")
  public long parDelete() {
    parRep.deleteAll();
    return parRep.count();
  }

  @RequestMapping("parvalue/count")
  public long valueCount() {
    return parValueRep.count();
  }

  @RequestMapping("parvalue/delete")
  public long valueDelete() {
    parValueRep.deleteAll();
    return parValueRep.count();
  }
}
