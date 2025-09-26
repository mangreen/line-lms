package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookInstanceDto {
    private Long id;
    private String location;
    private boolean isAvailable;
    private Long bookId; // 僅回傳書籍 ID
}
