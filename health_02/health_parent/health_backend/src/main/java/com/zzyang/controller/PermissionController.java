package com.zzyang.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zzyang.constant.MessageConstant;
import com.zzyang.entity.PageResult;
import com.zzyang.entity.QueryPageBean;
import com.zzyang.entity.Result;
import com.zzyang.pojo.Order;
import com.zzyang.pojo.Permission;
import com.zzyang.pojo.Role;
import com.zzyang.service.OrderService;
import com.zzyang.service.PermissionService;
import com.zzyang.utils.SpecialCharactersUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Reference
    private PermissionService permissionService;

    //查询用户权限列表
    @RequestMapping("/findAll")
    public Result findAll(){
        try {
            List<Permission> list = permissionService.findAll();
            return new Result( true, "查询用户权限列表成功",list );
        }catch (Exception e){
            //服务调用失败，打印错误信息
            e.printStackTrace();
            return new Result( false, "查询用户权限列表失败");
        }
    }

    //根据权限id查询权限列表
    @RequestMapping("/findPermissionsById")
    public Result findPermissionsById(Integer id){
        try {

            List<Integer> checkitemIds = permissionService.findPermissionIdsByRoleId( id );
            return new Result( true,"查询用户权限列表成功",checkitemIds );
        } catch (Exception e) {
            e.printStackTrace();
            return new Result( false,"查询用户权限列表失败");
        }
    }

    //编辑权限列表
    @RequestMapping("/edit")
    public Result edit(@RequestBody Role role, Integer[] checkitemIds){
        try {
            permissionService.edit(role,checkitemIds );
        }catch (Exception e){
            e.printStackTrace();
            return new Result( false, "修改权限列表失败" );//新增检查组失败
        }
        return new Result( true,"修改权限列表成功");//新增检查组成功
    }



}
