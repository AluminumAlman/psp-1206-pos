package com.team1206.pos.service.reservation;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AvailableSlotsResponseDTO {
    private List<Slot> items;

    @Data
    public static class Slot {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }
}
