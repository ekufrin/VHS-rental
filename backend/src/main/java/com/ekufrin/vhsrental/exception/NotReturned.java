package com.ekufrin.vhsrental.exception;

public class NotReturned extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "The item has not been returned yet.";

    public NotReturned(String message) {
        super(message);
    }
}
