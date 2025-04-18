package io.chenyiax.controller;

import io.chenyiax.entity.LoginRequest;
import io.chenyiax.entity.RestBean;
import io.chenyiax.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @Autowired
    LoginService loginService;

    @PostMapping("/login")
    public RestBean<String> login(@RequestBody LoginRequest request) {
        return RestBean.success(loginService.login(request.getCode()));
    }
}
