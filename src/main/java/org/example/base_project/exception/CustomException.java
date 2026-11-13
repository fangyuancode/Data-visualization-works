package org.example.base_project.exception;

public class CustomException extends RuntimeException {
    private String code;
    private String msg;

    //自定义构造器
    //public CustomException(String code, String msg) {
    //    //super(msg);
    //    this.code = code;
    //    this.msg = msg;
    //
    //}
    public CustomException(String code, String msg) {
        super(msg);  // 建议调用父类构造器保存信息
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
