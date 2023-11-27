package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理SQL异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    //相当于重载的方法 粘贴异常的类型传递进来
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();//获得异常信息
        if (message.contains("Duplicate entry")){//如果包含"重复的键值对"
            //动态地把username字符串动态地提取出来
            String[] split = message.split(" ");//得到数组对象
            String username = split[2];//通过下标得到第三个字符
            String msg = username+MessageConstant.ALREADY_EXISTS;//提示信息:用户名已存在
            return Result.error(msg);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);//如果不是这个错误,返回"未知错误"
        }
    }

}
