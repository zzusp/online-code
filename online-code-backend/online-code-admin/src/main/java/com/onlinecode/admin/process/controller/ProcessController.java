package com.onlinecode.admin.process.controller;

import com.onlinecode.admin.process.model.RunParam;
import com.onlinecode.admin.process.model.SysProcess;
import com.onlinecode.admin.process.service.ProcessService;
import com.onlinecode.admin.web.R;
import com.onlinecode.admin.web.page.PageParam;
import com.onlinecode.admin.web.page.PageTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/process")
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @PostMapping("/list")
    public R<PageTable> list(@RequestBody PageParam<SysProcess> pageParam) {
        return processService.list(pageParam);
    }

    @GetMapping("/getById")
    public R<SysProcess> getById(@RequestParam long id) {
        return R.ok(processService.getById(id));
    }

    @GetMapping("/getInfoWithTaskById")
    public R<SysProcess> getInfoWithTaskById(@RequestParam long id) {
        return R.ok(processService.getInfoWithTaskById(id));
    }

    @PostMapping("/save")
    public R<String> save(@RequestBody SysProcess process) {
        processService.save(process);
        return R.ok();
    }

    @PostMapping("/copy")
    public R<String> copy(@RequestBody SysProcess process) {
        processService.copy(process);
        return R.ok();
    }

    @DeleteMapping("/delete")
    public R<String> delete(@RequestParam Long id) {
        processService.delete(id);
        return R.ok();
    }

    @PostMapping("/run")
    public R<Object> run(@RequestBody RunParam param) {
        return R.ok(processService.run(param.getProcCode(), param.getVars()));
    }

    @PostMapping("/runTask")
    public R<Object> runTask(@RequestBody RunParam param) {
        return R.ok(processService.runTask(param.getProcCode(), param.getTaskCode(), param.getVars()));
    }

    @PostMapping("/runCmd")
    public R<Object> runCmd(@RequestBody RunParam param) {
        if (StringUtils.isBlank(param.getCmd())) {
            return R.error("代码不可为空");
        }
        try {
            return R.ok(processService.runCmd(param.getCmd(), param.getVars()));
        } catch (Exception e) {
            return R.error("节点执行失败，错误信息：\n" + ExceptionUtils.getStackTrace(e));
        }
    }

    @GetMapping("/autocomplete")
    public R<Object> autocomplete() {
        return R.ok(processService.autocomplete());
    }
}
