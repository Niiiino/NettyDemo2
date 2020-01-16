package com.zzyang.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zzyang.constant.MessageConstant;
import com.zzyang.entity.PageResult;
import com.zzyang.entity.QueryPageBean;
import com.zzyang.entity.Result;
import com.zzyang.pojo.Menu;
import com.zzyang.service.MenuService;
import com.zzyang.service.RoleService;
import com.zzyang.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Reference
   private RoleService roleService;

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = roleService.findPage( queryPageBean );
        return pageResult;
    }
}
