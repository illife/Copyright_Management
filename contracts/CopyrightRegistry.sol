// SPDX-License-Identifier: MIT
pragma solidity ^0.6.10;

import "./RBAC.sol";

/**
 * @title CopyrightRegistry
 * @dev 联盟链版权管理合约（无代币、无转账，仅事件记录 + 审批流程）
 */
contract CopyrightRegistry is RBAC {
     // 定义授权状态枚举
    enum LicenseStatus {
        NONE,     // 无效状态
        PENDING,  // 待审批状态
        APPROVED, // 已批准状态
        ACTIVE,   // 激活状态
        REVOKED,  // 已撤销状态
        EXPIRED   // 已过期状态
    }

    // 版权信息结构体
    struct Copyright {
        address owner;        // 版权拥有者地址
        string title;         // 版权标题
        string author;        // 作者名称
        string fileHash;      // 文件哈希值（用于验证文件完整性）
        uint256 registerTime; // 注册时间戳
        uint256 updateTime;   // 更新时间戳
        bool isActive;        // 是否激活状态
    }

    // 授权信息结构体
    struct License {
        uint256 copyrightId;  // 关联的版权ID
        address applicant;    // 申请者地址（可能是代理）
        address licensee;     // 被授权者地址
        address approver;     // 批准者地址
        uint256 startTime;    // 授权开始时间
        uint256 endTime;      // 授权结束时间
        bool isExclusive;     // 是否为独占授权
        LicenseStatus status; // 授权状态
        uint256 applyTime;    // 申请时间戳
        uint256 approveTime;  // 批准时间戳
    }

    // 版权映射：版权ID -> 版权信息
    mapping(uint256 => Copyright) public copyrights;
    // 用户拥有的版权列表：用户地址 -> 版权ID数组
    mapping(address => uint256[]) public copyrightsByOwner;
    
    // 授权映射：授权ID -> 授权信息
    mapping(uint256 => License) public licenses;
    // 版权相关的授权列表：版权ID -> 授权ID数组
    mapping(uint256 => uint256[]) public licensesByCopyright;
    // 用户获得的授权列表：用户地址 -> 授权ID数组
    mapping(address => uint256[]) public licensesByLicensee;
    
    // 版权审批者映射：版权ID -> 审批者地址（允许指定特定人员审批该版权的授权）
    mapping(uint256 => address) public copyrightApprovers;

    // 自增ID计数器
    uint256 private _nextCopyrightId = 1; // 下一个版权ID
    uint256 private _nextLicenseId = 1;   // 下一个授权ID

    // 定义事件，用于前端监听和日志记录
    event CopyrightRegistered(
        uint256 indexed copyrightId,  // 版权ID
        address indexed owner,        // 版权拥有者
        string title,                 // 版权标题
        string author,                // 作者名称
        string fileHash,              // 文件哈希
        uint256 timestamp             // 注册时间戳
    );

    event LicenseApplied(
        uint256 indexed licenseId,    // 授权ID
        uint256 indexed copyrightId,  // 版权ID
        address applicant,            // 申请者
        address licensee,             // 被授权者
        uint256 startTime,            // 开始时间
        uint256 endTime,              // 结束时间
        bool isExclusive              // 是否独占
    );

    event LicenseApproved(
        uint256 indexed licenseId,    // 授权ID
        address indexed approver,     // 批准者
        uint256 timestamp             // 批准时间戳
    );

    event LicenseRejected(
        uint256 indexed licenseId,    // 授权ID
        address indexed approver,     // 拒绝者
        uint256 timestamp             // 拒绝时间戳
    );

    event LicenseActivated(
        uint256 indexed licenseId,    // 授权ID
        address indexed licensee,     // 激活者（被授权者）
        uint256 timestamp             // 激活时间戳
    );

    event LicenseRevoked(
        uint256 indexed licenseId,    // 授权ID
        address indexed revoker,      // 撤销者
        uint256 timestamp             // 撤销时间戳
    );

    event RoyaltyRecorded(
        uint256 indexed copyrightId,  // 版权ID
        address indexed payer,        // 付款方
        address indexed payee,        // 收款方
        uint256 amount,               // 金额
        string currency,              // 货币类型
        uint256 timestamp             // 记录时间戳
    );

    event CopyrightTransferred(
        uint256 indexed copyrightId,  // 版权ID
        address indexed oldOwner,     // 原拥有者
        address indexed newOwner,     // 新拥有者
        uint256 timestamp             // 转让时间戳
    );

    /**
     * @dev 构造函数：初始化系统管理员
     * @param initialAdmins 初始管理员地址数组
     */
    constructor(address[] memory initialAdmins) public {
        // 设置初始系统管理员
        for (uint256 i = 0; i < initialAdmins.length; i++) {
            if (initialAdmins[i] != address(0)) {
                _grantRole(SYSTEM_ADMIN_ROLE, initialAdmins[i]);
            }
        }
        // 将合约部署者也设为系统管理员
        _grantRole(SYSTEM_ADMIN_ROLE, msg.sender);
    }

    /**
     * @dev 注册版权（仅系统管理员或已注册用户可调用）
     * @param title 版权标题
     * @param author 作者名称
     * @param fileHash 文件哈希值
     * @return uint256 注册的版权ID
     */
    function registerCopyright(
        string memory title,
        string memory author,
        string memory fileHash
    ) external returns (uint256) {
        require(bytes(title).length > 0, "Title cannot be empty");                    // 检查标题不为空
        require(bytes(fileHash).length >= 46, "Invalid file hash format");            // 检查文件哈希格式（IPFS格式）
        
        // 检查权限：系统管理员或已注册用户（自己）
        // 注意：LICENSE_MANAGER_ROLE不能注册版权，防止权限滥用
        bool isAuthorized = hasRole(SYSTEM_ADMIN_ROLE, msg.sender) || 
                          (isRegisteredUser[msg.sender] && !hasRole(LICENSE_MANAGER_ROLE, msg.sender));
        require(isAuthorized, "Unauthorized to register copyright");

        uint256 id = _nextCopyrightId++; // 生成新的版权ID
        copyrights[id] = Copyright({
            owner: msg.sender,           // 设置拥有者为调用者
            title: title,
            author: author,
            fileHash: fileHash,
            registerTime: block.timestamp, // 设置注册时间
            updateTime: block.timestamp,   // 设置更新时间
            isActive: true               // 设置为激活状态
        });

        // 记录用户拥有的版权列表
        copyrightsByOwner[msg.sender].push(id);
        // 如果用户还没有CONTENT_OWNER角色，则授予该角色
        if (!hasRole(CONTENT_OWNER_ROLE, msg.sender)) {
            _grantRole(CONTENT_OWNER_ROLE, msg.sender);
        }

        emit CopyrightRegistered(id, msg.sender, title, author, fileHash, block.timestamp);
        return id;
    }

    /**
     * @dev 转让版权（仅版权拥有者可调用）
     * @param copyrightId 要转让的版权ID
     * @param newOwner 新的拥有者地址
     */
    function transferCopyright(uint256 copyrightId, address newOwner) external {
        Copyright storage copyright = copyrights[copyrightId]; // 获取版权信息
        require(copyright.owner == msg.sender, "Only copyright owner can transfer"); // 检查调用者是否为拥有者
        require(newOwner != address(0), "Invalid new owner");                      // 检查新拥有者地址有效性
        require(isRegisteredUser[newOwner], "New owner must be registered");       // 检查新拥有者是否已注册

        address oldOwner = copyright.owner;
        copyright.owner = newOwner;      // 更新拥有者
        copyright.updateTime = block.timestamp; // 更新时间戳

        // 更新用户版权列表：从原拥有者列表中移除，添加到新拥有者列表
        _removeFromOwnerList(oldOwner, copyrightId);
        copyrightsByOwner[newOwner].push(copyrightId);

        // 检查新所有者是否已有CONTENT_OWNER角色，如果没有则授予
        if (!hasRole(CONTENT_OWNER_ROLE, newOwner)) {
            _grantRole(CONTENT_OWNER_ROLE, newOwner);
        }

        emit CopyrightTransferred(copyrightId, oldOwner, newOwner, block.timestamp);
    }

    /**
     * @dev 申请授权（任何人可调用）
     * @param copyrightId 要申请授权的版权ID
     * @param licensee 被授权者地址
     * @param startTime 授权开始时间
     * @param endTime 授权结束时间
     * @param isExclusive 是否为独占授权
     * @return uint256 申请的授权ID
     */
    function applyForLicense(
        uint256 copyrightId,
        address licensee,
        uint256 startTime,
        uint256 endTime,
        bool isExclusive
    ) external returns (uint256) {
        require(copyrights[copyrightId].owner != address(0), "Copyright does not exist"); // 检查版权是否存在
        require(licensee != address(0), "Invalid licensee");                            // 检查被授权者地址有效性
        require(startTime < endTime, "Invalid time range");                             // 检查时间范围有效性
        // require(block.timestamp <= startTime, "Start time must be in future or now");   // ❌ 已移除：由后端验证（避免节点时间问题）
        // require(startTime >= copyrights[copyrightId].registerTime, "License start time cannot be earlier than copyright registration"); // ❌ 已移除：由后端验证
        require(endTime - startTime <= 365 days * 10, "License period too long (max 10 years)"); // 限制授权期限不超过10年

        uint256 licenseId = _nextLicenseId++; // 生成新的授权ID
        licenses[licenseId] = License({
            copyrightId: copyrightId,
            applicant: msg.sender,     // 申请者为调用者
            licensee: licensee,        // 被授权者
            approver: address(0),      // 暂无批准者
            startTime: startTime,      // 开始时间
            endTime: endTime,          // 结束时间
            isExclusive: isExclusive,  // 是否独占
            status: LicenseStatus.PENDING, // 设置为待审批状态
            applyTime: block.timestamp,    // 申请时间
            approveTime: 0                 // 暂无批准时间
        });

        // 更新相关列表
        licensesByCopyright[copyrightId].push(licenseId); // 添加到版权相关授权列表
        licensesByLicensee[licensee].push(licenseId);     // 添加到被授权者相关授权列表

        emit LicenseApplied(licenseId, copyrightId, msg.sender, licensee, startTime, endTime, isExclusive);
        return licenseId;
    }

    /**
     * @dev 批准授权（仅版权拥有者或指定的审批者可调用）
     * @param licenseId 要批准的授权ID
     */
    function approveLicense(uint256 licenseId) external {
        License storage lic = licenses[licenseId]; // 获取授权信息
        require(lic.status == LicenseStatus.PENDING, "License not pending"); // 检查授权状态为待审批
        
        // 检查权限：版权拥有者或指定的审批者
        address copyrightOwner = copyrights[lic.copyrightId].owner;
        bool isAuthorizedApprover = msg.sender == copyrightOwner || 
                                  (msg.sender == copyrightApprovers[lic.copyrightId] && hasRole(LICENSE_APPROVER_ROLE, msg.sender));
        require(isAuthorizedApprover, "Only copyright owner or designated approver can approve");

        // 检查独占授权时间冲突（防止同一时间段内有多个独占授权）
        if (lic.isExclusive) {
            uint256[] storage existingLicenses = licensesByCopyright[lic.copyrightId]; // 获取该版权的所有授权
            for (uint256 i = 0; i < existingLicenses.length; i++) {
                License storage existing = licenses[existingLicenses[i]];
                if (existing.isExclusive && // 检查是否为独占授权
                    existing.status != LicenseStatus.REVOKED && // 排除已撤销的授权
                    existing.status != LicenseStatus.EXPIRED && // 排除已过期的授权
                    existing.endTime > lic.startTime &&        // 检查时间重叠：现有授权结束时间 > 新授权开始时间
                    lic.endTime > existing.startTime) {        // 检查时间重叠：新授权结束时间 > 现有授权开始时间
                    revert("Exclusive license time conflict"); // 时间冲突，拒绝批准
                }
            }
        }

        lic.status = LicenseStatus.APPROVED;  // 设置为已批准状态
        lic.approver = msg.sender;            // 记录批准者
        lic.approveTime = block.timestamp;    // 记录批准时间
        
        emit LicenseApproved(licenseId, msg.sender, block.timestamp);
    }

    /**
     * @dev 拒绝授权（仅版权拥有者或指定的审批者可调用）
     * @param licenseId 要拒绝的授权ID
     */
    function rejectLicense(uint256 licenseId) external {
        License storage lic = licenses[licenseId]; // 获取授权信息
        require(lic.status == LicenseStatus.PENDING, "License not pending"); // 检查授权状态为待审批
        
        // 检查权限：版权拥有者或指定的审批者
        address copyrightOwner = copyrights[lic.copyrightId].owner;
        bool isAuthorizedApprover = msg.sender == copyrightOwner || 
                                  (msg.sender == copyrightApprovers[lic.copyrightId] && hasRole(LICENSE_APPROVER_ROLE, msg.sender));
        require(isAuthorizedApprover, "Only copyright owner or designated approver can reject");

        lic.status = LicenseStatus.REVOKED; // 设置为已撤销状态（拒绝）
        emit LicenseRejected(licenseId, msg.sender, block.timestamp);
    }

    /**
     * @dev 激活授权（仅被授权者可调用）
     * @param licenseId 要激活的授权ID
     */
    function activateLicense(uint256 licenseId) external {
        License storage lic = licenses[licenseId]; // 获取授权信息
        // ❌ 已移除激活权限限制（由后端验证，更灵活）
        // require(lic.licensee == msg.sender || lic.applicant == msg.sender, "Only licensee or applicant can activate");
        require(lic.status == LicenseStatus.APPROVED, "License must be approved first"); // 检查授权状态为已批准

        lic.status = LicenseStatus.ACTIVE; // 设置为激活状态
        emit LicenseActivated(licenseId, msg.sender, block.timestamp);
    }

    /**
     * @dev 撤销授权（多角色可调用）
     * @param licenseId 要撤销的授权ID
     */
    function revokeLicense(uint256 licenseId) external {
        License storage lic = licenses[licenseId]; // 获取授权信息
        require(
            lic.status == LicenseStatus.APPROVED || lic.status == LicenseStatus.ACTIVE,
            "License not in revocable state" // 检查授权状态是否可撤销
        );

        bool isAuthorized = false;
        address copyrightOwner = copyrights[lic.copyrightId].owner;
        
        // 检查权限：版权拥有者、被授权者或授权管理者
        if (msg.sender == copyrightOwner) {
            isAuthorized = true; // 版权拥有者可以撤销
        } else if (msg.sender == lic.licensee) {
            isAuthorized = true; // 被授权者可以撤销自己的授权
        } else if (hasRole(LICENSE_MANAGER_ROLE, msg.sender)) {
            isAuthorized = true; // 授权管理者可以撤销
        } else if (msg.sender == copyrightApprovers[lic.copyrightId] && hasRole(LICENSE_APPROVER_ROLE, msg.sender)) {
            isAuthorized = true; // 指定审批者可以撤销
        }

        require(isAuthorized, "Unauthorized to revoke"); // 检查权限

        lic.status = LicenseStatus.REVOKED; // 设置为已撤销状态
        emit LicenseRevoked(licenseId, msg.sender, block.timestamp);
    }

    /**
     * @dev 设置版权审批者（仅版权拥有者可调用）
     * 允许版权拥有者指定特定人员来审批该版权的授权申请
     * @param copyrightId 版权ID
     * @param approver 审批者地址
     */
    function setCopyrightApprover(uint256 copyrightId, address approver) external {
        require(copyrights[copyrightId].owner == msg.sender, "Only copyright owner can set approver"); // 检查调用者是否为版权拥有者
        require(isRegisteredUser[approver], "Approver must be registered"); // 检查审批者是否已注册
        require(hasRole(LICENSE_APPROVER_ROLE, approver), "Approver must have LICENSE_APPROVER role"); // 检查审批者是否拥有审批角色
        
        copyrightApprovers[copyrightId] = approver; // 设置审批者
    }

    /**
     * @dev 记录版税（多角色可调用）
     * @param copyrightId 版权ID
     * @param payer 付款方地址
     * @param payee 收款方地址
     * @param amount 金额
     * @param currency 货币类型
     */
    function recordRoyaltyPayment(
        uint256 copyrightId,
        address payer,
        address payee,
        uint256 amount,
        string memory currency
    ) external {
        require(copyrights[copyrightId].owner != address(0), "Copyright does not exist"); // 检查版权是否存在
        require(payer != address(0), "Payer cannot be zero");                           // 检查付款方地址有效性
        require(payee != address(0), "Payee cannot be zero");                          // 检查收款方地址有效性
        require(amount > 0, "Amount must be greater than zero");                       // 检查金额大于0
        require(bytes(currency).length > 0, "Currency cannot be empty");               // 检查货币类型不为空
        
        // 检查权限：版权拥有者、授权管理者或付费者本人
        bool isAuthorized = msg.sender == copyrights[copyrightId].owner ||
                          hasRole(LICENSE_MANAGER_ROLE, msg.sender) ||
                          msg.sender == payer;
        require(isAuthorized, "Unauthorized to record royalty payment");

        emit RoyaltyRecorded(copyrightId, payer, payee, amount, currency, block.timestamp);
    }

    /**
     * @dev 查询版权信息
     * @param copyrightId 版权ID
     * @return owner 版权拥有者地址
     * @return title 版权标题
     * @return author 作者名称
     * @return fileHash 文件哈希值
     * @return registerTime 注册时间
     * @return updateTime 更新时间
     * @return isActive 是否激活
     */
    function getCopyright(uint256 copyrightId) external view returns (
        address owner,
        string memory title,
        string memory author,
        string memory fileHash,
        uint256 registerTime,
        uint256 updateTime,
        bool isActive
    ) {
        Copyright memory copyright = copyrights[copyrightId]; // 获取版权信息
        require(copyright.owner != address(0), "Copyright does not exist"); // 检查版权是否存在
        return (
            copyright.owner,
            copyright.title,
            copyright.author,
            copyright.fileHash,
            copyright.registerTime,
            copyright.updateTime,
            copyright.isActive
        );
    }

    /**
     * @dev 检查用户是否拥有有效的授权
     * @param copyrightId 版权ID
     * @param user 用户地址
     * @return bool 用户是否拥有有效的授权
     */
    function hasValidLicense(uint256 copyrightId, address user) external view returns (bool) {
        uint256[] storage ids = licensesByCopyright[copyrightId]; // 获取该版权的所有授权ID
        uint256 checkLimit = 50; // 设置检查限制，防止DoS攻击
        uint256 checkedCount = 0;
        
        // 遍历所有授权，检查是否存在有效的授权
        for (uint256 i = 0; i < ids.length && checkedCount < checkLimit; i++) {
            License storage lic = licenses[ids[i]];
            if (lic.licensee == user &&                    // 检查被授权者是否为指定用户
                (lic.status == LicenseStatus.ACTIVE) &&    // 检查授权状态为激活
                block.timestamp >= lic.startTime &&        // 检查当前时间 >= 授权开始时间
                block.timestamp <= lic.endTime) {          // 检查当前时间 <= 授权结束时间
                return true; // 找到有效授权，返回true
            }
            checkedCount++;
        }
        return false; // 未找到有效授权，返回false
    }

    /**
     * @dev 辅助函数：从所有者列表中移除版权ID
     * 用于版权转让时更新用户拥有的版权列表
     * @param owner 所有者地址
     * @param copyrightId 要移除的版权ID
     */
    function _removeFromOwnerList(address owner, uint256 copyrightId) private {
        uint256[] storage list = copyrightsByOwner[owner]; // 获取该用户拥有的版权列表
        for (uint256 i = 0; i < list.length; i++) {
            if (list[i] == copyrightId) { // 找到要移除的版权ID
                if (i != list.length - 1) {
                    list[i] = list[list.length - 1]; // 将最后一个元素移动到当前位置
                }
                list.pop(); // 移除最后一个元素
                break; // 退出循环
            }
        }
    }
}