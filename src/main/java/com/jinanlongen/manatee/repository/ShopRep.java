package com.jinanlongen.manatee.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.jinanlongen.manatee.domain.Store;

/**
 * 类说明
 * 
 * @author shangyao
 * @date 2017年11月24日
 */
public interface ShopRep extends JpaRepository<Store, String> {
  @Query(value = "select * from stores where platform='sn' ", nativeQuery = true)
  List<Store> findSnShop();

  @Query(value = "select * from stores where platform='jd' ", nativeQuery = true)
  List<Store> findJdShop();

}
