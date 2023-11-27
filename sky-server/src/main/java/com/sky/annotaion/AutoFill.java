package com.sky.annotaion;

import com.sky.enumeration.OperationType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//自动填充
//自定义注解autofile,标识需要进行公共字段的方法
//自定义切面类autofillaspect,统一拦截autofill注解的方法,通过反射为公共字段赋值
//再mapper的方法上加入autofill注解
//creat_time,creat_user,update_time,update_user
/**
 * 自定义注解，用于标识某个方法需要进行功能字段自动填充处理
 */
@Target(ElementType.METHOD)//指定当前注解加在什么位置--只能加在方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
//指定数据库操作的类型
    //标记数据库操作类型：UPDATE INSERT
    OperationType value();

}
