package com.team1206.pos.service.schedule;

import com.team1206.pos.common.dto.WorkHoursDTO;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.common.enums.UserRoles;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.user.User;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }


    public List<Schedule> createScheduleEntities(Map<DayOfWeek, WorkHoursDTO> requestSchedule, User user) {
        if (user.getRole() == UserRoles.EMPLOYEE) {
            return getScheduleList(requestSchedule, user, null);
        }
        return null;
    }

    public List<Schedule> createScheduleEntities(Map<DayOfWeek, WorkHoursDTO> requestSchedule, Merchant merchant) {
        return getScheduleList(requestSchedule, null, merchant);
    }

    private List<Schedule> getScheduleList(Map<DayOfWeek, WorkHoursDTO> requestSchedule, User user, Merchant merchant) {
        List<Schedule> schedules = new ArrayList<>();

        // Loop through each day of the week and create a Schedule entity if work hours are defined
        for (Map.Entry<DayOfWeek, WorkHoursDTO> entry : requestSchedule.entrySet()) {
            DayOfWeek dayOfWeek = entry.getKey();
            WorkHoursDTO workHours = entry.getValue();

            if (workHours != null && workHours.getStartTime() != null && workHours.getEndTime() != null) {
                Schedule schedule = new Schedule();
                if (user != null) {
                    schedule.setUser(user);
                } else if (merchant != null) {
                    schedule.setMerchant(merchant);
                }
                schedule.setDayOfWeek(dayOfWeek);
                schedule.setStartTime(workHours.getStartTime());
                schedule.setEndTime(workHours.getEndTime());
                schedules.add(schedule);
            }
        }
        return schedules;
    }

    // Create a new schedule for a user or merchant
    public Schedule createSchedule(Schedule schedule) {
        // Validate that the schedule is linked to either a user or a merchant, but not both
        if ((schedule.getUser() == null && schedule.getMerchant() == null) ||
                (schedule.getUser() != null && schedule.getMerchant() != null)) {
            throw new IllegalArgumentException("A schedule must be associated with either a user or a merchant, but not both.");
        }

        // If a user is linked, verify the user belongs to the logged-in user's merchant
        if (schedule.getUser() != null) {
            //userService.verifyLoggedInUserBelongsToMerchant(schedule.getUser().getMerchant().getId(), "You are not authorized to create this schedule!");
        }

        return scheduleRepository.save(schedule);
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

    // Delete a schedule by ID
    public void deleteSchedule(UUID scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ResourceNotFoundException(ResourceType.SCHEDULE, scheduleId.toString());
        }
        scheduleRepository.deleteById(scheduleId);
    }

    // Get work hours (schedule) for a specific day for a user
    public List<Schedule> getUserScheduleByDay(UUID userId, DayOfWeek dayOfWeek) {
        return scheduleRepository.findByUserIdAndDayOfWeek(userId, dayOfWeek);
    }

    // Get work hours (schedule) for all days for a user
    public List<Schedule> getUserScheduleForAllDays(UUID userId) {
        return scheduleRepository.findByUserId(userId);
    }

    // Get work hours (schedule) for a specific day for a merchant
    public List<Schedule> getMerchantScheduleByDay(UUID merchantId, DayOfWeek dayOfWeek) {
        return scheduleRepository.findByMerchantIdAndDayOfWeek(merchantId, dayOfWeek);
    }

    // Get work hours (schedule) for all days for a merchant
    public List<Schedule> getMerchantScheduleForAllDays(UUID merchantId) {
        return scheduleRepository.findByMerchantId(merchantId);
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
}
