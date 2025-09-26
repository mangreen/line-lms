package com.example.demo.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 處理業務邏輯錯誤 (例如：書籍已被借出, 借閱超過上限, 找不到資源)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleCustomRuntimeException(RuntimeException ex) {
        // 檢查錯誤訊息，如果屬於業務邏輯錯誤，通常回傳 400 Bad Request 或 404 Not Found
        
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("timestamp", System.currentTimeMillis());
        
        // 我們假設所有 Service 拋出的 RuntimeException 都是業務錯誤
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    // 捕獲所有未被處理的異常，作為系統錯誤 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "系統發生未知錯誤，請聯繫管理員。"); // 生產環境不應暴露原始錯誤
        body.put("detail", ex.getMessage()); 
        body.put("timestamp", System.currentTimeMillis());
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
