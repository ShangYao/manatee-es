package com.jinanlongen.manatee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jinanlongen.manatee.domain.Shop;

/**
 * 类说明
 * 
 * @author shangyao
 * @date 2017年11月24日
 */
public interface ShopRep extends JpaRepository<Shop, String> {
	@Query(value = "select * from shop where platform='sn' ", nativeQuery = true)
	List<Shop> findSnShop();

	@Query(value = "select * from shop where platform='jd' ", nativeQuery = true)
	List<Shop> findJdShop();

}
