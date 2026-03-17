package com.example.jobtracker.util;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String resource, String field, Object value) {
        super(String.format("%s already exists with %s = %s", resource, field, value));
    }
}
