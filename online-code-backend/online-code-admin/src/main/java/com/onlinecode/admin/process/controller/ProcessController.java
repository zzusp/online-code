package com.onlinecode.admin.process.controller;

import com.onlinecode.admin.process.model.RunParam;
import com.onlinecode.admin.process.model.SysProcess;
import com.onlinecode.admin.process.service.ProcessService;
import com.onlinecode.admin.web.R;
import com.onlinecode.admin.web.page.PageTable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/process")
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @PostMapping("/list")
    public R<PageTable> list() {
        return processService.list();
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

    @DeleteMapping("/delete")
    public R<String> delete(@RequestParam Long id) {
        processService.delete(id);
        return R.ok();
    }

    @PostMapping("/run")
    public R<Object> run(@RequestBody RunParam param) {
        return R.ok(processService.run(param.getProcCode(), param.getVars()));
    }
}
