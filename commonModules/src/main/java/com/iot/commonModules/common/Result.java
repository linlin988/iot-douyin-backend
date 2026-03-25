package com.iot.commonModules.common;


import lombok.AllArgsConstructor;
import lombok.Data;
//统一返回处理结果，有成功的，也有失败的
@Data
@AllArgsConstructor
public class Result {

    private Integer code;    // 响应码 200=成功 500=失败
    private String message; // 提示信息
    private Object data;    // 返回数据
    private Long total;     // 分页总数（非分页接口为null）



    // 成功

    /**
     * 操作成功
     * 使用示例：return Result.success();
     */
    public static Result success(){
        return new Result(200,"操作成功！",null,null);
    }

    /**
     * 操作成功，自定义状态码 + 自定义提示信息
     * 使用示例：return Result.success(200,"操作成功");
     */
    public static Result success(Integer code,String message){
        return new Result(code,message,null,null);
    }

    /**
     * 操作成功，自定义状态码 + 提示信息 + 总数
     * 使用示例：return Result.success(200,"查询成功",100L);
     */
    public static Result success(Integer code,String message,Long total){
        return new Result(code,message,null,total);
    }

    /**
     * 操作成功，返回数据，使用默认提示
     * 使用示例：return Result.success(user);
     */
    public static Result success(Object data){
        return new Result(200,"222",data,null);
    }

    /**
     * 操作成功，返回数据 + 分页总数
     * 使用示例：return Result.success(videoList, 50L);
     */
    public static Result success(Object data,Long total){
        return new Result(200,"222",data,total);
    }

    /**
     * 操作成功，自定义提示信息，无数据
     * 使用示例：return Result.success("发布视频成功！");
     */
    public static Result success(String message){
        return new Result(200,message,null,null);
    }

    /**
     * 操作成功，自定义提示信息 + 返回数据
     * 使用示例：return Result.success("查询成功", userInfo);
     */
    public static Result success(String message,Object data){
        return new Result(200,message,data,null);
    }

    /**
     * 操作成功，自定义提示 + 数据 + 分页总数（最全）
     * 使用示例：return Result.success("查询成功", list, 100L);
     */
    public static Result success(String message,Object data,Long total){
        return new Result(200,message,data,total);
    }

//操作失败

    /**
     * 操作失败，默认500错误，无提示信息
     * 使用示例：return Result.error();
     */
    public static Result error(String 头像上传失败){
        return new Result(500,null,null,null);
    }

    /**
     * 操作失败，自定义状态码 + 错误信息
     * 使用示例：return Result.error(400,"参数不能为空");
     */
    public static Result error(Integer code,String message){
        return new Result(code,message,null,null);
    }
}