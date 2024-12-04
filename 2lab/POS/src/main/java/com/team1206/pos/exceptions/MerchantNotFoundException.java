package com.team1206.pos.exceptions;

import java.io.Serial;

//TODO ar nereiktu padaryt BaseNotFoundException, kad visi kiti galetu extendint
public class MerchantNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public MerchantNotFoundException(String message) {
        super(message);
    }
}
