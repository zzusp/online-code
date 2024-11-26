package com.codeva.admin.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 下划线转驼峰
     *
     * @param str 字符串
     * @return 替换结果
     */
    public static String underlineToCamel(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        Pattern pattern = Pattern.compile("_(\\w)");
        Matcher matcher = pattern.matcher(str);
        StringBuffer result = new StringBuffer();
        while (matcher.find())
            matcher.appendReplacement(result, matcher.group(1).toUpperCase());
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 连字符（-）转驼峰
     *
     * @param str 字符串
     * @return 替换结果
     */
    public static String hyphenToCamel(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        Pattern pattern = Pattern.compile("-(\\w)");
        Matcher matcher = pattern.matcher(str);
        StringBuffer result = new StringBuffer();
        while (matcher.find())
            matcher.appendReplacement(result, matcher.group(1).toUpperCase());
        matcher.appendTail(result);
        return result.toString();
    }

}
