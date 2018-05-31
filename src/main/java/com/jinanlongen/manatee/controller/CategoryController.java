package com.jinanlongen.manatee.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jinanlongen.manatee.domain.CategoryDoc;
import com.jinanlongen.manatee.repository.CategoryRep;
import com.jinanlongen.manatee.service.JdService;
import com.jinanlongen.manatee.service.SnService;

@EnableAsync
@RestController
public class CategoryController {
  private Logger logger = LoggerFactory.getLogger(CategoryController.class);
  @Autowired
  private CategoryRep categoryRep;
  @Autowired
  private JdService jd;
  @Autowired
  private SnService sn;



  @RequestMapping("category/hello")
  public String hello() {
    return "hello!";
  }

  @RequestMapping("category/synAll")
  public String synAll() {
    logger.info(Thread.currentThread().getName() + "----------main：>");
    jd.synAllJdCategory();
    sn.synAllSnCategory();
    return "执行中........";
  }



  @RequestMapping("category/{id}")
  public CategoryDoc all(@PathVariable String id) {
    return categoryRep.findById(id).get();
  }

  @RequestMapping("category/count")
  public long count() {
    return categoryRep.count();
  }


}
