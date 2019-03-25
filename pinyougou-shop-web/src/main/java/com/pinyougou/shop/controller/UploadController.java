package com.pinyougou.shop.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

/**
 * 文件上传控制器
 * Author xushuai
 * Description
 */
@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    /**
     * 文件上传
     *
     * @param file 上传的文件
     * @return entity.Result
     */
    @RequestMapping("/upload")
    public Result upload(MultipartFile file) {
        try {
            // 创建FastDFS工具类对象
            FastDFSClient dfsClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            // 获取扩展名
            String originalFilename = file.getOriginalFilename();
            String exName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            // 执行上传
            String file_id = dfsClient.uploadFile(file.getBytes(), exName);
            file_id = FILE_SERVER_URL + file_id;
            return Result.success(file_id);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }
}

