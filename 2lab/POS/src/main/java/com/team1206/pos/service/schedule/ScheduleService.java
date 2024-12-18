package com.team1206.pos.service.schedule;

import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.user.user.UserService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;

    public ScheduleService(ScheduleRepository scheduleRepository, UserService userService) {
        this.scheduleRepository = scheduleRepository;
        this.userService = userService;
    }

    // Create a new schedule
    public Schedule createSchedule(Schedule schedule) {
        userService.verifyLoggedInUserBelongsToMerchant(schedule.getMerchant().getId(), "You are not authorized to create this schedule!");
        return scheduleRepository.save(schedule);
    }

    // Retrieve a schedule by its ID
    public Schedule getScheduleById(UUID id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.SCHEDULE, id.toString()));
        userService.verifyLoggedInUserBelongsToMerchant(schedule.getMerchant().getId(), "You are not authorized to access this schedule!");
        return schedule;
    }

    // Retrieve all schedules
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    // Update an existing schedule
    public Schedule updateSchedule(UUID id, Schedule updatedSchedule) {
        return scheduleRepository.findById(id)
                .map(existingSchedule -> {
                    existingSchedule.setUser(updatedSchedule.getUser());
                    existingSchedule.setMerchant(updatedSchedule.getMerchant());
                    existingSchedule.setDayOfWeek(updatedSchedule.getDayOfWeek());
                    existingSchedule.setStartTime(updatedSchedule.getStartTime());
                    existingSchedule.setEndTime(updatedSchedule.getEndTime());
                    return scheduleRepository.save(existingSchedule);
                }).orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + id));
    }

    // Delete a schedule
    public void deleteSchedule(UUID id) {
        scheduleRepository.deleteById(id);
    }

    // Get work hours for a user from a particular merchant on a specific day
    public List<Schedule> getWorkHours(UUID userId, UUID merchantId, DayOfWeek dayOfWeek) {
        return scheduleRepository.findByUserIdAndMerchantIdAndDayOfWeek(userId, merchantId, dayOfWeek);
    }
}
