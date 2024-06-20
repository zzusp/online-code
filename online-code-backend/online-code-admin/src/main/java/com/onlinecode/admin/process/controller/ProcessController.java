package com.onlinecode.admin.process.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import com.onlinecode.admin.grpc.ProcessRunProto;
import com.onlinecode.admin.process.model.JsonVars;
import com.onlinecode.admin.process.model.SysProcess;
import com.onlinecode.admin.process.service.ProcessService;
import com.onlinecode.admin.util.ProtobufUtils;
import com.onlinecode.admin.web.R;
import com.onlinecode.admin.web.page.PageParam;
import com.onlinecode.admin.web.page.PageTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

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

    @PostMapping(value = "/run")
    public void run(@RequestBody byte[] bytes, HttpServletResponse response) throws InvalidProtocolBufferException {
        // 接收字节数组，转为参数
        ProcessRunProto.RunRequest proto = ProcessRunProto.RunRequest.parseFrom(bytes);
        // 业务处理
        R<Object> r = R.ok(processService.run(proto.getProcCode(), JsonVars.parse(proto.getVars())));
        // 字节数组方式返回
        ProtobufUtils.renderBytes(response, r);
    }

    @PostMapping(value = "/runTask")
    public void runTask(@RequestBody byte[] bytes, HttpServletResponse response) throws InvalidProtocolBufferException {
        // 接收字节数组，转为参数
        ProcessRunProto.RunRequest proto = ProcessRunProto.RunRequest.parseFrom(bytes);
        // 业务处理
        R<Object> r = R.ok(processService.runTask(proto.getProcCode(), proto.getTaskCode(), JsonVars.parse(proto.getVars())));
        // 字节数组方式返回
        ProtobufUtils.renderBytes(response, r);
    }

    @PostMapping(value = "/runCmd")
    public void runCmd(@RequestBody byte[] bytes, HttpServletResponse response) throws InvalidProtocolBufferException {
        // 接收字节数组，转为参数
        ProcessRunProto.RunRequest proto = ProcessRunProto.RunRequest.parseFrom(bytes);
        if (StringUtils.isBlank(proto.getCmd())) {
            ProtobufUtils.renderBytes(response, R.error("代码不可为空"));
        }
        try {
            // 业务处理
            R<Object> r = R.ok(processService.runCmd(proto.getCmd(), JsonVars.parse(proto.getVars())));
            // 字节数组方式返回
            ProtobufUtils.renderBytes(response, r);
        } catch (Exception e) {
            ProtobufUtils.renderBytes(response, R.error("节点执行失败，错误信息：\n" + ExceptionUtils.getStackTrace(e)));
        }
    }

    @GetMapping("/autocomplete")
    public R<Object> autocomplete() {
        return R.ok(processService.autocomplete());
    }
}
