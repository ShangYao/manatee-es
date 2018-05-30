package com.jinanlongen.manatee.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.jinanlongen.manatee.domain.ParDoc;

public interface ParRep extends ElasticsearchRepository<ParDoc, String> {

}
