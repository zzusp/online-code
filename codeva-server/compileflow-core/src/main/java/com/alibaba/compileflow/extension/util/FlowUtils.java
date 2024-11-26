package com.alibaba.compileflow.extension.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlowUtils {

    private static final Pattern ID_PATTERN = Pattern.compile("<process id=\"(.*?)\"");
    private static final Pattern BPMN_ELEMENT_PATTERN = Pattern.compile("<bpmndi:BPMNPlane id=\"BpmnPlane_1\" bpmnElement=\"(.*?)\"");
    private static final Pattern DATA_ACTION_PATTERN = Pattern.compile("dataAction=\"(.*?)\"");
    private static final Pattern CONDITION_PATTERN = Pattern.compile("<conditionExpression xsi:type=\"tFormalExpression\">(.*?)</conditionExpression>");

    /**
     * 过滤bpmn字符串中无用内容
     *
     * @param bpmn bpmn字符串
     * @return 结果
     */
    public static String getFlowStr(String bpmn) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(bpmn)) {
            return null;
        }
        // 过滤掉不支持的dataAction属性
        Matcher matcher = DATA_ACTION_PATTERN.matcher(bpmn);
        while (matcher.find()) {
            bpmn = bpmn.replace(matcher.group(), "");
        }
        // 过滤掉不需要存储的conditionExpression标签
        matcher = CONDITION_PATTERN.matcher(bpmn);
        while (matcher.find()) {
            bpmn = bpmn.replace(matcher.group(), "");
        }
        // 过滤掉不需要解析bpmndi
        bpmn = bpmn.substring(0, bpmn.indexOf("<bpmndi:BPMNDiagram")) + "</definitions>";
        return bpmn;
    }

    /**
     * 替换bpmn字符串中的流程编码
     *
     * @param bpmn     bpmn字符串
     * @param procCode 流程编码
     * @return 结果
     */
    public static String replaceProcCode(String bpmn, String procCode) {
        if (StringUtils.isEmpty(bpmn)) {
            return null;
        }
        // 替换的id属性
        Matcher matcher = ID_PATTERN.matcher(bpmn);
        while (matcher.find()) {
            bpmn = bpmn.replace(matcher.group(), "<process id=\"" + procCode + "\"");
        }
        // 替换的bpmnElement属性
        matcher = BPMN_ELEMENT_PATTERN.matcher(bpmn);
        while (matcher.find()) {
            bpmn = bpmn.replace(matcher.group(), "<bpmndi:BPMNPlane id=\"BpmnPlane_1\" bpmnElement=\"" + procCode + "\"");
        }
        return bpmn;
    }
}
