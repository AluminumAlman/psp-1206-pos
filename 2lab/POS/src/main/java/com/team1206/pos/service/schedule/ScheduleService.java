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
        if (user.getRole() == UserRoles.EMPLOYEE) {
            return getScheduleList(requestSchedule, user, null);
        }
        return null;
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


    // TODO like metodai tikriausiai nereikalingi bus, kol kas palieku
    // Get work hours (schedule) for all days for a user
    public List<Schedule> getUserScheduleForAllDays(UUID userId) {
        return scheduleRepository.findByUserId(userId);
    }

    // Get work hours (schedule) for all days for a merchant
    public List<Schedule> getMerchantScheduleForAllDays(UUID merchantId) {
        return scheduleRepository.findByMerchantId(merchantId);
    }

    /*
    // Delete a schedule by ID
    public void deleteSchedule(UUID scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ResourceNotFoundException(ResourceType.SCHEDULE, scheduleId.toString());
        }
        scheduleRepository.deleteById(scheduleId);
    }

    // Update a schedule for a specific day for a user or merchant
    public Schedule updateSchedule(UUID scheduleId, Schedule updatedSchedule) {
        return scheduleRepository.findById(scheduleId)
                .map(existingSchedule -> {
                    // Validate association consistency
                    if ((existingSchedule.getUser() != null && updatedSchedule.getMerchant() != null) ||
                            (existingSchedule.getMerchant() != null && updatedSchedule.getUser() != null)) {
                        throw new IllegalArgumentException("A schedule must be associated with either a user or a merchant, but not both.");
                    }

                    // Update fields based on the association type
                    if (existingSchedule.getUser() != null) {
                        existingSchedule.setUser(updatedSchedule.getUser());
                    } else if (existingSchedule.getMerchant() != null) {
                        existingSchedule.setMerchant(updatedSchedule.getMerchant());
                    }

                    existingSchedule.setDayOfWeek(updatedSchedule.getDayOfWeek());
                    existingSchedule.setStartTime(updatedSchedule.getStartTime());
                    existingSchedule.setEndTime(updatedSchedule.getEndTime());

                    return scheduleRepository.save(existingSchedule);
                }).orElseThrow(() -> new ResourceNotFoundException(ResourceType.SCHEDULE, scheduleId.toString()));
    }

    // Bulk update schedules for all days for a user
    public List<Schedule> updateUserSchedulesForAllDays(UUID userId, List<Schedule> updatedSchedules) {
        List<Schedule> existingSchedules = scheduleRepository.findByUserId(userId);
        validateScheduleConsistency(existingSchedules, updatedSchedules);

        for (int i = 0; i < existingSchedules.size(); i++) {
            Schedule existingSchedule = existingSchedules.get(i);
            Schedule updatedSchedule = updatedSchedules.get(i);

            existingSchedule.setStartTime(updatedSchedule.getStartTime());
            existingSchedule.setEndTime(updatedSchedule.getEndTime());
            scheduleRepository.save(existingSchedule);
        }
        return existingSchedules;
    }

    // Bulk update schedules for all days for a merchant
    public List<Schedule> updateMerchantSchedulesForAllDays(UUID merchantId, List<Schedule> updatedSchedules) {
        List<Schedule> existingSchedules = scheduleRepository.findByMerchantId(merchantId);
        validateScheduleConsistency(existingSchedules, updatedSchedules);

        for (int i = 0; i < existingSchedules.size(); i++) {
            Schedule existingSchedule = existingSchedules.get(i);
            Schedule updatedSchedule = updatedSchedules.get(i);

            existingSchedule.setStartTime(updatedSchedule.getStartTime());
            existingSchedule.setEndTime(updatedSchedule.getEndTime());
            scheduleRepository.save(existingSchedule);
        }
        return existingSchedules;
    }

    // Delete schedules for a specific day for a user
    public void deleteUserSchedulesByDay(UUID userId, DayOfWeek dayOfWeek) {
        List<Schedule> schedules = scheduleRepository.findByUserIdAndDayOfWeek(userId, dayOfWeek);
        if (schedules.isEmpty()) {
            throw new ResourceNotFoundException(ResourceType.SCHEDULE, "No schedules found for the specified user and day.");
        }
        scheduleRepository.deleteAll(schedules);
    }

    // Delete schedules for a specific day for a merchant
    public void deleteMerchantSchedulesByDay(UUID merchantId, DayOfWeek dayOfWeek) {
        List<Schedule> schedules = scheduleRepository.findByMerchantIdAndDayOfWeek(merchantId, dayOfWeek);
        if (schedules.isEmpty()) {
            throw new ResourceNotFoundException(ResourceType.SCHEDULE, "No schedules found for the specified merchant and day.");
        }
        scheduleRepository.deleteAll(schedules);
    }

    // Helper method to validate consistency of schedule updates
    private void validateScheduleConsistency(List<Schedule> existingSchedules, List<Schedule> updatedSchedules) {
        if (existingSchedules.size() != updatedSchedules.size()) {
            throw new IllegalArgumentException("The number of schedules provided does not match the existing schedules.");
        }
    }
    */
}
