package com.techacademy.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import com.techacademy.entity.Dailyreport;
import com.techacademy.entity.Employee.Role;
import com.techacademy.service.DailyreportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("dailyreport")
public class DailyreportController {

    private final DailyreportService dailyreportService;

    @Autowired
    public DailyreportController(DailyreportService dailyreportService) {
        this.dailyreportService = dailyreportService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {

        List<Dailyreport> allReports = dailyreportService.findAll();

        if ( userDetail.getEmployee().getRole() != Role.ADMIN) {
            List<Dailyreport> myreports = new ArrayList<>();
            for (Dailyreport report : allReports) {
                if( report.getEmployee().getCode().equals(userDetail.getEmployee().getCode()) ) {
                    myreports.add(report);
                }
            }
            model.addAttribute("listSize", myreports.size());
            model.addAttribute("dailyreportList", myreports);
        }else {
            model.addAttribute("listSize", allReports.size());
            model.addAttribute("dailyreportList", allReports);
        }

        model.addAttribute("code", userDetail.getEmployee().getCode());

        return "dailyreport/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{code}/")
    public String detail(@PathVariable("code") String code, Model model) {

        model.addAttribute("dailyreport", dailyreportService.findByCode(code));
        return "dailyreport/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/{code}/add")
    public String create(@PathVariable("code") String code, @ModelAttribute Dailyreport dailyreport, Model model,@AuthenticationPrincipal UserDetail userDetail) {

        model.addAttribute("name", userDetail.getEmployee().getName());
        return "dailyreport/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/{code}/add")
    public String add(@PathVariable("code") String code, @Validated Dailyreport dailyreport, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {

        // 入力チェック
        if (res.hasErrors()) {
            return create(code, dailyreport, model,userDetail);
        }

        ErrorKinds result = dailyreportService.save(code, dailyreport, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(code, dailyreport, model,userDetail);
            }

        return "redirect:/dailyreport";
    }

    // 日報削除処理
    @PostMapping(value = "/{code}/delete")
    public String delete(@PathVariable("code") String code, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = dailyreportService.delete(code, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("dailyreport", dailyreportService.findByCode(code));
            return detail(code, model);
        }

        return "redirect:/dailyreport";
    }

    // 日報更新画面
    @GetMapping(value = "/{code}/update")
    public String update(@PathVariable("code") String code, Model model) {

        model.addAttribute("dailyreport", dailyreportService.findByCode(code));
        return "dailyreport/update";
    }

    // 日報更新処理
    @PostMapping(value = "/{code}/update")
    public String renew(@PathVariable("code") String code, @Validated Dailyreport dailyreport, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        dailyreport.setEmployee(userDetail.getEmployee());
        dailyreport.setId(Integer.parseInt(code));
        // 入力チェック
        if (res.hasErrors()) {
            return "dailyreport/update";
        }

        ErrorKinds result = dailyreportService.update(code, dailyreport, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return "dailyreport/update";
            }

        return "redirect:/dailyreport";
    }

}
