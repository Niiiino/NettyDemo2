package com.zzyang.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zzyang.constant.MessageConstant;
import com.zzyang.entity.PageResult;
import com.zzyang.entity.QueryPageBean;
import com.zzyang.entity.Result;
import com.zzyang.pojo.CheckGroup;
import com.zzyang.service.CheckGroupService;
import com.zzyang.utils.PageQueryUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/checkGroup")
public class CheckGroupController {

    @Reference
    private CheckGroupService checkGroupService;

    @RequestMapping("/addCheckGroup")
    @PreAuthorize("hasAuthority('CHECKGROUP_ADD')")
    public Result addCheckGroup(@RequestBody CheckGroup checkGroup, Integer[] checkItemIds) {
        //校验参数
        if (checkGroup != null && checkItemIds != null && checkItemIds.length > 0) {
            //判断是否添加成功
            try {
                checkGroupService.addCheckGroup(checkGroup, checkItemIds);
                return new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new Result(false, MessageConstant.ADD_CHECKGROUP_FAIL);
    }

    @RequestMapping("/findCheckGroupsByPageAndCondition")
    @PreAuthorize("hasAuthority('CHECKGROUP_QUERY')")
    public PageResult findCheckGroupsByPageAndCondition(QueryPageBean queryPageBean) {
        boolean flag = PageQueryUtils.checkPageQueryParam(queryPageBean);
        if (flag) {
            return checkGroupService.findCheckGroupsByPageAndCondition(queryPageBean);
        }

        return null;
    }

    @RequestMapping("/findCheckGroupByCheckGroupId")
    @PreAuthorize("hasAuthority('CHECKGROUP_QUERY')")
    public CheckGroup findCheckGroupByCheckGroupId(Integer checkGroupId) {
        //检验参数
        if (checkGroupId == null || checkGroupId <= 0) {
            return null;
        }
        return checkGroupService.findCheckGroupByCheckGroupId(checkGroupId);
    }

    @RequestMapping("/updateCheckGroup")
    @PreAuthorize("hasAuthority('CHECKGROUP_EDIT')")
    public Result updateCheckGroup(@RequestBody CheckGroup checkGroup, Integer[] checkItemIds) {
        //校验参数
        if (checkGroup != null && checkItemIds != null && checkItemIds.length > 0) {
            //判断检查组是否添加成功
            try {
                checkGroupService.updateCheckGroup(checkGroup, checkItemIds);
                return new Result(true, MessageConstant.EDIT_CHECKGROUP_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new Result(false, MessageConstant.EDIT_CHECKGROUP_FAIL);
    }

    @RequestMapping("/deleteCheckGroupByCheckGroupId")
    @PreAuthorize("hasAuthority('CHECKGROUP_DELETE')")
    public Result deleteCheckGroupByCheckGroupId(Integer checkGroupId) {
        //校验传来的参数
        if (checkGroupId != null && checkGroupId > 0) {
            //判断是否删除成功
            try {
                checkGroupService.deleteCheckGroupByCheckGroupId(checkGroupId);
                return new Result(true, MessageConstant.DELETE_CHECKGROUP_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new Result(false, MessageConstant.DELETE_CHECKGROUP_FAIL);
    }

    @RequestMapping("/findAllCheckGroups")
    @PreAuthorize("hasAuthority('CHECKGROUP_QUERY')")
    public List<CheckGroup> findAllCheckGroups() {
        return checkGroupService.findAllCheckGroups();
    }

}
