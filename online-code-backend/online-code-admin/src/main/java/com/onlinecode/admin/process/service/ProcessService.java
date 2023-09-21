package com.onlinecode.admin.process.service;

import com.onlinecode.admin.process.model.SysProcess;
import com.onlinecode.admin.web.R;
import com.onlinecode.admin.web.page.PageParam;
import com.onlinecode.admin.web.page.PageTable;

import java.util.Map;

/**
 * @author 孙鹏
 * @description 流程服务
 * @date Created in 17:57 2023/2/24
 * @modified By
 */
public interface ProcessService {

    /**
     * 分页查询
     */
    R<PageTable> list(PageParam<SysProcess> pageParam);

    /**
     * 根据id查询流程信息
     */
    SysProcess getById(long id);

    /**
     * 根据id查询流程信息，包含节点信息
     */
    SysProcess getInfoWithTaskById(long id);

    /**
     * 保存流程
     *
     * @param process 流程对象
     */
    void save(SysProcess process);

    /**
     * 拷贝流程
     *
     * @param process 流程对象
     */
    void copy(SysProcess process);

    /**
     * 删除
     *
     * @param id 流程ID
     */
    void delete(Long id);

    /**
     * 运行
     *
     * @param code   流程编码
     * @param params 运行参数
     * @return 结果
     */
    Object run(String code, Map<String, Object> params);

    /**
     * 运行单个节点
     *
     * @param procCode 流程编码
     * @param taskCode 节点编码
     * @param params   运行参数
     * @return 结果
     */
    Object runTask(String procCode, String taskCode, Map<String, Object> params);

    /**
     * 运行命令
     *
     * @param cmd    命令代码
     * @param params 运行参数
     * @return 结果
     */
    Object runCmd(String cmd, Map<String, Object> params);

    /**
     * 自动补全
     *
     * @return 结果
     */
    Object autocomplete();

}
