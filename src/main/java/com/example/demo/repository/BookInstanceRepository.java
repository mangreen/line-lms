package com.example.demo.repository;

import com.example.demo.model.BookInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookInstanceRepository extends JpaRepository<BookInstance, Long> {
    long countByBookIdAndIsAvailable(Long bookId, boolean isAvailable);
}
