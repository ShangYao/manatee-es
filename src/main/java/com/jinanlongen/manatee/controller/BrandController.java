package com.jinanlongen.manatee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jinanlongen.manatee.service.JdService;
import com.jinanlongen.manatee.service.SnService;

@EnableAsync
@RestController
public class BrandController {
  @Autowired
  private JdService jd;
  @Autowired
  private SnService sn;

  @RequestMapping("brand/jd")
  public String synJd() {
    jd.synBrand();
    return "synJdBrand执行中.........";
  }

  @RequestMapping("brand/sn")
  public String synSn() {
    sn.synBrand();
    return "synSnBrand执行中.........";
  }
}
