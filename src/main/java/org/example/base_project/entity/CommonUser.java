package org.example.base_project.entity;

import java.util.Date;

public class CommonUser {


    // 主键ID
    private Long id;

    // 用户名
    private String username;

    // 性别 0-女 1-男
    private Integer gender;

    // 手机号
    private String mobile;

    // 邮箱
    private String email;

    // 部门
    private String dep;

    // 状态 1-正常 2-禁用等
    private String status;

    // 创建时间
    private Date createTime;

    // 头像地址
    private String avatar;

    // 无参构造
    public CommonUser() {
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}