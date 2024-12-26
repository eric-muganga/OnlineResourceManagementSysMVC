package com.eric.onlineresourcemanagementsys.services;

import java.util.ArrayList;
import java.util.List;

public class ServiceResponse<T> {
    private T data;
    private boolean success = true;
    private String message = null;
    private List<String> errorMessages = new ArrayList<>();

    public ServiceResponse(T data, boolean success, String message, List<String> errorMessages) {
        this.data = data;
        this.success = success;
        this.message = message;
        this.errorMessages = errorMessages;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
