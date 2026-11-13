package org.example.base_project.common;

/**
 * 统一返回结构。
 * code 使用 Integer，方便前端 axios 拦截器按 200/401 判断。
 */
public class Result {
    private Integer code;
    private String msg;
    private Object data;

    public static Result success() {
        Result result = new Result();
        result.setCode(200);
        result.setMsg("请求成功");
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.setCode(200);
        result.setData(data);
        result.setMsg("请求成功");
        return result;
    }

    public static Result error() {
        Result result = new Result();
        result.setCode(500);
        result.setMsg("系统错误");
        return result;
    }

    public static Result error(String code, String msg) {
        Result result = new Result();
        try {
            result.setCode(Integer.parseInt(code));
        } catch (Exception e) {
            result.setCode(500);
        }
        result.setMsg(msg);
        return result;
    }

    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
