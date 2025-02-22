package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Dailyreport;
import com.techacademy.entity.Employee;
import com.techacademy.repository.DailyreportRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyreportService {

    private final DailyreportRepository dailyreportRepository;

    public DailyreportService(DailyreportRepository dailyreportRepository) {
        this.dailyreportRepository = dailyreportRepository;
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(String code, Dailyreport dailyreport, UserDetail userDetail) {

        // 従業員番号重複かつ日付チェック
        List<Dailyreport> dailyReports = findAll();

        for (Dailyreport report : dailyReports) {
            if(report.getEmployee().getCode().equals(code) && report.getReport_date().equals(dailyreport.getReport_date())) {
                return ErrorKinds.DATECHECK_ERROR;
            }
        }
        dailyreport.setDeleteFlg(false);
        dailyreport.setEmployee(userDetail.getEmployee());
        LocalDateTime now = LocalDateTime.now();
        dailyreport.setCreatedAt(now);
        dailyreport.setUpdatedAt(now);

        dailyreportRepository.save(dailyreport);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(String code, UserDetail userDetail) {

        Dailyreport dailyreport = findByCode(code);
        LocalDateTime now = LocalDateTime.now();
        dailyreport.setUpdatedAt(now);
        dailyreport.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 日報一覧表示処理
    public List<Dailyreport> findAll() {
        return dailyreportRepository.findAll();
    }

    // 1件を検索
    public Dailyreport findByCode(String code) {
        // findByIdで検索
        Optional<Dailyreport> option = dailyreportRepository.findById(code);
        // 取得できなかった場合はnullを返す
        Dailyreport dailyreport = option.orElse(null);
        return dailyreport;
    }

    // 従業員に紐づく日報を検索
    public List<Dailyreport> findByEmployee(Employee employee) {
        List<Dailyreport> allReports = dailyreportRepository.findAll();
        List<Dailyreport> myreports = new ArrayList<>();

        for (Dailyreport report : allReports) {
            if ( employee.getCode().equals(report.getEmployee().getCode())) {
                myreports.add(report);
            }
        }

        return myreports;
    }

    // 日報員更新
    @Transactional
    public ErrorKinds update(String code, Dailyreport dailyreport, UserDetail userDetail) {

        if (! dailyreport.getReport_date().equals(findByCode(code).getReport_date())){
            List<Dailyreport> dailyReports = findAll();
            for (Dailyreport report : dailyReports) {
                if(report.getEmployee().getCode().equals(dailyreport.getEmployee().getCode()) && report.getReport_date().equals(dailyreport.getReport_date())) {
                    return ErrorKinds.DATECHECK_ERROR;
                }
            }
        }
        dailyreport.setId(Integer.parseInt(code));
        dailyreport.setEmployee(userDetail.getEmployee());
        LocalDateTime now = LocalDateTime.now();
        dailyreport.setCreatedAt(findByCode(dailyreport.getEmployee().getCode()).getCreatedAt());
        dailyreport.setUpdatedAt(now);
        dailyreportRepository.save(dailyreport);
        return ErrorKinds.SUCCESS;
    }
}
