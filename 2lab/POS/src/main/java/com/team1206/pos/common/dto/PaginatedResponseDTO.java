package com.team1206.pos.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedResponseDTO<T> {

    public PaginatedResponseDTO (int total, int offset, int limit, List<T> items) {
        this.total = total;
        this.offset = offset;
        this.limit = limit;
        this.items = items;
    }

    private int total;  // Total number of matching records

    private int offset;  // Offset of the current page

    private int limit;  // Limit (page size)

    private List<T> items;  // The list of results
}
