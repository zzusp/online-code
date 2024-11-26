package com.codeva.admin.cron.controller;

import com.codeva.admin.cron.model.SysCron;
import com.codeva.admin.cron.service.CronService;
import com.codeva.admin.web.R;
import com.codeva.admin.web.page.PageParam;
import com.codeva.admin.web.page.PageTable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cron")
public class CronController {

    private final CronService cronService;

    public CronController(CronService cronService) {
        this.cronService = cronService;
    }

    @PostMapping("/list")
    public R<PageTable> list(@RequestBody PageParam<SysCron> pageParam) {
        return cronService.list(pageParam);
    }

    @GetMapping("/getById")
    public R<SysCron> getById(@RequestParam long id) {
        return R.ok(cronService.getById(id));
    }

    @PostMapping("/save")
    public R<String> save(@RequestBody SysCron process) {
        cronService.save(process);
        return R.ok();
    }

    @DeleteMapping("/delete")
    public R<String> delete(@RequestParam Long id) {
        cronService.delete(id);
        return R.ok();
    }

//    @PostMapping("/run")
//    public R<Object> run(@RequestBody RunParam param) {
//        return R.ok(cronService.run(param.getProcCode(), param.getVars()));
//    }

}
