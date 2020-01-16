package com.zzyang.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zzyang.constant.MessageConstant;
import com.zzyang.entity.Result;
import com.zzyang.pojo.OrderSetting;
import com.zzyang.service.OrderSettingService;
import com.zzyang.utils.POIUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/orderSetting")
public class OrderSettingController {

    @Reference
    private OrderSettingService orderSettingService;

    @RequestMapping("/uploadOrderSettingExcelFile")
    public Result uploadOrderSettingExcelFile(MultipartFile excelFile) {
        if (excelFile != null) {
            try {
                Map<Date, Integer> dateAndNumberMap = POIUtils.getOrderDateAndNumber(excelFile);
                List<OrderSetting> orderSettingList = new ArrayList<>();
                Set<Map.Entry<Date, Integer>> set = dateAndNumberMap.entrySet();
                for (Map.Entry<Date, Integer> entry : set) {
                    orderSettingList.add(new OrderSetting(entry.getKey(), entry.getValue()));
                }
                orderSettingService.importOrderSetting(orderSettingList);
                return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
    }

    @RequestMapping("/getOrderSettingsByYearAndMonth")
    public List<Map> getOrderSettingsByYearAndMonth(String yearAndMonth) {
        //截取日期年和月的字符串，拼接，进行模糊查询做准备
        if (yearAndMonth == null) return null;
        String substring = yearAndMonth.substring(0, 8) + "__";

        //处理数据并返回
        List<OrderSetting> orderSettingList = orderSettingService.getOrderSettingsByYearAndMonth(substring);
        if (orderSettingList == null || orderSettingList.size() == 0) return null;
        List<Map> result = new ArrayList<>();
        for (OrderSetting orderSetting : orderSettingList) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", orderSetting.getOrderDate().getDate()); //获取几号
            map.put("number", orderSetting.getNumber());
            map.put("reservations", orderSetting.getReservations());
            result.add(map);
        }
        return result;

    }

    @RequestMapping("/setOrderSettingByOrderDate")
    public Result setOrderSettingByOrderDate(String orderDate, Integer number) {
        if (orderDate != null && number > 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                orderSettingService.setOrderSettingByOrderDate(sdf.parse(orderDate), number);
                return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return new Result(false, MessageConstant.ORDERSETTING_FAIL);
    }

}
