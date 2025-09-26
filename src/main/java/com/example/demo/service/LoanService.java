package com.example.demo.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.LoanDto;
import com.example.demo.model.Book.BookType;
import com.example.demo.model.BookInstance;
import com.example.demo.model.Loan;
import com.example.demo.model.User;
import com.example.demo.repository.BookInstanceRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.UserRepository;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookInstanceRepository bookInstanceRepository;
    private final UserRepository userRepository;

    public LoanService(LoanRepository loanRepository, BookInstanceRepository bookInstanceRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.bookInstanceRepository = bookInstanceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public LoanDto borrowBook(String username, Long bookInstanceId) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("找不到使用者名稱：" + username));
        
        BookInstance bookInstance = bookInstanceRepository.findById(bookInstanceId)
            .orElseThrow(() -> new RuntimeException("找不到書籍副本ID：" + bookInstanceId));

        if (!bookInstance.isAvailable()) {
            throw new RuntimeException("該書籍已被借出或不可借閱。");
        }

        BookType bookType = bookInstance.getBook().getType();
        long currentLoanCount = loanRepository.countByUserAndBookInstanceBookTypeAndReturnDateIsNull(user, bookType);
        
        if (bookType == BookType.BOOK && currentLoanCount >= 5) {
            throw new RuntimeException("您已借閱 5 本圖書，無法再借。");
        }
        if (bookType == BookType.TEXTBOOK && currentLoanCount >= 10) {
            throw new RuntimeException("您已借閱 10 本書籍，無法再借。");
        }
        
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusMonths(1);

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBookInstance(bookInstance);
        loan.setLoanDate(loanDate);
        loan.setDueDate(dueDate);
        
        bookInstance.setAvailable(false);
        bookInstanceRepository.save(bookInstance);

        Loan savedLoan = loanRepository.save(loan);

        // 回傳 DTO
        return convertToDto(savedLoan);
    }
    
    @Transactional
    public LoanDto returnBook(Long bookInstanceId) {
        Loan loan = loanRepository.findByBookInstanceIdAndReturnDateIsNull(bookInstanceId)
            .orElseThrow(() -> new RuntimeException("該書籍副本未被借出。"));
        
        loan.setReturnDate(LocalDate.now());

        BookInstance bookInstance = loan.getBookInstance();
        bookInstance.setAvailable(true);
        bookInstanceRepository.save(bookInstance);
        
        Loan savedLoan = loanRepository.save(loan);

        // 回傳 DTO
        return convertToDto(savedLoan);
    }

    // 新增：將 Loan 實體轉換為 LoanDto 的私有方法
    private LoanDto convertToDto(Loan loan) {
        LoanDto dto = new LoanDto();
        dto.setId(loan.getId());
        dto.setUserId(loan.getUser().getId());
        dto.setUsername(loan.getUser().getUsername());
        dto.setBookInstanceId(loan.getBookInstance().getId());
        dto.setBookTitle(loan.getBookInstance().getBook().getTitle());
        dto.setBookAuthor(loan.getBookInstance().getBook().getAuthor());
        dto.setBookLocation(loan.getBookInstance().getLocation());
        dto.setLoanDate(loan.getLoanDate());
        dto.setDueDate(loan.getDueDate());
        dto.setReturnDate(loan.getReturnDate());
        return dto;
    }
}
