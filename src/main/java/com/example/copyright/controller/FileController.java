package com.example.copyright.controller;

import com.example.copyright.utils.JwtUtil;
import com.example.copyright.vo.ResultVO;
import com.example.copyright.service.FileUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@Api(tags = "文件管理")
@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${ipfs.api-url:http://127.0.0.1:5001/api/v0}")
    private String ipfsApiUrl;

    /**
     * 上传文件到 IPFS
     */
    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public ResultVO<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            log.info("开始上传文件: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());

            // 验证 token（可选，根据需求决定是否需要登录）
            if (authorization != null && !authorization.isEmpty()) {
                String token = authorization.replace("Bearer ", "");
                if (!jwtUtil.validateToken(token)) {
                    return ResultVO.error(401, "Token 无效");
                }
            }

            // 上传文件
            String fileHash = fileUploadService.uploadFile(file);

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("fileHash", fileHash);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            result.put("contentType", file.getContentType());

            // 判断是 IPFS CID 还是本地文件
            if (fileHash.startsWith("Qm") || fileHash.startsWith("ba")) {
                result.put("storage", "ipfs");
                result.put("ipfsUrl", "https://ipfs.io/ipfs/" + fileHash);
                result.put("gatewayUrl", "https://ipfs.io/ipfs/" + fileHash);
            } else if (fileHash.startsWith("local://")) {
                result.put("storage", "local");
                result.put("downloadUrl", "/api/files/download/" + fileUploadService.extractFileName(fileHash));
                result.put("sha256", fileUploadService.extractSHA256(fileHash));
            }

            log.info("文件上传成功: {}", fileHash);
            return ResultVO.success("文件上传成功", result);

        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return ResultVO.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 下载本地文件
     */
    @ApiOperation("下载文件")
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            File file = filePath.toFile();

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);

            // 确定内容类型
            String contentType = "application/octet-stream";
            try {
                contentType = Files.probeContentType(filePath);
            } catch (Exception e) {
                log.warn("无法检测文件内容类型，使用默认值");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取文件信息
     */
    @ApiOperation("获取文件信息")
    @GetMapping("/info/{fileHash}")
    public ResultVO<Map<String, Object>> getFileInfo(@PathVariable String fileHash) {
        try {
            Map<String, Object> info = new HashMap<>();
            info.put("fileHash", fileHash);

            if (fileUploadService.isLocalFile(fileHash)) {
                info.put("storage", "local");
                info.put("fileName", fileUploadService.extractFileName(fileHash));
                info.put("sha256", fileUploadService.extractSHA256(fileHash));
                info.put("downloadUrl", "/api/files/download/" + fileUploadService.extractFileName(fileHash));
            } else {
                info.put("storage", "ipfs");
                info.put("ipfsUrl", "https://ipfs.io/ipfs/" + fileHash);
                info.put("gatewayUrl", "https://ipfs.io/ipfs/" + fileHash);
            }

            return ResultVO.success(info);

        } catch (Exception e) {
            log.error("获取文件信息失败: {}", e.getMessage(), e);
            return ResultVO.error("获取文件信息失败: " + e.getMessage());
        }
    }

    /**
     * 检查 IPFS 连接状态
     * 使用 Apache HttpClient 调用 IPFS API
     */
    @ApiOperation("检查 IPFS 状态")
    @GetMapping("/ipfs/status")
    public ResultVO<Map<String, Object>> checkIPFSStatus() {
        try {
            Map<String, Object> status = new HashMap<>();

            // 使用 FileUploadService 检查 IPFS 是否可用
            boolean ipfsRunning = fileUploadService.isIpfsNodeAvailable();
            status.put("ipfsRunning", ipfsRunning);

            if (ipfsRunning) {
                // 尝试获取更多信息
                try {
                    // 获取版本信息
                    String version = getIpfsInfo("/version");
                    status.put("version", version);

                    // 获取节点ID
                    String nodeId = getIpfsInfo("/id");
                    status.put("nodeId", extractJsonValue(nodeId, "ID"));

                    // 获取peers数量
                    String peers = getIpfsInfo("/swarm/peers");
                    long peerCount = countLines(peers);
                    status.put("peerCount", peerCount);

                } catch (Exception e) {
                    log.warn("获取 IPFS 详细信息失败: {}", e.getMessage());
                }
            }

            return ResultVO.success("IPFS 状态查询成功", status);

        } catch (Exception e) {
            log.error("检查 IPFS 状态失败: {}", e.getMessage(), e);
            return ResultVO.error("IPFS 未运行或无法连接: " + e.getMessage());
        }
    }

    /**
     * 获取 IPFS 信息的辅助方法
     */
    private String getIpfsInfo(String endpoint) throws IOException {
        String url = ipfsApiUrl + endpoint;
        org.apache.http.client.methods.HttpPost post = new org.apache.http.client.methods.HttpPost(url);

        try (org.apache.http.impl.client.CloseableHttpClient httpClient =
                     org.apache.http.impl.client.HttpClients.createDefault()) {

            org.apache.http.HttpResponse response = httpClient.execute(post);
            return readInputStream(response.getEntity().getContent());
        }
    }

    /**
     * 从 JSON 中提取指定字段的值
     */
    private String extractJsonValue(String json, String key) {
        try {
            String searchPattern = "\"" + key + "\":\"";
            int startIndex = json.indexOf(searchPattern);
            if (startIndex != -1) {
                startIndex += searchPattern.length();
                int endIndex = json.indexOf("\"", startIndex);
                if (endIndex > startIndex) {
                    return json.substring(startIndex, endIndex);
                }
            }
        } catch (Exception e) {
            log.warn("解析 JSON 失败: {}", e.getMessage());
        }
        return "N/A";
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
     * 计算字符串的行数（Java 8 兼容）
     */
    private long countLines(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        return str.split("\n").length;
    }
}
