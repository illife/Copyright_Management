// SPDX-License-Identifier: MIT
pragma solidity ^0.6.10;

/**
 * @title RBAC
 * @dev 增强型角色权限控制模块，支持多角色精细管理
 */
contract RBAC {
        // 定义系统中使用的各种角色
    bytes32 public constant SYSTEM_ADMIN_ROLE = keccak256("SYSTEM_ADMIN_ROLE");      // 系统管理员：最高权限
    bytes32 public constant CONTENT_OWNER_ROLE = keccak256("CONTENT_OWNER_ROLE");    // 内容拥有者：管理自己的版权
    bytes32 public constant LICENSE_APPROVER_ROLE = keccak256("LICENSE_APPROVER_ROLE"); // 授权审批者：审批授权申请
    bytes32 public constant LICENSE_MANAGER_ROLE = keccak256("LICENSE_MANAGER_ROLE");  // 授权管理者：管理授权流程
    bytes32 public constant AUDITOR_ROLE = keccak256("AUDITOR_ROLE");                // 审计员：只读权限

    // 角色权限映射：角色 -> 地址 -> 是否拥有该角色
    mapping(bytes32 => mapping(address => bool)) private _roles;
    
    // 用户注册状态映射：地址 -> 是否已注册
    mapping(address => bool) public isRegisteredUser;

    // 定义事件，用于前端监听和日志记录
    event RoleGranted(bytes32 indexed role, address indexed account, address indexed sender);  // 授予角色事件
    event RoleRevoked(bytes32 indexed role, address indexed account, address indexed sender); // 撤销角色事件
    event UserRegistered(address indexed user, address indexed registrar, uint256 timestamp); // 用户注册事件

    /**
     * @dev 检查指定地址是否拥有指定角色
     * @param role 要检查的角色
     * @param account 要检查的地址
     * @return bool 是否拥有该角色
     */
    function hasRole(bytes32 role, address account) public view returns (bool) {
        return _roles[role][account];
    }

    /**
     * @dev 内部函数：授予角色
     * @param role 要授予的角色
     * @param account 接收角色的地址
     */
    function _grantRole(bytes32 role, address account) internal {
        if (!_roles[role][account]) {
            _roles[role][account] = true;
            emit RoleGranted(role, account, msg.sender); // 触发授予角色事件
        }
    }

    /**
     * @dev 内部函数：撤销角色
     * @param role 要撤销的角色
     * @param account 要撤销角色的地址
     */
    function _revokeRole(bytes32 role, address account) internal {
        if (_roles[role][account]) {
            _roles[role][account] = false;
            emit RoleRevoked(role, account, msg.sender); // 触发撤销角色事件
        }
    }

    /**
     * @dev 修饰符：只有拥有指定角色的地址才能调用
     * @param role 必须拥有的角色
     */
    modifier onlyRole(bytes32 role) {
        require(hasRole(role, msg.sender), "OptimizedRBAC: unauthorized");
        _;
    }

    /**
     * @dev 授予角色（仅系统管理员可调用）
     * @param role 要授予的角色
     * @param account 接收角色的地址
     */
    function grantRole(bytes32 role, address account) external onlyRole(SYSTEM_ADMIN_ROLE) {
        _grantRole(role, account);
    }

    /**
     * @dev 撤销角色（仅系统管理员可调用）
     * @param role 要撤销的角色
     * @param account 要撤销角色的地址
     */
    function revokeRole(bytes32 role, address account) external onlyRole(SYSTEM_ADMIN_ROLE) {
        _revokeRole(role, account);
    }

    /**
     * @dev 注册用户（仅系统管理员可调用）
     * @param user 要注册的用户地址
     */
    function registerUser(address user) external onlyRole(SYSTEM_ADMIN_ROLE) {
        require(user != address(0), "Invalid user address");           // 检查地址有效性
        require(!isRegisteredUser[user], "User already registered");   // 检查用户是否已注册
        isRegisteredUser[user] = true;                                // 设置用户注册状态
        emit UserRegistered(user, msg.sender, block.timestamp);       // 触发用户注册事件
    }
}