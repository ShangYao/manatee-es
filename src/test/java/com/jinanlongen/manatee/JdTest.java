package com.jinanlongen.manatee;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.jinanlongen.manatee.service.JdService;
import com.jinanlongen.manatee.service.SnService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JdTest {
  @Autowired
  JdService jd;
  @Autowired
  SnService sn;

  @Test
  public void testSsynSaleAttr() {
    // jd.synSaleAttr();
    System.out.println("ok!");
  }

  @Test
  public void freight() {
    // sn.synFreight();
    System.out.println("ok!");
  }
}
