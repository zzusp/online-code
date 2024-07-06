package com.onlinecode.admin.cron.service;

import com.onlinecode.admin.cron.model.SysCron;
import com.onlinecode.admin.web.R;
import com.onlinecode.admin.web.page.PageParam;
import com.onlinecode.admin.web.page.PageTable;

import java.util.List;

/**
 * @author 孙鹏
 * @description 定时任务服务
 * @date Created in 10:17 2024/6/5
 * @modified By
 */
public interface CronService {

    /**
     * 分页查询
     */
    R<PageTable> list(PageParam<SysCron> pageParam);

    /**
     * 查询所有定时任务信息
     *
     * @return 所有定时任务信息
     */
    List<SysCron> listAll();

    /**
     * 根据id查询定时任务信息
     */
    SysCron getById(long id);

    /**
     * 保存定时任务
     *
     * @param cron 定时任务对象
     */
    void save(SysCron cron);

    /**
     * 删除
     *
     * @param id 定时任务ID
     */
    void delete(Long id);
    
}
