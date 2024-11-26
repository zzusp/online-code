package com.alibaba.compileflow.extension.core;

import javax.tools.JavaFileObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CompilerCache {

    public static Map<String, List<JavaFileObject>> javaFileCache = new ConcurrentHashMap<>();

}
