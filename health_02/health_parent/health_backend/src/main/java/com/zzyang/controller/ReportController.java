package com.zzyang.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zzyang.constant.MessageConstant;
import com.zzyang.entity.Result;
import com.zzyang.service.MemberService;
import com.zzyang.service.ReportService;
import com.zzyang.utils.DateUtils;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    private MemberService memberService;

    @Reference
    private ReportService reportService;

    @RequestMapping("/getMemberReport")
    public Result getMemberReport() {
        //获取一年前的日历对象
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -12);
        Date passDate = calendar.getTime();

        //获取当前日期对象
        Date currentDate = new Date();

        //转换日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        //获取月份的集合
        List<String> months = null;
        try {
            months = DateUtils.getMonthBetween(sdf.format(passDate), sdf.format(currentDate), "yyyy.MM");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //获取月份对应会员数量的集合
        List<Integer> memberCount = memberService.findMemberNumberByMonths(months);

        if (memberCount != null && memberCount.size() > 0 && months != null) {
            //封装返回的数据
            Map<String, Object> map = new HashMap<>();
            map.put("months", months.toArray());
            map.put("memberCount", memberCount.toArray());
            return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS, map);
        }

        return new Result(false, MessageConstant.GET_MEMBER_NUMBER_REPORT_FAIL);
    }

    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport() {
        Map setmealReport = reportService.getSetmealReport();
        if (setmealReport != null && setmealReport.size() > 0) {
            return new Result(true, MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS, setmealReport);
        }

        return new Result(false, MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);
    }

    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData() {
        Map businessReportData = reportService.getBusinessReportData();
        if (businessReportData != null && businessReportData.size() > 0) {
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, businessReportData);
        }

        return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
    }

    @RequestMapping("/exportBusinessExcelReport")
    public void exportBusinessExcelReport(HttpServletResponse response, HttpSession session) {
        //获取表格要填写的数据
        Map result = reportService.getBusinessReportData();
        if (result == null || result.size() == 0) {
            return;
        }
        //普通文本数据
        String reportDate = (String) result.get("reportDate");
        Integer todayNewMember = (Integer) result.get("todayNewMember");
        Integer totalMember = (Integer) result.get("totalMember");
        Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
        Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
        Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
        Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
        Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
        Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
        Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
        Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
        //热门套餐数据
        List hotSetmeal = (List) result.get("hotSetmeal");

        //获取ServletContext对象，用来获取文件的mime类型和文件的绝对路径
        ServletContext servletContext = session.getServletContext();
        String templateRealPath = servletContext.getRealPath("/template/report_template.xlsx");

        //创建工作簿
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(templateRealPath);
            //根据索引获取工作页
            XSSFSheet sheet = workbook.getSheetAt(0);
            XSSFRow row3 = sheet.getRow(2);
            row3.getCell(5).setCellValue(reportDate);
            XSSFRow row5 = sheet.getRow(4);
            row5.getCell(5).setCellValue(todayNewMember);
            row5.getCell(7).setCellValue(totalMember);
            XSSFRow row6 = sheet.getRow(5);
            row6.getCell(5).setCellValue(thisWeekNewMember);
            row6.getCell(7).setCellValue(thisMonthNewMember);
            XSSFRow row8 = sheet.getRow(7);
            row8.getCell(5).setCellValue(todayOrderNumber);
            row8.getCell(7).setCellValue(todayVisitsNumber);
            XSSFRow row9 = sheet.getRow(8);
            row9.getCell(5).setCellValue(thisWeekOrderNumber);
            row9.getCell(7).setCellValue(thisWeekVisitsNumber);
            XSSFRow row10 = sheet.getRow(9);
            row10.getCell(5).setCellValue(thisMonthOrderNumber);
            row10.getCell(7).setCellValue(thisMonthVisitsNumber);

            int rowNum = 12;
            for (Object o : hotSetmeal) {
                XSSFRow row = sheet.getRow(rowNum);
                Map map = (Map) o;
                row.getCell(4).setCellValue((String) map.get("name"));
                row.getCell(5).setCellValue((Long) map.get("setmeal_count"));
                row.getCell(6).setCellValue((BigDecimal) map.get("proportion") + "");
                rowNum++;
            }

            //返回数据
            String mimeType = servletContext.getMimeType("report_template.xlsx");
            response.setContentType(mimeType);
            response.setHeader("Content-disposition", "attachment;filename=" + reportDate + ".xlsx");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/exportBusinessPdfReport")
    public void exportBusinessPdfReport(HttpServletResponse response, HttpSession session) {
        //获取运营统计数据集合
        Map reportData = reportService.getBusinessReportData();
        //获取热门套餐数据
        List hotSetmeal = (List) reportData.get("hotSetmeal");
        //获取日期
        String reportDate = (String) reportData.get("reportDate");

        //获取ServletContext对象
        ServletContext servletContext = session.getServletContext();
        //获取jrxml的真实路径
        String jrxmlPath = servletContext.getRealPath("/template/health_business3.jrxml");
        //获取jrxml的真实路径所在目录,将jasper也放入该目录下
        int index = jrxmlPath.lastIndexOf("\\");
        String jasperPath = jrxmlPath.substring(0, index + 1) + "health_business3.jasper";

        try {
            //编译模板
            JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);
            //填充数据
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, reportData, new JRBeanCollectionDataSource(hotSetmeal));
            //输出文件
            response.setHeader("Content-disposition", "attachment;filename=" + reportDate + ".pdf");
            response.setContentType("application/pdf");
            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
        } catch (JRException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}