package com.jinanlongen.manatee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.jinanlongen.manatee.domain.ParDoc;

public interface ParRep extends ElasticsearchRepository<ParDoc, String> {
  @Query(
      value = "{\"bool\":{\"must\":[{\"term\":{\"ecp.code\":\"JD\"}},{\"term\":{\"par_type.keyword\":\"4\"}}] }  }")
  Page<ParDoc> getSaleAttr(Pageable pageable);
}
