package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoanDto;
import com.example.demo.service.LoanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "借閱管理", description = "處理書籍的借閱與歸還。")
@SecurityRequirement(name = "BearerAuth")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/borrow/{bookInstanceId}")
    @Operation(summary = "借閱書籍",
               description = "使用者借閱指定 ID 的書籍副本。**使用者身份由 JWT 自動判斷。**",
               parameters = {@Parameter(name = "bookInstanceId", description = "要借閱的書籍副本 ID")},
               responses = {
                   @ApiResponse(responseCode = "200", description = "借閱成功，回傳 LoanDto"),
                   @ApiResponse(responseCode = "400", description = "書籍已被借出或超過借閱上限"),
                   @ApiResponse(responseCode = "401", description = "未提供 JWT Token")
               })
    public ResponseEntity<LoanDto> borrowBook(@AuthenticationPrincipal UserDetails currentUser, 
            @PathVariable Long bookInstanceId) {
        
        // 從 AuthenticationPrincipal 中獲取使用者名稱
        String username = currentUser.getUsername();
        
        return ResponseEntity.ok(loanService.borrowBook(username, bookInstanceId));
    }

    @PutMapping("/return/{bookInstanceId}")
    @Operation(summary = "歸還書籍",
               description = "使用者歸還指定 ID 的書籍副本。",
               parameters = {@Parameter(name = "bookInstanceId", description = "要歸還的書籍副本 ID")},
               responses = {
                   @ApiResponse(responseCode = "200", description = "歸還成功，回傳 LoanDto"),
                   @ApiResponse(responseCode = "400", description = "書籍副本未被借出"),
                   @ApiResponse(responseCode = "401", description = "未提供 JWT Token")
               })
    public ResponseEntity<LoanDto> returnBook(@PathVariable Long bookInstanceId) {
        return ResponseEntity.ok(loanService.returnBook(bookInstanceId));
    }
}