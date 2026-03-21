package com.iot.content.config;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class AliyunOSSOperator {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    public String upload(byte[] content, String originalFilename) throws Exception {
        // 按日期生成目录
        String dir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + suffix;
        String objectName = dir + "/" + newFileName;


        OSS ossClient = new OSSClientBuilder().build(
                endpoint,
                accessKeyId,
                accessKeySecret
        );

        try {
            // 上传
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content));
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // 返回正确的访问地址
        return "https://" + bucketName + "." + endpoint + "/" + objectName;
    }
}