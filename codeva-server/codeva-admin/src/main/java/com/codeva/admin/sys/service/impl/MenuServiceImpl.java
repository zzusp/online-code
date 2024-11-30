package com.codeva.admin.sys.service.impl;

import com.codeva.admin.constant.RedisKey;
import com.codeva.admin.sys.dao.MenuMapper;
import com.codeva.admin.sys.model.SysMenu;
import com.codeva.admin.sys.service.MenuService;
import com.codeva.admin.sys.service.RedisCacheService;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 孙鹏
 * @description 菜单服务实现
 * @date Created in 17:26 2024/11/30
 * @modified By
 */
@Service
public class MenuServiceImpl implements MenuService {
    private static final String ALL_MENU_CACHE = RedisKey.BUSINESS_CACHE + "all_menu";

    private final SqlSessionFactory sqlSessionFactory;
    private final RedisCacheService redisCacheService;

    public MenuServiceImpl(DataSource dataSource, RedisCacheService redisCacheService) {
        this.redisCacheService = redisCacheService;
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(MenuMapper.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    @Override
    public List<SysMenu> listAll() {
        return redisCacheService.cacheList(ALL_MENU_CACHE, (data) -> {
            // 缓存未找到，查询数据库
            try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
                return sqlSession.getMapper(MenuMapper.class).getAllMenu();
            }
        }, SysMenu.class, 10, 24 * 60 * 60, TimeUnit.SECONDS);
    }
}
