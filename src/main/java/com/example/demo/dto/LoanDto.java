package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class LoanDto {
    private Long id;
    private Long userId;
    private String username;
    private Long bookInstanceId;
    private String bookTitle;
    private String bookAuthor;
    private String bookLocation;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
}
