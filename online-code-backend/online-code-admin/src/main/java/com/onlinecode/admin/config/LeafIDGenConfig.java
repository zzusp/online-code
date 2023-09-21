package com.onlinecode.admin.config;

import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.exception.InitException;
import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.dao.impl.IDAllocDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LeafIDGenConfig {

    private static final Logger log = LoggerFactory.getLogger(LeafIDGenConfig.class);

    @Bean
    public IDGen idGen(DataSource dataSource) throws InitException {
        // Config Dao
        IDAllocDao dao = new IDAllocDaoImpl(dataSource);
        // Config ID Gen
        SegmentIDGenImpl idGen = new SegmentIDGenImpl();
        idGen.setDao(dao);
        if (idGen.init()) {
            log.info("Segment Service Init Successfully");
        } else {
            throw new InitException("Segment Service Init Fail");
        }
        return idGen;
    }

}
