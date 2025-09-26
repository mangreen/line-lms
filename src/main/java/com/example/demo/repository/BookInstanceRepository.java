package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.BookInstance;

@Repository
public interface BookInstanceRepository extends JpaRepository<BookInstance, Long> {
    long countByBookIdAndIsAvailable(Long bookId, boolean isAvailable);

    @Query("SELECT i FROM BookInstance i WHERE i.book.id = :bookId " +
           "AND (:available IS NULL OR i.isAvailable = :available) " +
           // 對 i.location 和 :location 參數進行 CAST 轉換
           "AND (:location IS NULL OR LOWER(CAST(i.location AS text)) LIKE LOWER(CONCAT('%', CAST(:location AS text), '%')))")
    List<BookInstance> findInstancesByCriteria(
        Long bookId, 
        Boolean available, 
        String location
    );
}
