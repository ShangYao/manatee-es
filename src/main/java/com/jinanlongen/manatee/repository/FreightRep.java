package com.jinanlongen.manatee.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.jinanlongen.manatee.domain.FreightDoc;

public interface FreightRep extends ElasticsearchRepository<FreightDoc, String> {

}
