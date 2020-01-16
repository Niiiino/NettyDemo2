package com.zzyang.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zzyang.constant.MessageConstant;
import com.zzyang.entity.PageResult;
import com.zzyang.entity.QueryPageBean;
import com.zzyang.entity.Result;
import com.zzyang.pojo.Setmeal;
import com.zzyang.service.SetmealService;
import com.zzyang.utils.QiuNiuUtils;
import com.zzyang.utils.SpecialCharactersUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;

    @RequestMapping("/findSetmealsByPageAndCondition")
    public PageResult findSetmealsByPageAndCondition(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        if (currentPage == null || currentPage <= 0 || pageSize == null || pageSize <= 0) {
            return null;
        }
        String queryString = queryPageBean.getQueryString();
        queryString = SpecialCharactersUtils.filterSpecialCharacter(queryString);

        return setmealService.findSetmealsByPageAndCondition(currentPage, pageSize, queryString);
    }

    @RequestMapping("/uploadImg")
    public Result uploadImg(MultipartFile imgFile) {
        String originalFilename = imgFile.getOriginalFilename();
        int lastIndex = originalFilename.lastIndexOf('.');
        String extension = originalFilename.substring(lastIndex);
        //随机生成图片名称
        String fileName = UUID.randomUUID().toString() + extension;

        try {
            //上传图片
            byte[] imgFileBytes = imgFile.getBytes();
            QiuNiuUtils.uploadByFileBytes(imgFileBytes, fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
        return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS, fileName);
    }

    @RequestMapping(value = "/addSetmeal")
    public Result addSetmeal(@RequestBody Setmeal setmeal, Integer[] checkGroupIds) {
        if (setmeal != null && checkGroupIds != null && checkGroupIds.length > 0) {
            boolean isSuccessAdd = setmealService.addSetmeal(setmeal, checkGroupIds);
            if (isSuccessAdd) {
                return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);
            }
        }

        return new Result(false, MessageConstant.ADD_SETMEAL_FAIL);
    }

    @RequestMapping("/findAllSetmeal")
    public Result findAllSetmeal() {
        List<Setmeal> setmealList = setmealService.findAllSetmeal();
        if (setmealList != null && setmealList.size() > 0) {
            return new Result(true, MessageConstant.GET_SETMEAL_LIST_SUCCESS, setmealList);
        }

        return new Result(false, MessageConstant.GET_SETMEAL_LIST_FAIL);
    }

}
