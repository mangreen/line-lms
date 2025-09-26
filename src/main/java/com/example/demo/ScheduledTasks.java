package com.example.demo;

import com.example.demo.model.Loan;
import com.example.demo.repository.LoanRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
public class ScheduledTasks {

    private final LoanRepository loanRepository;

    public ScheduledTasks(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Scheduled(cron = "* * 2 * * *")
    public void sendDueDateNotifications() {
        System.out.println("--- 開始發送即將到期通知 ---");

        LocalDate today = LocalDate.now();
        LocalDate fiveDaysFromNow = today.plusDays(5);
        
        List<Loan> upcomingLoans = loanRepository.findByDueDateBetweenAndReturnDateIsNull(today, fiveDaysFromNow);

        if (!upcomingLoans.isEmpty()) {
            
            for (Loan loan : upcomingLoans) {
                System.out.printf("通知: 會員 %s 借閱的書籍《%s》將在 5 天內到期 (到期日: %s)%n",
                    loan.getUser().getUsername(),
                    loan.getBookInstance().getBook().getTitle(),
                    loan.getDueDate());
            }
            
        }

        System.out.println("--- 通知發送完畢 ---");
    }
}
