package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Reference(timeout = 10000)
    private UserService userService;

    @PostMapping("/save")
    public boolean save(@RequestBody User user,String smsCode){
        try {
            boolean ok = userService.checkSmsCode(user.getPhone(),smsCode);
            if (ok) {
                userService.save(user);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/sendCode")
    public boolean sendGet(String phone){
        try {
            if(StringUtils.isNoneBlank(phone)){
                userService.sendCode(phone);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
