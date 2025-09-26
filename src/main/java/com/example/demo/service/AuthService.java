package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.User.Role;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final WebClient librarianApiWebClient; // 用於模擬外部 API 呼叫
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, WebClient.Builder webClientBuilder, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.librarianApiWebClient = webClientBuilder.baseUrl("https://todo.com.tw").build();
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("使用者名稱已存在");
        }

        if (user.getRole() == Role.LIBRARIAN) {
            // 模擬呼叫外部 API 驗證館員身份
            boolean isValidLibrarian = validateLibrarian(user.getUsername());
            if (!isValidLibrarian) {
                throw new RuntimeException("館員身份驗證失敗");
            }
        }
        // 使用 BCryptPasswordEncoder 對密碼進行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public boolean validateLibrarian(String employeeId) {
        // 模擬呼叫外部 API，實際應用中應有更完善的錯誤處理
        // 這段程式碼只是示範
        try {
            // librarianApiWebClient.get()
            //     .uri("/api/validate-librarian?id=" + employeeId)
            //     .header("Authorization", "todo_token")
            //     .retrieve()
            //     .bodyToMono(Boolean.class)
            //     .block(); // 阻塞式呼叫，簡單示範用
            return true;
        } catch (Exception e) {
            System.out.println("=======>" + e.getMessage());
            return false;
        }
    }
}
