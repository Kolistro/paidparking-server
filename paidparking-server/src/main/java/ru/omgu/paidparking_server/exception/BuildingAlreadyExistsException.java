package ru.omgu.paidparking_server.exception;

public class BuildingAlreadyExistsException extends RuntimeException{
    public BuildingAlreadyExistsException(String message) {
        super(message);
    }
}
