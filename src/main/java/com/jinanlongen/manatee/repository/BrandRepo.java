package com.jinanlongen.manatee.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.jinanlongen.manatee.domain.BrandDoc;

public interface BrandRepo extends ElasticsearchRepository<BrandDoc, String> {

}
