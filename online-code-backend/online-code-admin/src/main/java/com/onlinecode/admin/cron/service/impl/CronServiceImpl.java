package com.onlinecode.admin.cron.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageInterceptor;
import com.onlinecode.admin.constant.FlowConstants;
import com.onlinecode.admin.cron.dao.CronMapper;
import com.onlinecode.admin.cron.model.SysCron;
import com.onlinecode.admin.cron.service.CronService;
import com.onlinecode.admin.enums.IdKeyEnum;
import com.onlinecode.admin.enums.StatusEnum;
import com.onlinecode.admin.exception.BusinessException;
import com.onlinecode.admin.process.service.ProcessService;
import com.onlinecode.admin.util.JsonUtils;
import com.onlinecode.admin.web.R;
import com.onlinecode.admin.web.page.PageParam;
import com.onlinecode.admin.web.page.PageTable;
import com.sankuai.inf.leaf.IDGen;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 孙鹏
 * @description 定时任务服务
 * @date Created in 10:17 2024/6/5
 * @modified By
 */
@Service
public class CronServiceImpl implements CronService {

    private static final Logger log = LoggerFactory.getLogger(CronServiceImpl.class);

    private static final String LOCK_KEY = FlowConstants.BUSINESS_LOCK + "sys_cron:";

    private final SqlSessionFactory sqlSessionFactory;
    private final IDGen idGen;
    private final RedissonClient redissonClient;
    private final CronTaskRegistrar cronTaskRegistrar;
    private final ProcessService processService;

    public CronServiceImpl(DataSource dataSource, IDGen idGen, PageInterceptor pageInterceptor,
                           RedissonClient redissonClient, CronTaskRegistrar cronTaskRegistrar,
                           ProcessService processService) {
        this.idGen = idGen;
        this.redissonClient = redissonClient;
        this.cronTaskRegistrar = cronTaskRegistrar;
        this.processService = processService;
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(CronMapper.class);
        configuration.addInterceptor(pageInterceptor);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    @Override
    public R<PageTable> list(PageParam<SysCron> pageParam) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
                PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            }
            if (pageParam.getParam() == null) {
                pageParam.setParam(new SysCron());
            }
            String cronCode = pageParam.getParam().getCronCode();
            String cronName = pageParam.getParam().getCronName();
            List<SysCron> list = sqlSession.getMapper(CronMapper.class).getAllCron(cronCode, cronName);
            PageInfo<SysCron> pageInfo = new PageInfo<>(list);
            return R.ok(PageTable.page(pageInfo.getTotal(), pageInfo.getList()));
        }
    }

    @Override
    public List<SysCron> listAll() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            return sqlSession.getMapper(CronMapper.class).getAllCron(null, null);
        }
    }

    @Override
    public SysCron getById(long id) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            return sqlSession.getMapper(CronMapper.class).getById(id);
        }
    }

    @Override
    public void save(SysCron cron) {
        String cronCode = cron.getCronCode();
        if (StringUtils.isEmpty(cronCode)) {
            throw new BusinessException("编码不可为空");
        }
        // 分布式业务锁
        RLock lock = redissonClient.getLock(LOCK_KEY + cronCode);
        try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
            // 获取锁
            boolean locked = lock.tryLock(10, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("服务器忙，请稍后重试");
            }
            // 业务处理
            if (cron.getId() == null) {
                if (sqlSession.getMapper(CronMapper.class).getByCronCode(cronCode) != null) {
                    throw new BusinessException("编码已存在");
                }
                cron.setId(idGen.get(IdKeyEnum.SYS_CRON.getCode()).getId());
                cron.setCreateTime(LocalDateTime.now());
                sqlSession.getMapper(CronMapper.class).insert(cron);
            } else {
                cron.setUpdateTime(LocalDateTime.now());
                sqlSession.getMapper(CronMapper.class).update(cron);
            }
            if (StringUtils.isBlank(cron.getProcCode())) {
                throw new BusinessException("流程代码不可为空");
            }
            // 注册定时任务并启动
            if (StatusEnum.ENABLED.equals(cron.getStatus())) {
                cronTaskRegistrar.startTask(cronCode, cron.getCronTxt(), () -> {
                    processService.run(cron.getProcCode(), JsonUtils.convertJsonToMap(cron.getExecuteParam()));
                });
            } else {
                cronTaskRegistrar.stopTask(cronCode);
            }
            sqlSession.commit();
        } catch (Exception e) {
            log.error("保存定时任务失败，错误信息：{}", e.getMessage(), e);
            throw new BusinessException("保存定时任务失败，错误信息：" + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(Long id) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
            SysCron cron = sqlSession.getMapper(CronMapper.class).getById(id);
            if (cron == null) {
                return;
            }
            String cronCode = cron.getCronCode();
            // 分布式业务锁
            RLock lock = redissonClient.getLock(LOCK_KEY + cronCode);
            try {
                // 获取锁
                boolean locked = lock.tryLock(10, TimeUnit.SECONDS);
                if (!locked) {
                    throw new BusinessException("服务器忙，请稍后重试");
                }
                // 业务操作
                cronTaskRegistrar.stopTask(cron.getCronCode());
                sqlSession.getMapper(CronMapper.class).delete(id);
                sqlSession.commit();
            } catch (Exception e) {
                log.error("删除定时任务失败，错误信息：{}", e.getMessage(), e);
            } finally {
                lock.unlock();
            }
        }
    }
}
