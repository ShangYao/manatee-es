package com.jinanlongen.manatee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.jinanlongen.manatee.domain.CategoryStoreDoc;

public interface CategoryStoreRep extends ElasticsearchRepository<CategoryStoreDoc, String> {
  @Query(value = "{\"bool\":{\"must\":[{\"term\":{\"ecp_id.keyword\":\"JD\"}}] }  }")
  Page<CategoryStoreDoc> getJdCategoryStore(Pageable pageable);

  @Query(value = "{\"bool\":{\"must\":[{\"term\":{\"ecp_id.keyword\":\"SN\"}}] }  }")
  Page<CategoryStoreDoc> getSnCategoryStore(Pageable pageable);
}
