package com.example.myproject.utils;


public class Result<T> {
    private String code;
    private String msg;
    private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Result() {
    }

    public Result(T data) {
        this.data = data;
    }

    public static Result success() {
        Result result = new Result<>();
        result.setCode("0");
        result.setMsg("成功");
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>(data);
        result.setCode("0");
        result.setMsg("成功");
        return result;
    }
    public static <T> Result<T> success(String msg) {
        Result result = new Result();
        result.setCode("0");
        result.setMsg("成功");
        return result;
    }

    public static <T> Result<T> success(T data,String msg) {
        Result<T> result = new Result<>(data);
        result.setCode("0");
        result.setMsg(msg);
        return result;
    }

    public static Result error(String code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
    /**
     * 创建一个表示文件大小超出限制的结果对象。
     *
     * @return 构造的文件过大错误结果对象
     */
    public static Result fileTooLarge() {
        Result result = new Result();
        result.setCode("413");
        result.setMsg("文件大小超出限制。");
        return result;
    }

    /**
     * 创建一个表示文件格式不支持的结果对象。
     *
     * @return 构造的文件格式错误结果对象
     */
    public static Result unsupportedFileType() {
        Result result = new Result();
        result.setCode("415");
        result.setMsg("不支持的文件格式。");
        return result;
    }

    /**
     * 创建一个表示文件未找到的结果对象。
     *
     * @return 构造的文件未找到错误结果对象
     */
    public static Result fileNotFound() {
        Result result = new Result();
        result.setCode("404");
        result.setMsg("文件未找到。");
        return result;
    }

    /**
     * 创建一个表示文件存储错误的结果对象。
     *
     * @param errorMsg 详细错误信息
     * @return 构造的文件存储错误结果对象
     */
    public static Result fileStorageError(String errorMsg) {
        Result result = new Result();
        result.setCode("500");
        result.setMsg("文件存储错误: " + errorMsg);
        return result;
    }

    public static Result permissionDenied() {
        Result result = new Result();
        result.setCode("401");
        result.setMsg("权限不足，无法执行该操作。");
        return result;
    }

}
