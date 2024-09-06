package com.northcoders.recordshopAPI.exception;

public class AlbumAlreadyExistsException extends RuntimeException{
    public AlbumAlreadyExistsException(String message) {
        super(message);
    }
}
