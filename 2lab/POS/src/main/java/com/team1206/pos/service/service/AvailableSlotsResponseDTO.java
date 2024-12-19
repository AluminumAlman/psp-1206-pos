package com.team1206.pos.service.service;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class AvailableSlotsResponseDTO {
    private List<Slot> items = new ArrayList<>();

    @Data
    public static class Slot {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }
}
