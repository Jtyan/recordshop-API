package com.northcoders.recordshopAPI.exception;

public class AlbumNotFoundException extends RuntimeException{
    public AlbumNotFoundException(String message) {
        super(message);
    }
}
