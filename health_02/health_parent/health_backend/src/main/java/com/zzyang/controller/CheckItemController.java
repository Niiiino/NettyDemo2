package com.zzyang.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.zzyang.constant.MessageConstant;
import com.zzyang.entity.PageResult;
import com.zzyang.entity.QueryPageBean;
import com.zzyang.entity.Result;
import com.zzyang.pojo.CheckItem;
import com.zzyang.service.CheckItemService;
import com.zzyang.utils.PageQueryUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/checkItem")
public class CheckItemController {

    @Reference
    private CheckItemService checkItemService;

    /**
     * 添加检查项
     *
     * @param checkItem
     * @return
     */
    @RequestMapping(value = "/addCheckItem")
    @PreAuthorize("hasAuthority('CHECKITEM_ADD')")
    public Result addCheckItem(@RequestBody CheckItem checkItem) {
        //判断数据是否为空
        if (checkItem != null) {
            try {
                checkItemService.addCheckItem(checkItem);
                return new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new Result(false, MessageConstant.ADD_CHECKITEM_FAIL);
    }

    /**
     * 通过分页条件查询检查项
     *
     * @param queryPageBean
     * @return
     */
    @RequestMapping(value = "/findCheckItemsByPageAndCondition")
    @PreAuthorize("hasAuthority('CHECKITEM_QUERY')")
    public PageResult findCheckItemsByPageAndCondition(QueryPageBean queryPageBean) {
        boolean flag = PageQueryUtils.checkPageQueryParam(queryPageBean);
        if (flag) {
            return checkItemService.findCheckItemsByPageAndCondition(queryPageBean);
        }

        return null;
    }

    /**
     * 通过id查询检查项
     *
     * @param checkItemId
     * @return
     */
    @RequestMapping("/findCheckItemByCheckItemId")
    @PreAuthorize("hasAuthority('CHECKITEM_QUERY')")
    public CheckItem findCheckItemByCheckItemId(Integer checkItemId) {
        return checkItemService.findCheckItemByCheckItemId(checkItemId);
    }

    /**
     * 更新检查项
     *
     * @param checkItem
     * @return
     */
    @RequestMapping(value = "/updateCheckItem", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('CHECKITEM_EDIT')")
    public Result updateCheckItem(@RequestBody CheckItem checkItem) {
        //判断是否为空
        if (checkItem != null) {
            //判断是否修改成功
            try {
                checkItemService.updateCheckItem(checkItem);
                return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new Result(false, MessageConstant.EDIT_CHECKITEM_FAIL);
    }

    /**
     * 根据id删除检查项
     *
     * @param checkItemId
     * @return
     */
    @RequestMapping(value = "/deleteCheckItemByCheckItemId")
    @PreAuthorize("hasAuthority('CHECKITEM_DELETE')")
    public Result deleteCheckItemByCheckItemId(Integer checkItemId) {
        //判断参数是否符合规则
        if (checkItemId != null && checkItemId > 0) {
            try {
                //判断是否删除成功
                checkItemService.deleteCheckItemByCheckItemId(checkItemId);
                return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new Result(true, MessageConstant.DELETE_CHECKITEM_FAIL);
    }

    /**
     * 查询所有检查项
     *
     * @return
     */
    @RequestMapping("/findAllCheckItems")
    @PreAuthorize("hasAuthority('CHECKITEM_QUERY')")
    public List<CheckItem> findAllCheckItems() {
        return checkItemService.findAllCheckItems();
    }

}
