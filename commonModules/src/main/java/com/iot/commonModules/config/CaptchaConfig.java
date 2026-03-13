package com.iot.commonModules.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/*
    使用说明：
    1.在controller里面注入：
         @Autowired
    private CaptchaService captchaService;

    2.写入一下实现方法
    @PostMapping(value = "/captcha", produces = "image/jpeg")
    public void getCaptcha(@RequestBody LoginRequest  loginRequest, HttpServletResponse response) throws IOException {
          captchaService.captcha(loginRequest,response);
    }

    具体看一看testForMaffile里面的UserTestController，里面有测试类

 */


@Configuration
public class CaptchaConfig {

    @Bean
    public DefaultKaptcha defaultKaptcha() {
        //验证码生成器
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        //配置
        Properties properties = new Properties();
        //是否有边框
        properties.setProperty("kaptcha.border", "yes");
        //设置边框颜色
        properties.setProperty("kaptcha.border.color", "105,179,90");
        //边框粗细度，默认为1
        // properties.setProperty("kaptcha.border.thickness","1");
        //验证码
        properties.setProperty("kaptcha.session.key", "code");
        //验证码文本字符颜色 默认为黑色
        properties.setProperty("kaptcha.textProducer.font.color", "blue");
        //设置字体样式
        properties.setProperty("kaptcha.textProducer.font.names", "宋体,楷体,微软雅黑");
        //字体大小，默认40
        properties.setProperty("kaptcha.textProducer.font.size", "30");
        //验证码文本字符内容范围 默认为abced2345678gfynmnpwx
        // properties.setProperty("kaptcha.textProducer.char.string", "");
        //字符长度，默认为5
        properties.setProperty("kaptcha.textProducer.char.length", "4");
        //字符间距 默认为2
        properties.setProperty("kaptcha.textProducer.char.space", "4");
        //验证码图片宽度 默认为200
        properties.setProperty("kaptcha.image.width", "100");
        //验证码图片高度 默认为40
        properties.setProperty("kaptcha.image.height", "40");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

}
