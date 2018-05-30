package com.jinanlongen.manatee.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.jinanlongen.manatee.domain.ParValueDoc;

public interface ParValueRep extends ElasticsearchRepository<ParValueDoc, String> {

}
