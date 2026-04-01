package com.example.copyright.controller;

import com.example.copyright.dto.UserRegisterDTO;
import com.example.copyright.entity.User;
import com.example.copyright.service.UserService;
import com.example.copyright.utils.JwtUtil;
import com.example.copyright.vo.ResultVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@Api(tags = "用户管理")
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @ApiOperation("用户登录")
    @ApiImplicitParam(name = "address", value = "用户地址", required = true, dataType = "String", paramType = "query")
    @PostMapping("/login")
    public ResultVO<Map<String, Object>> login(@RequestParam String address) {
        try {
            User user = userService.getByAddress(address);
            if (user == null) {
                return ResultVO.error("用户不存在");
            }

            if (user.getStatus() == 0) {
                return ResultVO.error("账户已被禁用");
            }

            // 生成 token
            String token = jwtUtil.generateToken(address, user.getRole());

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);

            return ResultVO.success("登录成功", data);
        } catch (Exception e) {
            log.error("Failed to login: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @ApiOperation("用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "用户注册信息", required = true, dataType = "UserRegisterDTO")
    })
    @PostMapping("/register")
    public ResultVO<User> register(@Validated @RequestBody UserRegisterDTO dto) {
        try {
            User user = userService.register(dto);
            return ResultVO.success("用户注册成功", user);
        } catch (Exception e) {
            log.error("Failed to register user: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 根据地址查询用户
     */
    @ApiOperation("根据地址查询用户")
    @ApiImplicitParam(name = "address", value = "用户地址", required = true, dataType = "String", paramType = "path")
    @GetMapping("/address/{address}")
    public ResultVO<User> getByAddress(@PathVariable String address) {
        try {
            User user = userService.getByAddress(address);
            if (user == null) {
                return ResultVO.error("用户不存在");
            }
            return ResultVO.success(user);
        } catch (Exception e) {
            log.error("Failed to get user: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 根据ID查询用户
     */
    @ApiOperation("根据ID查询用户")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/{id}")
    public ResultVO<User> getById(@PathVariable Long id) {
        try {
            User user = userService.getById(id);
            if (user == null) {
                return ResultVO.error("用户不存在");
            }
            return ResultVO.success(user);
        } catch (Exception e) {
            log.error("Failed to get user: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 查询所有用户
     */
    @ApiOperation("查询所有用户")
    @GetMapping("/list")
    public ResultVO<List<User>> listAll() {
        try {
            List<User> users = userService.listAll();
            return ResultVO.success(users);
        } catch (Exception e) {
            log.error("Failed to list users: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 更新用户角色
     */
    @ApiOperation("更新用户角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "role", value = "角色", required = true, dataType = "String", paramType = "query")
    })
    @PutMapping("/{id}/role")
    public ResultVO<Void> updateRole(@PathVariable Long id, @RequestParam String role) {
        try {
            userService.updateRole(id, role);
            return ResultVO.success();
        } catch (Exception e) {
            log.error("Failed to update user role: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 更新用户状态
     */
    @ApiOperation("更新用户状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "status", value = "状态(0:禁用,1:启用)", required = true, dataType = "Integer", paramType = "query")
    })
    @PutMapping("/{id}/status")
    public ResultVO<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            userService.updateStatus(id, status);
            return ResultVO.success();
        } catch (Exception e) {
            log.error("Failed to update user status: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }
}
