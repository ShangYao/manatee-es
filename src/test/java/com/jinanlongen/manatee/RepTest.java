package com.jinanlongen.manatee;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import com.jinanlongen.manatee.domain.CategoryStoreDoc;
import com.jinanlongen.manatee.domain.ParDoc;
import com.jinanlongen.manatee.repository.BrandRepo;
import com.jinanlongen.manatee.repository.CategoryStoreRep;
import com.jinanlongen.manatee.repository.ParRep;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RepTest {
  @Autowired
  ParRep parRep;
  @Autowired
  BrandRepo brand;
  @Autowired
  CategoryStoreRep categoryStoreRep;

  @Test
  public void testParRep2() {

    // brand.deleteAll();
  }

  @Test
  public void testParRep() {
    Pageable pageable = PageRequest.of(0, 1500);
    Page<ParDoc> pars = parRep.getSaleAttr(pageable);
    List<ParDoc> docs = pars.getContent();
    // Set<String> cSet = docs.stream().map(i -> i.getCategory_id()).collect(Collectors.toSet());
    Map<String, List<ParDoc>> m = docs.stream().collect(Collectors.groupingBy(i -> i.getId()));
    Set<String> cset = m.keySet();
    Iterable<CategoryStoreDoc> cs = categoryStoreRep.findAll();
    Iterator<CategoryStoreDoc> it = cs.iterator();
    List<CategoryStoreDoc> list = new ArrayList<CategoryStoreDoc>();
    while (it.hasNext()) {
      list.add(it.next());
    }
    Map<String, List<CategoryStoreDoc>> csm =
        list.stream().collect(Collectors.groupingBy(i -> i.getStore_id()));
    Set<String> storesid = csm.keySet();
    List<CategoryStoreDoc> csOfStore;
    for (String id : storesid) {
      csOfStore = csm.get(id);
      System.out.println(id + ":" + csOfStore.size());
      for (CategoryStoreDoc categoryStoreDoc : csOfStore) {
        if (cset.contains(categoryStoreDoc.getCategory_code())) {

        }
      }
    }


    System.out.println(m.size());
    System.out.println(csm.size());
    System.out.println(pars.getTotalElements());
  }
}
