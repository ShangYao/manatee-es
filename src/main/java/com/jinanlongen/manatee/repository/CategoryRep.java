package com.jinanlongen.manatee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.jinanlongen.manatee.domain.CategoryDoc;

public interface CategoryRep extends ElasticsearchRepository<CategoryDoc, String> {
  @Query(
      value = "{\"bool\":{\"must\":[{\"term\":{\"ecp.code.keyword\":\"JD\"}},{\"term\":{\"is_leaf\":true}}] }  }")
  Page<CategoryDoc> getJdLeafCategory(Pageable pageable);
}
