package com.zzyang.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zzyang.constant.MessageConstant;
import com.zzyang.entity.PageResult;
import com.zzyang.entity.QueryPageBean;
import com.zzyang.entity.Result;
import com.zzyang.pojo.Menu;
import com.zzyang.service.MenuService;
import com.zzyang.service.UserService;
import com.zzyang.utils.PageQueryUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @Reference
    private MenuService menuService;

    @GetMapping("/getUsernameAndMenuList")
    public Result getUsernameAndMenuList() {
        //当登录成功后框架会将用户的信息存在框架提供的上下文对象中
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null) {
            //获取用户名
            String username = user.getUsername();
            //获取用户的菜单列表
            List<Menu> menuList = menuService.findMenusByUsername(username);
            Map<String, Object> result = new HashMap<>();
            result.put("username", username);
            result.put("menuList", menuList);
            return new Result(true, MessageConstant.GET_USERNAME_SUCCESS, result);
        }

        return new Result(false, MessageConstant.GET_USERNAME_FAIL);
    }

    @PostMapping("/updatePassword/{oldPassword}")
    @PreAuthorize("isAuthenticated()")
    public Result updatePassword(@RequestBody com.zzyang.pojo.User user, @PathVariable String oldPassword) {
        if (user.getPassword() != null && oldPassword != null) {
            try {
                //修改密码
                return userService.updatePassword(user, oldPassword);
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false, MessageConstant.UPDATE_PASSWORD_FAIL, MessageConstant.SERVER_IS_BUSY);
            }
        }

        return new Result(false, MessageConstant.UPDATE_PASSWORD_FAIL);
    }

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = userService.findPage( queryPageBean );
        return pageResult;
    }
}
