package com.alibaba.compileflow.extension.core;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaStringCompiler {

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+\\S+\\s*;");
    private static final Pattern CLASS_PATTERN = Pattern.compile("class\\s+\\S+\\s+\\{");
    private static final Pattern CLASS_EXTENDS_PATTERN = Pattern.compile("class\\s+\\S+\\s+extends");
    private static final Pattern CLASS_IMPL_PATTERN = Pattern.compile("class\\s+\\S+\\s+implements");

    /** 类全名 */
    private final String fullClassName;
    private final String sourceCode;
    /** 存放编译之后的字节码(key:类全名,value:编译之后输出的字节码) */
    private final Map<String, ByteJavaFileObject> javaFileObjectMap = new ConcurrentHashMap<>();
    /** 获取java的编译器 */
    private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    /** 存放编译过程中输出的信息 */
    private final DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<>();
    /** 执行结果（控制台输出的内容） */
    private String runResult;
    /** 编译耗时(单位ms) */
    private long compilerTakeTime;
    /** 运行耗时(单位ms) */
    private long runTakeTime;


    public JavaStringCompiler(String sourceCode) {
        this.sourceCode = sourceCode;
        this.fullClassName = getFullClassName(sourceCode);
    }

    public JavaStringCompiler(String sourceCode, String fullClassName) {
        this.sourceCode = sourceCode;
        this.fullClassName = fullClassName;
    }

    /**
     * 编译字符串源代码,编译失败在 diagnosticsCollector 中获取提示信息
     *
     * @return true:编译成功 false:编译失败
     */
    public boolean compile() {
        long startTime = System.currentTimeMillis();
        // 标准的内容管理器,更换成自己的实现，覆盖部分方法
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnosticsCollector,
            null, null);
        JavaFileManager javaFileManager = new StringJavaFileManage(standardFileManager);
        // 构造源代码对象
        JavaFileObject javaFileObject = new StringJavaFileObject(fullClassName, sourceCode);
        // 获取一个编译任务
        JavaCompiler.CompilationTask task = compiler.getTask(null, javaFileManager, diagnosticsCollector,
            null, null, Collections.singletonList(javaFileObject));
        // 设置编译耗时
        compilerTakeTime = System.currentTimeMillis() - startTime;
        return task.call();
    }

    public Object getClassInstance() {
        try {
            StringClassLoader scl = new StringClassLoader();
            Class<?> clazz = scl.findClass(fullClassName);
            scl.clearAssertionStatus();
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException
            | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行方法
     */
    public void runMethod(String methodName, Class<?> parameterType, Object parameter) throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            long startTime = System.currentTimeMillis();

            StringClassLoader scl = new StringClassLoader();
            Class<?> clazz = scl.findClass(fullClassName);
            Method main = clazz.getMethod(methodName, parameterType);

            // 调用方法
            main.invoke(Class.forName(fullClassName).getDeclaredConstructor().newInstance(), parameter);
            // 设置运行耗时
            runTakeTime = System.currentTimeMillis() - startTime;
            // 设置打印输出的内容
            runResult = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);

        } catch (IOException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取编译信息
     *
     * @return 编译信息(错误 警告)
     */
    public String getCompilerMessage() {
        StringBuilder sb = new StringBuilder();
        List<Diagnostic<? extends JavaFileObject>> diagnostics = diagnosticsCollector.getDiagnostics();
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
            sb.append(diagnostic.toString()).append("\r\n");
        }
        return sb.toString();
    }

    /**
     * 获取执行结果
     *
     * @return 控制台打印的信息
     */
    public String getRunResult() {
        return runResult;
    }

    /**
     * 获取编译耗费时长（毫秒ms）
     *
     * @return 编译耗费时长（毫秒ms）
     */
    public long getCompilerTakeTime() {
        return compilerTakeTime;
    }

    /**
     * 获取执行耗费时长（毫秒ms）
     *
     * @return 执行耗费时长（毫秒ms）
     */
    public long getRunTakeTime() {
        return runTakeTime;
    }

    /**
     * 获取类的全名称
     *
     * @param sourceCode 源码
     * @return 类的全名称
     */
    public static String getFullClassName(String sourceCode) {
        String className = "";
        Matcher matcher = PACKAGE_PATTERN.matcher(sourceCode);
        if (matcher.find()) {
            className = matcher.group().replaceFirst("package", "")
                .replace(";", "").trim() + ".";
        }
        matcher = CLASS_EXTENDS_PATTERN.matcher(sourceCode);
        if (matcher.find()) {
            className += matcher.group().replaceFirst("class", "")
                    .replace("extends", "").trim();
        }
        matcher = CLASS_IMPL_PATTERN.matcher(sourceCode);
        if (matcher.find()) {
            className += matcher.group().replaceFirst("class", "")
                    .replace("implements", "").trim();
        }
        matcher = CLASS_PATTERN.matcher(sourceCode);
        if (matcher.find()) {
            className += matcher.group().replaceFirst("class", "")
                .replace("{", "").trim();
        }
        return className;
    }

    /**
     * 自定义一个字符串的源码对象
     */
    private static class StringJavaFileObject extends SimpleJavaFileObject {
        /** 等待编译的源码字段 */
        private final String contents;

        /**
         * java源代码 => StringJavaFileObject对象 的时候使用
         *
         * @param className 类名
         * @param contents  等待编译的源码字段
         */
        public StringJavaFileObject(String className, String contents) {
            super(URI.create("string:///" + className.replace("\\.", "/")
                + Kind.SOURCE.extension), Kind.SOURCE);
            this.contents = contents;
        }

        /**
         * 字符串源码会调用该方法
         *
         * @param ignoreEncodingErrors 是否忽略编码错误
         * @return 字符序列
         */
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return contents;
        }

    }

    /**
     * 自定义一个编译之后的字节码对象
     */
    private static class ByteJavaFileObject extends SimpleJavaFileObject {
        /** 存放编译后的字节码 */
        private ByteArrayOutputStream outPutStream;

        public ByteJavaFileObject(String className, Kind kind) {
            super(URI.create("string:///" + className.replace("\\.", "/")
                + Kind.SOURCE.extension), kind);
        }

        /**
         * StringJavaFileManage 编译之后的字节码输出会调用该方法（把字节码输出到outputStream）
         *
         * @return 输出流
         */
        @Override
        public OutputStream openOutputStream() {
            outPutStream = new ByteArrayOutputStream();
            return outPutStream;
        }

        /**
         * 在类加载器加载的时候需要用到
         *
         * @return 字节数组
         */
        public byte[] getCompiledBytes() {
            return outPutStream.toByteArray();
        }
    }

    /**
     * 自定义一个JavaFileManage来控制编译之后字节码的输出位置
     */
    private class StringJavaFileManage extends ForwardingJavaFileManager<StandardJavaFileManager> {

        StringJavaFileManage(StandardJavaFileManager fileManager) {
            super(fileManager);
        }

        /**
         * 获取输出的文件对象，它表示给定位置处指定类型的指定类。
         *
         * @param location  输出位置
         * @param className 类名
         * @param kind      类型
         * @param sibling   文件对象
         * @return 文件输出对象
         */
        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
                                                   FileObject sibling) {
            ByteJavaFileObject javaFileObject = new ByteJavaFileObject(className, kind);
            javaFileObjectMap.put(className, javaFileObject);
            return javaFileObject;
        }
    }

    /**
     * 自定义类加载器, 用来加载动态的字节码
     */
    private class StringClassLoader extends ClassLoader {
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            ByteJavaFileObject fileObject = javaFileObjectMap.get(name);
            if (fileObject != null) {
                byte[] bytes = fileObject.getCompiledBytes();
                return this.defineClass(name, bytes, 0, bytes.length);
            }
            try {
                return this.loadClass(name);
            } catch (Exception e) {
                return super.findClass(name);
            }
        }
    }
}
