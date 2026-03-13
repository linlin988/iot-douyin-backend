package com.iot.commonModules.service.Impl;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.iot.commonModules.request.LoginRequest;
import com.iot.commonModules.service.CaptchaService;
import com.iot.commonModules.utils.Redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import javax.imageio.ImageIO;
import jakarta.servlet.ServletOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

import static com.iot.commonModules.common.Constant.CAPTCHA_KEY;
import static com.iot.commonModules.common.Constant.CAPTCHA_TIME_OUT;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private RedisUtil redisUtil;

    @Override

    public void captcha(LoginRequest loginRequest, HttpServletResponse response) {

       /* if (loginRequest == null || !StringUtils.hasText(loginRequest.getUsername())) {
            throw new IllegalArgumentException("生成验证码必须传入有效的用户名");
        }*/

        //禁用浏览器缓存
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        // 生成验证码文本
        String captchaText = defaultKaptcha.createText();
        System.out.println("验证码内容：" + captchaText);

        // 无论username是否为空，都生成验证码并存储，确保前端能够获取有效的captchaKey
        String captchaKey = CAPTCHA_KEY + UUID.randomUUID().toString().replaceAll("-", "");
        if (StringUtils.isNotEmpty(loginRequest.getUsername())) {
            captchaKey = CAPTCHA_KEY + loginRequest.getUsername().trim() + ":" + UUID.randomUUID().toString().replaceAll("-", "");
        }
        redisUtil.set(captchaKey, captchaText, CAPTCHA_TIME_OUT); // 存入Redis
        response.setHeader("Captcha-Key", captchaKey); // 响应头返回Key给前端

        BufferedImage image = defaultKaptcha.createImage(captchaText);
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            ImageIO.write(image, "jpg", outputStream); // 图片写入响应流
            outputStream.flush(); // 确保所有图片数据发送完成
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("验证码生成失败", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close(); //  释放资源，
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
