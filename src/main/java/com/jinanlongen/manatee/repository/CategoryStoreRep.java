package com.jinanlongen.manatee.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.jinanlongen.manatee.domain.CategoryStoreDoc;

public interface CategoryStoreRep extends ElasticsearchRepository<CategoryStoreDoc, String> {

}
