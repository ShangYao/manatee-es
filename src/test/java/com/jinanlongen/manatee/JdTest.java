package com.jinanlongen.manatee;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.jinanlongen.manatee.service.JdService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JdTest {
  @Autowired
  JdService jd;

  @Test
  public void testSsynSaleAttr() {
    jd.synSaleAttr();
    System.out.println("ok!");
  }
}
