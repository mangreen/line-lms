package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private Integer publicationYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookType type;

    // 定義與 BookInstance 的一對多關聯
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookInstance> instances;

    public enum BookType {
        TEXTBOOK, // 書籍
        BOOK      // 圖書
    }
}
