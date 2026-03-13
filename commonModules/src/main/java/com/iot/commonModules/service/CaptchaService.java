package com.iot.commonModules.service;

import com.iot.commonModules.request.LoginRequest;

import jakarta.servlet.http.HttpServletResponse;

public interface CaptchaService {

    void captcha(LoginRequest loginRequest, HttpServletResponse response);
}