package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")//Api注解
@Slf4j//日志
public class CommonController {

    //依赖注入:注入aliyunoss工具类
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);

        try {
            //构造uuid文件名
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀 dfdfdf.png 从点后开始截取
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名
            String objectName = UUID.randomUUID().toString() + extension;

            //文件请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            //调用阿里云工具类的方法(文件对象转成的byte数组,传上的图片在aliyun的名字
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}",e);
    }
        return Result.error(MessageConstant.UPLOAD_FAILED);//失败返回文件上传失败
    }

}
