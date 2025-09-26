package com.example.demo.controller;

import com.example.demo.dto.BookDto;
import com.example.demo.dto.BookInstanceDto;
import com.example.demo.model.Book;
import com.example.demo.model.BookInstance;
import com.example.demo.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "書籍管理", description = "圖書與館藏的增刪查改，多數需要 LIBRARIAN 權限。")
@SecurityRequirement(name = "BearerAuth")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('LIBRARIAN')") // 確保只有館員能新增書籍
    @Operation(summary = "新增書籍",
               description = "只有 **LIBRARIAN** 才能新增書籍及其副本。請求體中應包含 `instances` 列表。",
               requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = Book.class))),
               responses = {
                   @ApiResponse(responseCode = "200", description = "新增成功，回傳 BookDto"),
                   @ApiResponse(responseCode = "400", description = "請求資料無效"),
                   @ApiResponse(responseCode = "403", description = "權限不足 (非 LIBRARIAN)")
               })
    public ResponseEntity<BookDto> addNewBook(@RequestBody Book book) {
        return ResponseEntity.ok(bookService.addNewBook(book));
    }

    @GetMapping("/search")
    @Operation(summary = "搜尋書籍",
               description = "根據書名或作者模糊搜尋書籍，並顯示各館館藏數量。所有**已登入**使用者可存取。",
               parameters = {@Parameter(name = "query", description = "搜尋關鍵字 (書名或作者)")},
               responses = {
                   @ApiResponse(responseCode = "200", description = "搜尋成功，回傳 BookDto 列表"),
                   @ApiResponse(responseCode = "401", description = "未提供 JWT Token")
               })
    public ResponseEntity<List<BookDto>> searchBooks(@RequestParam String query) {
        return ResponseEntity.ok(bookService.searchBooks(query));
    }

    @PutMapping("/{bookId}")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    @Operation(summary = "修改書籍資訊",
               description = "只有 **LIBRARIAN** 才能修改書籍的基本資訊。ID 從 Path 取得。",
               parameters = {@Parameter(name = "bookId", description = "要修改的書籍 ID")},
               requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = Book.class))),
               responses = {
                   @ApiResponse(responseCode = "200", description = "修改成功"),
                   @ApiResponse(responseCode = "400", description = "請求資料無效或書籍不存在"),
                   @ApiResponse(responseCode = "403", description = "權限不足 (非 LIBRARIAN)")
               })
    public ResponseEntity<BookDto> updateBook(@PathVariable Long bookId, @RequestBody Book book) {
        return ResponseEntity.ok(bookService.updateBook(bookId, book));
    }

    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    @Operation(summary = "刪除書籍",
               description = "只有 **LIBRARIAN** 才能刪除書籍，會一併刪除所有副本。",
               parameters = {@Parameter(name = "bookId", description = "要刪除的書籍 ID")},
               responses = {
                   @ApiResponse(responseCode = "204", description = "刪除成功，無回傳內容"),
                   @ApiResponse(responseCode = "400", description = "書籍不存在"),
                   @ApiResponse(responseCode = "403", description = "權限不足 (非 LIBRARIAN)")
               })
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookId}/instance")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    @Operation(summary = "新增書籍副本",
               description = "只有 **LIBRARIAN** 才能為特定書籍新增一個副本，並指定其所在館別。",
               parameters = {@Parameter(name = "bookId", description = "要新增副本的書籍 ID")},
               requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = BookInstance.class))),
               responses = {
                   @ApiResponse(responseCode = "200", description = "新增成功，回傳 BookInstanceDto"),
                   @ApiResponse(responseCode = "400", description = "書籍不存在"),
                   @ApiResponse(responseCode = "403", description = "權限不足 (非 LIBRARIAN)")
               })
    public ResponseEntity<BookInstanceDto> addNewBookInstance(@PathVariable Long bookId, @RequestBody BookInstance instance) {
        return ResponseEntity.ok(bookService.addNewBookInstance(bookId, instance.getLocation()));
    }

    @DeleteMapping("/instance/{instanceId}")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    @Operation(summary = "刪除書籍副本",
               description = "只有 **LIBRARIAN** 才能刪除指定的書籍副本。**注意：** 若副本已被借出則無法刪除。",
               parameters = {@Parameter(name = "instanceId", description = "要刪除的書籍副本 ID")},
               responses = {
                   @ApiResponse(responseCode = "204", description = "刪除成功，無回傳內容"),
                   @ApiResponse(responseCode = "400", description = "副本不存在或已被借出"),
                   @ApiResponse(responseCode = "403", description = "權限不足 (非 LIBRARIAN)")
               })
    public ResponseEntity<Void> deleteBookInstance(@PathVariable Long instanceId) {
        bookService.deleteBookInstance(instanceId);
        return ResponseEntity.noContent().build();
    }
}
