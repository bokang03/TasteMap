package com.example.TasteMap.exception;

public enum ErrorMessage {
    INVALID_TASTE_MAP_DUPLICATE("[ERROR] 이미 저장된 음식점입니다."),
    NO_SEARCH_RESULT("검색된 음식점이 없습니다.");

    private String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

