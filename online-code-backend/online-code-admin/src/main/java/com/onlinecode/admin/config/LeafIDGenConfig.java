package com.onlinecode.admin.config;

import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.dao.impl.IDAllocDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LeafIDGenConfig {

    @Bean
    public IDGen idGen(DataSource dataSource) {
        // Config Dao
        IDAllocDao dao = new IDAllocDaoImpl(dataSource);

        // Config ID Gen
        SegmentIDGenImpl idGen = new SegmentIDGenImpl();
        idGen.setDao(dao);
        if (idGen.init()) {
//            logger.info("Segment Service Init Successfully");
        } else {
//            throw new InitException("Segment Service Init Fail");
        }
        return idGen;
    }



}
