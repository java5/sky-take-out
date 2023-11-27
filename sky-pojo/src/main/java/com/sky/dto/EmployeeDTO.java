package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

//和前端提交的数据对应上
@Data
public class EmployeeDTO implements Serializable {

    private Long id;

    private String username;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;

}
