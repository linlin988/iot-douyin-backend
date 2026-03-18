package com.iot.commonModules.utils.PasswordUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();//创建encoder对象（通过BCrypt算法）
    public String encrypt(String password){//对输入的密码加密，用在用户注册时
        return encoder.encode(password);//.encode()方法用来加密
    }
    public boolean match(String rawPassword,String encodePassword){//用户输入的明文密码和储存的加密密码比对，用在用户登录时
        return encoder.matches(rawPassword,encodePassword);
    }
}
