package com.team1206.pos.service.schedule;

import com.team1206.pos.common.dto.WorkHoursDTO;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.common.enums.UserRoles;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.user.User;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.*;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    // Create schedule for user
    public List<Schedule> createScheduleEntities(Map<DayOfWeek, WorkHoursDTO> requestSchedule, User user) {
        return getScheduleList(requestSchedule, user, null);
    }

    // Create schedule for merchant
    public List<Schedule> createScheduleEntities(Map<DayOfWeek, WorkHoursDTO> requestSchedule, Merchant merchant) {
        return getScheduleList(requestSchedule, null, merchant);
    }

    // Set schedule entities with validation
    private List<Schedule> getScheduleList(Map<DayOfWeek, WorkHoursDTO> requestSchedule, User user, Merchant merchant) {
        if (requestSchedule == null || requestSchedule.isEmpty()) {
            throw new IllegalArgumentException("Request schedule cannot be null or empty.");
        }

        List<Schedule> schedules = new ArrayList<>();
        Set<DayOfWeek> daysProcessed = new HashSet<>(); // To track processed days for duplicates

        for (Map.Entry<DayOfWeek, WorkHoursDTO> entry : requestSchedule.entrySet()) {
            DayOfWeek dayOfWeek = entry.getKey();
            WorkHoursDTO workHours = entry.getValue();

            // Validate if day is duplicated
            if (daysProcessed.contains(dayOfWeek)) {
                throw new IllegalArgumentException("Duplicate day found in the schedule: " + dayOfWeek);
            }
            daysProcessed.add(dayOfWeek);

            // If work hours are not provided for this day, leave them as null
            if (workHours != null && workHours.getStartTime() != null && workHours.getEndTime() != null) {
                // Validate work hours
                if (workHours.getEndTime().isBefore(workHours.getStartTime()) || workHours.getEndTime().equals(workHours.getStartTime())) {
                    throw new IllegalArgumentException("End time must be later than start time for day: " + dayOfWeek);
                }
            } else {
                // If work hours are not provided, we leave them as null in the schedule entity
                workHours = null;
            }

            // Create the schedule entity with startTime and endTime being null if no work hours are provided
            Schedule schedule = new Schedule();
            if (user != null && merchant == null) {
                schedule.setUser(user);
            } else if (user == null && merchant != null) {
                schedule.setMerchant(merchant);
            }
            schedule.setDayOfWeek(dayOfWeek);
            if (workHours != null) {
                schedule.setStartTime(workHours.getStartTime());
                schedule.setEndTime(workHours.getEndTime());
            } else {
                schedule.setStartTime(null);  // No work hours provided
                schedule.setEndTime(null);    // No work hours provided
            }
            schedules.add(schedule);
        }

        return schedules;
    }

    // Get work hours (schedule) for a specific day for a user
    public List<Schedule> getUserScheduleByDay(UUID userId, DayOfWeek dayOfWeek) {
        List<Schedule> schedules = scheduleRepository.findByUserIdAndDayOfWeek(userId, dayOfWeek);

        if (schedules == null || schedules.isEmpty()) {
            throw new ResourceNotFoundException(ResourceType.SCHEDULE, "userId: " + userId + " on " + dayOfWeek);
        }

        return schedules;
    }

    // Get work hours (schedule) for a specific day for a merchant
    public List<Schedule> getMerchantScheduleByDay(UUID merchantId, DayOfWeek dayOfWeek) {
        List<Schedule> schedules = scheduleRepository.findByMerchantIdAndDayOfWeek(merchantId, dayOfWeek);

        if (schedules == null || schedules.isEmpty()) {
            throw new ResourceNotFoundException(ResourceType.SCHEDULE, "merchantId: " + merchantId + " on " + dayOfWeek);
        }
        return schedules;
    }

    // Get work hours (schedule) for all days for a merchant
    private List<Schedule> getMerchantSchedule(UUID merchantId) {
        List<Schedule> merchantSchedules = scheduleRepository.findByMerchantId(merchantId);
        if (merchantSchedules == null || merchantSchedules.isEmpty()) {
            throw new IllegalArgumentException("A merchant must have a schedule assigned.");
        }
        return merchantSchedules;
    }

    private void validateUserScheduleAgainstMerchantSchedule(
            Map<DayOfWeek, WorkHoursDTO> userSchedule,
            List<Schedule> merchantSchedules) {

        Map<DayOfWeek, Schedule> merchantScheduleMap = new HashMap<>();
        for (Schedule schedule : merchantSchedules) {
            merchantScheduleMap.put(schedule.getDayOfWeek(), schedule);
        }

        for (Map.Entry<DayOfWeek, WorkHoursDTO> entry : userSchedule.entrySet()) {
            DayOfWeek dayOfWeek = entry.getKey();
            WorkHoursDTO userWorkHours = entry.getValue();

            Schedule merchantSchedule = merchantScheduleMap.get(dayOfWeek);

            // If the merchant does not operate on this day (no working hours), allow the user to have null hours
            if (merchantSchedule == null || merchantSchedule.getStartTime() == null || merchantSchedule.getEndTime() == null) {
                // If the user doesn't have work hours defined for this day, it's okay
                if (userWorkHours != null && (userWorkHours.getStartTime() != null || userWorkHours.getEndTime() != null)) {
                    throw new IllegalArgumentException("User's schedule cannot include day: " + dayOfWeek +
                            " because the merchant does not operate on this day.");
                }
            } else {
                // Validate user work hours against merchant hours if the merchant operates on this day
                if (userWorkHours != null && userWorkHours.getStartTime() != null && userWorkHours.getEndTime() != null) {
                    if (userWorkHours.getStartTime().isBefore(merchantSchedule.getStartTime()) ||
                            userWorkHours.getEndTime().isAfter(merchantSchedule.getEndTime())) {
                        throw new IllegalArgumentException("User's schedule on " + dayOfWeek +
                                " exceeds the merchant's operating hours.");
                    }
                }
            }
        }
    }


    // TODO like metodai tikriausiai nereikalingi bus, kol kas palieku
    // Get work hours (schedule) for all days for a user
    public List<Schedule> getUserScheduleForAllDays(UUID userId) {
        return scheduleRepository.findByUserId(userId);
    }
}
