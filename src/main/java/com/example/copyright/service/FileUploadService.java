package com.example.copyright.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 文件上传服务
 * 支持本地存储和可选的 IPFS 上传
 */
@Slf4j
@Service
public class FileUploadService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${ipfs.api-url:http://127.0.0.1:5001/api/v0}")
    private String ipfsApiUrl;

    @Value("${ipfs.gateway.url:https://ipfs.io/ipfs/}")
    private String ipfsGatewayUrl;

    @Value("${ipfs.enabled:false}")
    private boolean ipfsEnabled;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 上传文件并返回哈希值
     * 如果启用 IPFS，上传到 IPFS 并返回 CID
     * 否则，存储到本地并计算 SHA-256 哈希
     */
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 如果启用 IPFS，尝试上传到 IPFS
        if (ipfsEnabled) {
            try {
                return uploadToIPFS(file);
            } catch (Exception e) {
                log.warn("IPFS 上传失败，转为本地存储: {}", e.getMessage());
                // IPFS 失败，fallback 到本地存储
                return uploadToLocal(file);
            }
        } else {
            // 直接上传到本地
            return uploadToLocal(file);
        }
    }

    /**
     * 上传文件到本地存储
     * 返回 SHA-256 哈希值
     */
    private String uploadToLocal(MultipartFile file) throws IOException {
        // 确保上传目录存在
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("创建上传目录: {}", uploadPath.toAbsolutePath());
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
            ? originalFilename.substring(originalFilename.lastIndexOf("."))
            : "";

        // 生成唯一的文件名
        String fileName = System.currentTimeMillis() + "_" + generateRandomString(8) + extension;
        Path filePath = uploadPath.resolve(fileName);

        // 保存文件
        java.nio.file.Files.copy(file.getInputStream(), filePath);
        log.info("文件已保存到本地: {}", filePath.toAbsolutePath());

        // 计算文件哈希（SHA-256）
        String fileHash = calculateSHA256(filePath);
        log.info("文件 SHA-256 哈希: {}", fileHash);

        // 返回格式：local://filename?hash=sha256
        return "local://" + fileName + "?hash=" + fileHash;
    }

    /**
     * 上传文件到 IPFS
     * 使用 Apache HttpClient 直接调用 IPFS API
     * 返回 IPFS CID
     */
    private String uploadToIPFS(MultipartFile file) throws IOException {
        log.info("开始上传文件到 IPFS: {}", file.getOriginalFilename());

        // 获取文件字节数组
        byte[] fileBytes = file.getBytes();
        String url = ipfsApiUrl + "/add";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);

            // 构建multipart请求
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addPart("file", new ByteArrayBody(fileBytes, file.getOriginalFilename()))
                    .build();

            post.setEntity(entity);

            log.info("发送请求到 IPFS API: {}", url);
            HttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                throw new IOException("IPFS 上传失败，状态码: " + statusCode);
            }

            // 读取响应
            String responseText = getResponseString(response);
            log.info("IPFS 响应: {}", responseText);

            // 解析JSON响应获取哈希
            JsonNode jsonNode = objectMapper.readTree(responseText);
            String hash = jsonNode.get("Hash").asText();

            if (hash != null && (hash.startsWith("Qm") || hash.startsWith("b"))) {
                log.info("文件上传到 IPFS 成功，CID: {}", hash);
                return hash;
            } else {
                throw new RuntimeException("无法从 IPFS 响应中提取有效 CID: " + responseText);
            }
        } catch (Exception e) {
            log.error("IPFS 上传错误: {}", e.getMessage(), e);
            throw new IOException("IPFS 上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 计算文件的 SHA-256 哈希
     */
    private String calculateSHA256(Path filePath) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(filePath);
            byte[] hashBytes = digest.digest(fileBytes);

            // 转换为十六进制字符串（Java 8 兼容）
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 算法不可用", e);
        }
    }

    /**
     * 将字节数组转换为十六进制字符串（Java 8 兼容）
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * 读取 InputStream 为字符串（Java 8 兼容）
     */
    private String readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toString("UTF-8");
    }

    /**
     * 生成随机字符串
     */
    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    /**
     * 判断是否为本地文件哈希
     */
    public boolean isLocalFile(String fileHash) {
        return fileHash != null && fileHash.startsWith("local://");
    }

    /**
     * 从本地文件哈希中提取文件名
     */
    public String extractFileName(String localFileHash) {
        if (!isLocalFile(localFileHash)) {
            return null;
        }
        String withoutPrefix = localFileHash.substring("local://".length());
        int queryIndex = withoutPrefix.indexOf("?");
        return queryIndex != -1 ? withoutPrefix.substring(0, queryIndex) : withoutPrefix;
    }

    /**
     * 从本地文件哈希中提取 SHA-256 哈希
     */
    public String extractSHA256(String localFileHash) {
        if (!isLocalFile(localFileHash)) {
            return null;
        }
        int queryIndex = localFileHash.indexOf("?hash=");
        if (queryIndex != -1) {
            return localFileHash.substring(queryIndex + 6);
        }
        return null;
    }

    /**
     * 获取文件下载 URL
     */
    public String getFileDownloadUrl(String fileHash) {
        if (isLocalFile(fileHash)) {
            String fileName = extractFileName(fileHash);
            return "/api/files/download/" + fileName;
        } else {
            // IPFS CID，使用配置的 IPFS 网关
            return ipfsGatewayUrl + fileHash;
        }
    }

    /**
     * 从 HttpResponse 读取响应字符串
     */
    private String getResponseString(HttpResponse response) throws IOException {
        try (InputStream responseStream = response.getEntity().getContent();
             ByteArrayOutputStream result = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = responseStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return result.toString("UTF-8");
        }
    }

    /**
     * 检查 IPFS 节点是否可用
     */
    public boolean isIpfsNodeAvailable() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = ipfsApiUrl + "/version";
            HttpPost post = new HttpPost(url);

            HttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();

            log.info("IPFS 节点状态检查，状态码: {}", statusCode);
            return statusCode == 200;
        } catch (Exception e) {
            log.warn("IPFS 节点检查失败: {}", e.getMessage());
            return false;
        }
    }
}
