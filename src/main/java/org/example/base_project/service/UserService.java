package org.example.base_project.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import jakarta.annotation.Resource;
import org.example.base_project.entity.*;
import org.example.base_project.exception.CustomException;
import org.example.base_project.mapper.RoleMapper;
import org.example.base_project.mapper.UserMapper;
import org.example.base_project.utils.IpUtils;
import org.example.base_project.utils.TokenUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
    private static final String READONLY_TEST_USERNAME = "test";

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    public User selectById(Integer userId) {
        if (userId == null) {
            return null;
        }
        return userMapper.selectById(userId);
    }

    public void register(User user) {
        if (user == null || StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            throw new CustomException("400", "请输入用户名和密码");
        }

        User dbuser = userMapper.selectByName(user);
        if (dbuser != null) {
            throw new CustomException("500", "账号已经存在");
        }

        String rawPassword = user.getPassword();
        String encodedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        String timeStr = now();

        user.setPassword(encodedPassword);
        user.setUsercode(System.currentTimeMillis() + "_" + rawPassword);
        user.setCreatedAt(timeStr);
        user.setUpdatedAt(timeStr);
        if (user.getRoleId() == null) {
            user.setRoleId(3);
        }
        if (StrUtil.isBlank(user.getStatus())) {
            user.setStatus("1");
        }

        userMapper.insert(user);
    }

    /**
     * 兼容原 WebController 的 /api/login。
     */
    public LoginUserResult login(Account account) {
        User dbuser = checkLogin(account);
        String token = TokenUtils.createToken(String.valueOf(dbuser.getId()), dbuser.getPassword());
        updateLoginIp(dbuser);

        LoginUserResult safeUser = new LoginUserResult();
        safeUser.setId(dbuser.getId());
        safeUser.setUsername(dbuser.getUsername());
        safeUser.setAvatar(dbuser.getAvatarUrl());
        safeUser.setToken(token);
        safeUser.setIp(IpUtils.getClientIpAddress());
        return safeUser;
    }

    /**
     * 供前端新登录接口 /api/auth/login 使用。
     */
    public LoginResponse loginForFrontend(Account account) {
        User dbuser = checkLogin(account);
        String token = TokenUtils.createToken(String.valueOf(dbuser.getId()), dbuser.getPassword());
        String refreshToken = TokenUtils.createRefreshToken(String.valueOf(dbuser.getId()), dbuser.getPassword());
        updateLoginIp(dbuser);
        return new LoginResponse(token, refreshToken);
    }

    public UserInfoResult buildUserInfo(User user) {
        UserInfoResult result = new UserInfoResult();
        result.setUserId(user.getId());
        result.setUserName(user.getUsername());
        result.setEmail(user.getEmail());
        result.setAvatar(user.getAvatarUrl());

        // test 账号固定为只读账号，避免数据库误分配管理员角色后拥有写权限。
        if (isReadonlyTestUser(user)) {
            result.setRoles(Collections.singletonList("R_USER"));
            result.setButtons(new ArrayList<>());
            return result;
        }

        result.setRoles(resolveRoles(user));
        result.setButtons(resolveButtons(result.getRoles()));
        return result;
    }

    /**
     * 判断当前用户是否允许执行新增、编辑、删除等写操作。
     */
    public boolean hasWritePermission(User user) {
        if (user == null) {
            return false;
        }
        if (isReadonlyTestUser(user)) {
            return false;
        }

        List<String> roles = resolveRoles(user);
        return roles.contains("R_SUPER") || roles.contains("R_ADMIN");
    }

    private boolean isReadonlyTestUser(User user) {
        return user != null
                && StrUtil.isNotBlank(user.getUsername())
                && READONLY_TEST_USERNAME.equalsIgnoreCase(user.getUsername().trim());
    }

    public PageResult<UserListItem> page(UserQuery query) {
        normalizePage(query);
        List<UserListItem> records = userMapper.selectPage(query);
        Long total = userMapper.selectCount(query);
        return new PageResult<>(records, query.getCurrent(), query.getSize(), total);
    }

    public void createUser(UserSaveRequest request) {
        User user = buildUserFromRequest(request, false);
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            throw new CustomException("400", "新增用户时用户名和密码不能为空");
        }
        User exists = userMapper.selectByName(user);
        if (exists != null) {
            throw new CustomException("500", "用户名已存在");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setUsercode(System.currentTimeMillis() + "_" + request.getPassword());
        user.setCreatedAt(now());
        user.setUpdatedAt(now());
        userMapper.insertUser(user);
    }

    public void updateUser(Integer id, UserSaveRequest request) {
        if (id == null) {
            throw new CustomException("400", "用户ID不能为空");
        }
        User dbUser = userMapper.selectById(id);
        if (dbUser == null) {
            throw new CustomException("500", "用户不存在");
        }

        User user = buildUserFromRequest(request, true);
        user.setId(id);
        user.setUpdatedAt(now());

        if (StrUtil.isNotBlank(request.getPassword())) {
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        userMapper.updateUser(user);
    }

    public void deleteUser(Integer id) {
        if (id == null) {
            throw new CustomException("400", "用户ID不能为空");
        }
        userMapper.deleteById(id);
    }

    public void updateStatus(Integer id, String status) {
        if (id == null) {
            throw new CustomException("400", "用户ID不能为空");
        }
        if (!"1".equals(status) && !"0".equals(status)) {
            throw new CustomException("400", "用户状态只能是 1 或 0");
        }
        userMapper.updateStatus(id, status);
    }

    public void assignRole(Integer id, Integer roleId) {
        if (id == null || roleId == null) {
            throw new CustomException("400", "用户ID和角色ID不能为空");
        }
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new CustomException("500", "角色不存在");
        }
        if (Boolean.FALSE.equals(role.getEnabled())) {
            throw new CustomException("500", "该角色已禁用，不能分配");
        }
        userMapper.assignRole(id, roleId);
    }

    private User checkLogin(Account account) {
        if (account == null || StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())) {
            throw new CustomException("400", "请输入用户名和密码");
        }
        User dbuser = userMapper.selectByName(account);
        if (dbuser == null) {
            throw new CustomException("500", "账号不存在");
        }
        if ("0".equals(dbuser.getStatus())) {
            throw new CustomException("500", "账号已被禁用，请联系管理员");
        }
        if (StrUtil.isBlank(dbuser.getPassword()) || !BCrypt.checkpw(account.getPassword(), dbuser.getPassword())) {
            throw new CustomException("500", "账号或密码错误");
        }
        return dbuser;
    }

    private void updateLoginIp(User dbuser) {
        LoginUserResult loginUserResult = new LoginUserResult();
        loginUserResult.setId(dbuser.getId());
        loginUserResult.setIp(IpUtils.getClientIpAddress());
        String address = "";
        try {
            address = IpUtils.getIpPossessionByFile(loginUserResult.getIp());
        } catch (Exception ignored) {
        }
        loginUserResult.setAddress(address);
        userMapper.updateIp(loginUserResult);
    }

    private List<String> resolveRoles(User user) {
        List<String> roles = new ArrayList<>();
        if (user.getRoleId() != null) {
            Role role = roleMapper.selectById(user.getRoleId());
            if (role != null && Boolean.TRUE.equals(role.getEnabled()) && StrUtil.isNotBlank(role.getRoleCode())) {
                roles.add(role.getRoleCode());
            }
        }
        if (roles.isEmpty()) {
            roles.add("R_USER");
        }
        return roles;
    }

    private List<String> resolveButtons(List<String> roles) {
        List<String> buttons = new ArrayList<>();
        if (roles.contains("R_SUPER")) {
            buttons.add("system:user:add");
            buttons.add("system:user:edit");
            buttons.add("system:user:delete");
            buttons.add("system:role:add");
            buttons.add("system:role:edit");
            buttons.add("system:role:delete");
        } else if (roles.contains("R_ADMIN")) {
            buttons.add("system:user:add");
            buttons.add("system:user:edit");
        }
        return buttons;
    }

    private User buildUserFromRequest(UserSaveRequest request, boolean update) {
        if (request == null) {
            throw new CustomException("400", "用户信息不能为空");
        }
        User user = new User();
        user.setUsername(trimToNull(request.getUserName()));
        user.setName(trimToNull(request.getNickName()));
        user.setPhone(trimToNull(request.getUserPhone()));
        user.setEmail(trimToNull(request.getUserEmail()));
        user.setAvatarUrl(trimToNull(request.getAvatar()));
        user.setStatus(StrUtil.isBlank(request.getStatus()) ? "1" : request.getStatus());
        user.setRoleId(request.getRoleId() == null ? 3 : request.getRoleId());
        user.setGenderId(resolveGender(request.getUserGender()));
        if (!update || StrUtil.isNotBlank(request.getPassword())) {
            user.setPassword(request.getPassword());
        }
        return user;
    }

    private Integer resolveGender(String userGender) {
        if (StrUtil.isBlank(userGender)) {
            return null;
        }
        if ("男".equals(userGender) || "1".equals(userGender)) {
            return 1;
        }
        if ("女".equals(userGender) || "2".equals(userGender)) {
            return 2;
        }
        return null;
    }

    private String trimToNull(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    private void normalizePage(UserQuery query) {
        if (query.getCurrent() == null || query.getCurrent() < 1) {
            query.setCurrent(1);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10);
        }
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
