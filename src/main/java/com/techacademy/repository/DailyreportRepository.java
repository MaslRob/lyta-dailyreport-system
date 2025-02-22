package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Dailyreport;

public interface DailyreportRepository extends JpaRepository<Dailyreport, String> {
}