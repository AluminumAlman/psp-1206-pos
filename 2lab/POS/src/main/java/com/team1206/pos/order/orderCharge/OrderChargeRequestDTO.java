package com.team1206.pos.order.orderCharge;

import com.team1206.pos.common.validation.OneOf;
import lombok.Data;

import java.math.BigDecimal;

@Data
@OneOf(fields = {"percent", "amount"})
public class OrderChargeRequestDTO {
    private String type;
    private String name;
    private Integer percent;
    private BigDecimal amount;
}
