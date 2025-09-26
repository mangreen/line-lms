package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import com.example.demo.util.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
@Tag(name = "認證管理", description = "提供使用者註冊與登入功能，回傳 JWT Token。")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(summary = "註冊新使用者",
               description = "根據傳入的使用者名稱、密碼和角色進行註冊。館員需要額外驗證。",
               requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = User.class))),
               responses = {
                   @ApiResponse(responseCode = "200", description = "註冊成功，回傳使用者資訊"),
                   @ApiResponse(responseCode = "400", description = "使用者名稱已存在或館員驗證失敗")
               })
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = authService.registerUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "使用者登入",
               description = "使用帳號密碼登入並獲取 JWT token。",
               requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = User.class))),
               responses = {
                   @ApiResponse(responseCode = "200", description = "登入成功，回傳 JWT Token",
                                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"token\": \"eyJ...\"}"))),
                   @ApiResponse(responseCode = "400", description = "使用者名稱或密碼不正確")
               })
    public ResponseEntity<Map<String, String>> createAuthenticationToken(@RequestBody User user) {
        try {
            // 透過 AuthenticationManager 進行驗證，如果密碼不正確會拋出異常
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
        } catch (Exception e) {
            throw new RuntimeException("使用者名稱或密碼不正確", e);
        }
        
        // 驗證成功後，生成 JWT token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        return ResponseEntity.ok(response);
    }
}
