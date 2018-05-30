package com.jinanlongen.manatee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jinanlongen.manatee.domain.CategoryDoc;
import com.jinanlongen.manatee.repository.CategoryRep;
import com.jinanlongen.manatee.service.JdService;
import com.jinanlongen.manatee.service.SnService;

@RestController
public class CategoryController {
  @Autowired
  private CategoryRep categoryRep;
  @Autowired
  private JdService jd;
  @Autowired
  private SnService sn;

  @RequestMapping("category/delete")
  public void add() {

    categoryRep.deleteById("JD#color");

  }

  @RequestMapping("category/all")
  public Iterable<CategoryDoc> all() {
    return categoryRep.findAll();
  }

  @RequestMapping("category/{id}")
  public CategoryDoc all(@PathVariable String id) {
    return categoryRep.findById(id).get();
  }

  @RequestMapping("category/jd")
  public Iterable<CategoryDoc> jd() {
    jd.synAllJdCategory();
    return categoryRep.findAll();
  }

  @RequestMapping("category/sn")
  public Iterable<CategoryDoc> sn() {
    sn.synAllSnCategory();
    return categoryRep.findAll();
  }
}
