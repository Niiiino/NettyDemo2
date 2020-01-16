package com.zzyang.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zzyang.constant.MessageConstant;
import com.zzyang.entity.PageResult;
import com.zzyang.entity.QueryPageBean;
import com.zzyang.entity.Result;
import com.zzyang.pojo.Order;
import com.zzyang.service.OrderService;
import com.zzyang.utils.SpecialCharactersUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/addOrder")
    public Result addOrder(@RequestBody Map map) {
        map.put("orderType",Order.ORDERTYPE_TELEPHONE);
        Result result = orderService.handleOrder(map);
        if (result.isFlag()) {
            return new Result(true, MessageConstant.ORDER_SUCCESS);
        }
        return result;
    }

    @RequestMapping("/findOrderByPageAndCondition")
    public PageResult findOrderByPageAndCondition(QueryPageBean queryPageBean) {
        String queryString = queryPageBean.getQueryString();
        if (queryString != null) {
            queryPageBean.setQueryString(SpecialCharactersUtils.filterSpecialCharacter(queryString));
        }
        return orderService.findOrderByPageAndCondition(queryPageBean);
    }

}
