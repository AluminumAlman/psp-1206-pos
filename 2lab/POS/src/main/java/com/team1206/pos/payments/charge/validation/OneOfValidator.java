package com.team1206.pos.payments.charge.validation;

import com.team1206.pos.payments.charge.ChargeRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OneOfValidator implements ConstraintValidator<OneOf, ChargeRequestDTO> {

    @Override
    public boolean isValid(ChargeRequestDTO dto, ConstraintValidatorContext context) {
        boolean isPercentSet = dto.getPercent() != null;
        boolean isAmountSet = dto.getAmount() != null;

        return isPercentSet ^ isAmountSet;
    }
}