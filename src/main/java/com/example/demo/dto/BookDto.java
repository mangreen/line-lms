package com.example.demo.dto;

import com.example.demo.model.Book;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private Integer publicationYear;
    private Book.BookType type;
    private Map<String, Long> availableCountByLocation;
}
