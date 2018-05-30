package com.jinanlongen.manatee.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.jinanlongen.manatee.domain.CategoryDoc;

public interface CategoryRep extends ElasticsearchRepository<CategoryDoc, String> {

}
