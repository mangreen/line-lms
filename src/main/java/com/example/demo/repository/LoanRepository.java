package com.example.demo.repository;

import com.example.demo.model.Loan;
import com.example.demo.model.Book.BookType;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    
    Optional<Loan> findByBookInstanceIdAndReturnDateIsNull(Long bookInstanceId);

    // 根據使用者和書籍類型計算未歸還數量
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user = ?1 AND l.returnDate IS NULL AND l.bookInstance.book.type = ?2")
    long countByUserAndBookInstanceBookTypeAndReturnDateIsNull(User user, BookType type);
    
    // 查詢即將到期的借閱
    List<Loan> findByDueDateBetweenAndReturnDateIsNull(LocalDate start, LocalDate end);
}
