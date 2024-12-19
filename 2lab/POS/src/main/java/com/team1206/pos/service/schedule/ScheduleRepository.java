package com.team1206.pos.service.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    // Find schedules for a specific user and day
    List<Schedule> findByUserIdAndDayOfWeek(UUID userId, DayOfWeek dayOfWeek);

    // Find schedules for a specific merchant and day
    List<Schedule> findByMerchantIdAndDayOfWeek(UUID merchantId, DayOfWeek dayOfWeek);

    List<Schedule> findByUserId(UUID userId);

    // Find all schedules for a specific merchant
    List<Schedule> findByMerchantId(UUID merchantId);
}
