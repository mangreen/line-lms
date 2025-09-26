package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.BookDto;
import com.example.demo.dto.BookInstanceDto;
import com.example.demo.model.Book;
import com.example.demo.model.BookInstance;
import com.example.demo.repository.BookInstanceRepository;
import com.example.demo.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookInstanceRepository bookInstanceRepository;

    public BookService(BookRepository bookRepository, BookInstanceRepository bookInstanceRepository) {
        this.bookRepository = bookRepository;
        this.bookInstanceRepository = bookInstanceRepository;
    }

    public BookDto addNewBook(Book newBook) {
        if (newBook.getInstances() != null) {
            for (BookInstance instance : newBook.getInstances()) {
                instance.setBook(newBook);
            }
        }

        Book savedBook = bookRepository.save(newBook);

        return convertToDto(savedBook);
    }

    public List<BookDto> searchBooks(String query) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
        
        return books.stream().map(book -> {
            return convertToDto(book);
        }).collect(Collectors.toList());
    }

    // 新增: 修改書籍資訊
    public BookDto updateBook(Long bookId, Book updatedBook) {
        return bookRepository.findById(bookId)
            .map(book -> {
                book.setTitle(updatedBook.getTitle());
                book.setAuthor(updatedBook.getAuthor());
                book.setPublicationYear(updatedBook.getPublicationYear());
                book.setType(updatedBook.getType());
                Book savedBook = bookRepository.save(book);

                return convertToDto(savedBook);
            }).orElseThrow(() -> new RuntimeException("找不到書籍ID：" + bookId));
    }

    private BookDto convertToDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setType(book.getType());
        
        List<BookInstance> instances = book.getInstances();
        // 計算各館的可用數量
        if (instances != null) {
            dto.setAvailableCountByLocation(instances.stream()
                .filter(BookInstance::isAvailable)
                .collect(Collectors.groupingBy(BookInstance::getLocation, Collectors.counting())));
        }
        return dto;
    }

    // 刪除書籍
    public void deleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new RuntimeException("找不到書籍ID：" + bookId);
        }
        bookRepository.deleteById(bookId);
    }

    // 為特定書籍新增副本
    @Transactional // 確保操作在單一事務中完成
    public BookInstanceDto addNewBookInstance(Long bookId, String location) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("找不到書籍ID：" + bookId));
        
        BookInstance newInstance = new BookInstance();
        newInstance.setBook(book);
        newInstance.setLocation(location);
        newInstance.setAvailable(true);
        
        BookInstance savedInstance = bookInstanceRepository.save(newInstance);
        
        // 將實體轉換為 DTO 並回傳        
        return convertToDto(savedInstance);
    }

    private BookInstanceDto convertToDto(BookInstance instance) {
        BookInstanceDto dto = new BookInstanceDto();
        dto.setId(instance.getId());
        dto.setLocation(instance.getLocation());
        dto.setAvailable(instance.isAvailable());
        dto.setBookId(instance.getBook().getId());

        return dto;
    }

    // 刪除書籍副本
    public void deleteBookInstance(Long instanceId) {
        BookInstance instance = bookInstanceRepository.findById(instanceId)
            .orElseThrow(() -> new RuntimeException("找不到書籍副本ID：" + instanceId));
        
        // 額外檢查：只有在副本未被借出時才允許刪除
        if (!instance.isAvailable()) {
            throw new RuntimeException("書籍副本已被借出，無法刪除。");
        }
        bookInstanceRepository.deleteById(instanceId);
    }
}
