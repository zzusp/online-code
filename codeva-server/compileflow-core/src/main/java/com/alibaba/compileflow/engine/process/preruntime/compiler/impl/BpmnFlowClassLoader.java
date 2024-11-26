package com.alibaba.compileflow.engine.process.preruntime.compiler.impl;

import com.alibaba.compileflow.engine.common.CompileFlowException;

import java.net.*;
import java.util.*;

public class BpmnFlowClassLoader extends URLClassLoader {
    private static volatile BpmnFlowClassLoader instance = null;

    public BpmnFlowClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public static BpmnFlowClassLoader getInstance() {
        if (instance == null) {
            synchronized (BpmnFlowClassLoader.class) {
                if (instance == null) {
                    try {
                        URL url = new URL("file:///" + CompileConstants.FLOW_COMPILE_CLASS_DIR);
                        List<URL> urls = new ArrayList<>();
                        urls.add(url);
                        URLClassLoader parent = (URLClassLoader) Thread.currentThread().getContextClassLoader();
                        urls.addAll(Arrays.asList(parent.getURLs()));
                        instance = new BpmnFlowClassLoader(urls.toArray(new URL[]{}), parent);
                    } catch (MalformedURLException var3) {
                        throw new CompileFlowException(var3);
                    }
                }
            }
        }
        return instance;
    }

    public void clearCache() {
        instance = null;
    }

}
