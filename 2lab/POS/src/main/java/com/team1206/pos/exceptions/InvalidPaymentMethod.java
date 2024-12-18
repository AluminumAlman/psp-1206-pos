package com.team1206.pos.exceptions;

public class InvalidPaymentMethod extends RuntimeException {
    public InvalidPaymentMethod(String message) {
        super(message);
    }
}
