package com.zzyang.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zzyang.pojo.Permission;
import com.zzyang.pojo.Role;
import com.zzyang.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component("userService")
public class SpringSecurityUserService implements UserDetailsService {

    @Reference
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户登录查询数据库对应的user对象
        User user = userService.findUserByUsername(username);
        if (user == null) {
            //说明对应的用户名不存在
            return null;
        }

        //角色权限集合
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        //获得用户对应的角色集合
        Set<Role> roles = user.getRoles();
        for (Role role : roles) {
            //添加角色
            grantedAuthorityList.add(new SimpleGrantedAuthority(role.getKeyword()));
            Set<Permission> permissions = role.getPermissions();
            for (Permission permission : permissions) {
                //添加每个角色对应的权限集合
                grantedAuthorityList.add(new SimpleGrantedAuthority(permission.getKeyword()));
            }
        }
        return new org.springframework.security.core.userdetails.User(username,user.getPassword(),grantedAuthorityList);
    }

}
