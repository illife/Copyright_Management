package com.example.copyright.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.copyright.dto.CopyrightRegisterDTO;
import com.example.copyright.entity.Copyright;
import com.example.copyright.service.CopyrightService;
import com.example.copyright.service.FileUploadService;
import com.example.copyright.utils.JwtUtil;
import com.example.copyright.vo.CopyrightVO;
import com.example.copyright.vo.ResultVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权控制器
 */
@Api(tags = "版权管理")
@Slf4j
@RestController
@RequestMapping("/api/copyrights")
public class CopyrightController {

    @Autowired
    private CopyrightService copyrightService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 注册版权（支持文件上传）
     */
    @ApiOperation("注册版权（带文件上传）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "owner", value = "所有者地址", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "title", value = "作品标题", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "author", value = "作者", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "description", value = "作品描述", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "file", value = "作品文件", required = true, dataType = "file", paramType = "form")
    })
    @PostMapping(value = "/register-with-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultVO<Copyright> registerWithFile(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 新合约版本：从JWT token获取owner地址
            String owner = jwtUtil.getAddressFromToken(authorization);
            if (owner == null) {
                return ResultVO.error(401, "未登录或token无效");
            }

            log.info("开始注册版权: owner={}, title={}, file={}", owner, title, file.getOriginalFilename());

            // 1. 上传文件到 IPFS
            String fileHash = fileUploadService.uploadFile(file);
            log.info("文件上传成功，hash: {}", fileHash);

            // 2. 构建 DTO（不再设置owner，由Service层从token获取）
            CopyrightRegisterDTO dto = new CopyrightRegisterDTO();
            dto.setTitle(title);
            dto.setAuthor(author);
            dto.setFileHash(fileHash);
            dto.setDescription(description);

            // 3. 注册版权（传入owner地址）
            Copyright copyright = copyrightService.register(dto, owner);

            // 4. 添加文件信息到返回结果
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("fileHash", fileHash);
            if (fileHash.startsWith("Qm") || fileHash.startsWith("ba")) {
                fileInfo.put("storage", "ipfs");
                fileInfo.put("ipfsUrl", "https://ipfs.io/ipfs/" + fileHash);
            }

            return ResultVO.success("版权注册成功", copyright);

        } catch (Exception e) {
            log.error("Failed to register copyright with file: {}", e.getMessage(), e);
            return ResultVO.error("版权注册失败: " + e.getMessage());
        }
    }

    /**
     * 注册版权
     * 新合约版本：从Authorization header获取owner地址
     */
    @ApiOperation("注册版权")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "版权注册信息", required = true, dataType = "CopyrightRegisterDTO"),
            @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, paramType = "header")
    })
    @PostMapping("/register")
    public ResultVO<Copyright> register(
            @Validated @RequestBody CopyrightRegisterDTO dto,
            @RequestHeader(value = "Authorization", required = true) String authorization
    ) {
        try {
            // 新合约版本：从JWT token获取owner地址
            String owner = jwtUtil.getAddressFromToken(authorization);
            if (owner == null) {
                return ResultVO.error(401, "未登录或token无效");
            }

            Copyright copyright = copyrightService.register(dto, owner);
            return ResultVO.success("版权注册成功", copyright);
        } catch (Exception e) {
            log.error("Failed to register copyright: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 根据ID查询版权
     */
    @ApiOperation("根据ID查询版权")
    @ApiImplicitParam(name = "id", value = "版权ID", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/{id}")
    public ResultVO<CopyrightVO> getById(@PathVariable Long id) {
        try {
            CopyrightVO vo = copyrightService.getById(id);
            if (vo == null) {
                return ResultVO.error("版权不存在");
            }
            return ResultVO.success(vo);
        } catch (Exception e) {
            log.error("Failed to get copyright: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 根据合约ID查询版权
     */
    @ApiOperation("根据合约ID查询版权")
    @ApiImplicitParam(name = "contractId", value = "合约ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/contract/{contractId}")
    public ResultVO<CopyrightVO> getByContractId(@PathVariable String contractId) {
        try {
            CopyrightVO vo = copyrightService.getByContractId(contractId);
            if (vo == null) {
                return ResultVO.error("版权不存在");
            }
            return ResultVO.success(vo);
        } catch (Exception e) {
            log.error("Failed to get copyright: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 根据所有者查询版权列表
     */
    @ApiOperation("根据所有者查询版权列表")
    @ApiImplicitParam(name = "ownerAddress", value = "所有者地址", required = true, dataType = "String", paramType = "path")
    @GetMapping("/owner/{ownerAddress}")
    public ResultVO<List<CopyrightVO>> listByOwner(@PathVariable String ownerAddress) {
        try {
            List<CopyrightVO> list = copyrightService.listByOwner(ownerAddress);
            return ResultVO.success(list);
        } catch (Exception e) {
            log.error("Failed to list copyrights: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 分页查询版权
     */
    @ApiOperation("分页查询版权")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "int", paramType = "query")
    })
    @GetMapping("/page")
    public ResultVO<Page<CopyrightVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Page<CopyrightVO> page = copyrightService.page(pageNum, pageSize);
            return ResultVO.success(page);
        } catch (Exception e) {
            log.error("Failed to page copyrights: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 搜索版权
     */
    @ApiOperation("搜索版权")
    @ApiImplicitParam(name = "keyword", value = "关键词", required = true, dataType = "String", paramType = "query")
    @GetMapping("/search")
    public ResultVO<List<CopyrightVO>> search(@RequestParam String keyword) {
        try {
            List<CopyrightVO> list = copyrightService.searchByKeyword(keyword);
            return ResultVO.success(list);
        } catch (Exception e) {
            log.error("Failed to search copyrights: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 根据作者查询版权
     */
    @ApiOperation("根据作者查询版权")
    @ApiImplicitParam(name = "author", value = "作者", required = true, dataType = "String", paramType = "path")
    @GetMapping("/author/{author}")
    public ResultVO<List<CopyrightVO>> listByAuthor(@PathVariable String author) {
        try {
            List<CopyrightVO> list = copyrightService.listByAuthor(author);
            return ResultVO.success(list);
        } catch (Exception e) {
            log.error("Failed to list copyrights by author: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }
}
