package com.team1206.pos.service.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    List<Schedule> findByUserIdAndMerchantIdAndDayOfWeek(UUID userId, UUID merchantId, DayOfWeek dayOfWeek);
}
