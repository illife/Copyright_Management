package com.example.copyright.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * IO工具类
 * 完全模仿 backend 真实项目实现
 */
@Slf4j
public class IOUtil {

    private static final int BUF_SIZE = 2048;

    private IOUtil() {}

    /**
     * 读取资源文件为字符串
     * 模仿 backend 实现
     */
    public static String readResourceAsString(String resource) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream in = classLoader.getResourceAsStream(resource)) {
            return readAsString(in);
        } catch (IOException ex) {
            log.error("Error reading resource: {}", resource, ex);
            return null;
        }
    }

    /**
     * 读取 InputStream 为字符串
     */
    public static String readAsString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(inputStream, baos);
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * 复制流
     */
    public static void copy(InputStream is, OutputStream os) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(is);
             BufferedOutputStream bos = new BufferedOutputStream(os)) {
            byte[] buf = new byte[BUF_SIZE];
            int n;
            while ((n = bis.read(buf)) != -1) {
                bos.write(buf, 0, n);
            }
            bos.flush();
        }
    }
}
