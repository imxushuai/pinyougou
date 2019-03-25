package entity;

import java.io.Serializable;

/**
 * 表现层返回给前端的结果
 * Author xushuai
 * Description
 */
public class Result implements Serializable {

    /** 是否成功 */
    private Boolean success;
    /** 提示消息 */
    private String message;

    public Result() {}

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    /**
     * 返回成功
     *
     * @return entity.Result
     */
    public static Result success() {
        return new Result(true, "成功");
    }

    /**
     * 返回成功，自定义提示消息
     * 
     * @param message 提示消息
     * @return entity.Result 
     */
    public static Result success(String message) {
        return new Result(true, message);
    }

    /**
     * 返回失败
     *
     * @return entity.Result
     */
    public static Result errpr() {
        return new Result(false, "失败");
    }

    /**
     * 返回失败，自定义提示消息
     *
     * @param message 提示消息
     * @return entity.Result
     */
    public static Result error(String message) {
        return new Result(false, message);
    }






}

