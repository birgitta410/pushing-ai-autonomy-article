package com.gen.example.officelibrary.library.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gen.example.officelibrary.library.application.BookService;
import com.gen.example.officelibrary.library.domain.*;
import com.gen.example.officelibrary.shared.exception.BusinessRuleException;
import com.gen.example.officelibrary.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({BookController.class, GlobalExceptionHandler.class})
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturnCreatedBook_WhenValidRequest() throws Exception {
        // Given
        CreateBookRequest request = new CreateBookRequest();
        request.setIsbn("978-0123456789");
        request.setTitle("Test Book");
        request.setAuthorId(UUID.randomUUID());
        request.setPublisher("Test Publisher");
        request.setPublicationYear(2023);
        request.setGenre("Fiction");
        request.setLocation("A1-B2");

        BookDTO expectedDto = new BookDTO();
        expectedDto.setId(UUID.randomUUID());
        expectedDto.setIsbn("978-0123456789");
        expectedDto.setTitle("Test Book");
        expectedDto.setAuthorId(request.getAuthorId());
        expectedDto.setPublisher("Test Publisher");
        expectedDto.setPublicationYear(2023);
        expectedDto.setGenre("Fiction");
        expectedDto.setStatus(BookStatus.AVAILABLE);
        expectedDto.setDateAdded(LocalDate.now());
        expectedDto.setLocation("A1-B2");

        when(bookService.create(any(CreateBookRequest.class))).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedDto.getId().toString()))
                .andExpect(jsonPath("$.isbn").value("978-0123456789"))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.publisher").value("Test Publisher"))
                .andExpect(jsonPath("$.publicationYear").value(2023))
                .andExpect(jsonPath("$.genre").value("Fiction"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.location").value("A1-B2"));

        verify(bookService).create(any(CreateBookRequest.class));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenIsbnAlreadyExists() throws Exception {
        // Given
        CreateBookRequest request = new CreateBookRequest();
        request.setIsbn("978-0123456789");
        request.setTitle("Test Book");
        request.setAuthorId(UUID.randomUUID());

        when(bookService.create(any(CreateBookRequest.class)))
                .thenThrow(new BusinessRuleException("Book with ISBN 978-0123456789 already exists"));

        // When & Then
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(bookService).create(any(CreateBookRequest.class));
    }

    @Test
    void findAll_ShouldReturnListOfBooks() throws Exception {
        // Given
        BookDTO book1 = new BookDTO();
        book1.setId(UUID.randomUUID());
        book1.setTitle("Book 1");
        book1.setStatus(BookStatus.AVAILABLE);

        BookDTO book2 = new BookDTO();
        book2.setId(UUID.randomUUID());
        book2.setTitle("Book 2");
        book2.setStatus(BookStatus.BORROWED);

        List<BookDTO> books = Arrays.asList(book1, book2);
        when(bookService.findAll()).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Book 1"))
                .andExpect(jsonPath("$[1].title").value("Book 2"));

        verify(bookService).findAll();
    }

    @Test
    void findAll_ShouldReturnFilteredBooks_WhenFiltersProvided() throws Exception {
        // Given
        BookStatus status = BookStatus.AVAILABLE;
        String genre = "Fiction";
        UUID authorId = UUID.randomUUID();

        BookDTO book1 = new BookDTO();
        book1.setId(UUID.randomUUID());
        book1.setTitle("Filtered Book");
        book1.setStatus(status);
        book1.setGenre(genre);
        book1.setAuthorId(authorId);

        List<BookDTO> books = Arrays.asList(book1);
        when(bookService.findBooksWithFilters(status, genre, authorId)).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books")
                .param("status", status.toString())
                .param("genre", genre)
                .param("authorId", authorId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Filtered Book"))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$[0].genre").value("Fiction"));

        verify(bookService).findBooksWithFilters(status, genre, authorId);
        verify(bookService, never()).findAll();
    }

    @Test
    void findById_ShouldReturnBook_WhenBookExists() throws Exception {
        // Given
        UUID bookId = UUID.randomUUID();
        BookDTO expectedDto = new BookDTO();
        expectedDto.setId(bookId);
        expectedDto.setTitle("Test Book");
        expectedDto.setStatus(BookStatus.AVAILABLE);

        when(bookService.findById(bookId)).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId.toString()))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));

        verify(bookService).findById(bookId);
    }

    @Test
    void findById_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
        // Given
        UUID bookId = UUID.randomUUID();
        when(bookService.findById(bookId)).thenThrow(new BookNotFoundException(bookId));

        // When & Then
        mockMvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isNotFound());

        verify(bookService).findById(bookId);
    }

    @Test
    void update_ShouldReturnUpdatedBook_WhenValidRequest() throws Exception {
        // Given
        UUID bookId = UUID.randomUUID();
        UpdateBookRequest request = new UpdateBookRequest();
        request.setIsbn("978-0987654321");
        request.setTitle("Updated Book");
        request.setAuthorId(UUID.randomUUID());
        request.setPublisher("Updated Publisher");
        request.setPublicationYear(2024);
        request.setGenre("Non-Fiction");
        request.setLocation("B2-C3");

        BookDTO expectedDto = new BookDTO();
        expectedDto.setId(bookId);
        expectedDto.setIsbn("978-0987654321");
        expectedDto.setTitle("Updated Book");
        expectedDto.setAuthorId(request.getAuthorId());
        expectedDto.setPublisher("Updated Publisher");
        expectedDto.setPublicationYear(2024);
        expectedDto.setGenre("Non-Fiction");
        expectedDto.setLocation("B2-C3");

        when(bookService.update(eq(bookId), any(UpdateBookRequest.class))).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(put("/api/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId.toString()))
                .andExpect(jsonPath("$.isbn").value("978-0987654321"))
                .andExpect(jsonPath("$.title").value("Updated Book"))
                .andExpect(jsonPath("$.publisher").value("Updated Publisher"))
                .andExpect(jsonPath("$.publicationYear").value(2024))
                .andExpect(jsonPath("$.genre").value("Non-Fiction"))
                .andExpect(jsonPath("$.location").value("B2-C3"));

        verify(bookService).update(eq(bookId), any(UpdateBookRequest.class));
    }

    @Test
    void update_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
        // Given
        UUID bookId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UpdateBookRequest request = new UpdateBookRequest();
        request.setIsbn("978-0123456789");
        request.setTitle("Updated Book");
        request.setAuthorId(authorId);

        when(bookService.update(eq(bookId), any(UpdateBookRequest.class)))
                .thenThrow(new BookNotFoundException(bookId));

        // When & Then
        mockMvc.perform(put("/api/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(bookService).update(eq(bookId), any(UpdateBookRequest.class));
    }

    @Test
    void deleteById_ShouldReturnNoContent_WhenBookExists() throws Exception {
        // Given
        UUID bookId = UUID.randomUUID();
        doNothing().when(bookService).deleteById(bookId);

        // When & Then
        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isNoContent());

        verify(bookService).deleteById(bookId);
    }

    @Test
    void deleteById_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
        // Given
        UUID bookId = UUID.randomUUID();
        doThrow(new BookNotFoundException(bookId)).when(bookService).deleteById(bookId);

        // When & Then
        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isNotFound());

        verify(bookService).deleteById(bookId);
    }

    @Test
    void deleteById_ShouldReturnBadRequest_WhenBookHasActiveBorrowingRecords() throws Exception {
        // Given
        UUID bookId = UUID.randomUUID();
        doThrow(new BusinessRuleException("Cannot delete book with active borrowing records"))
                .when(bookService).deleteById(bookId);

        // When & Then
        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isBadRequest());

        verify(bookService).deleteById(bookId);
    }

    @Test
    void searchBooks_ShouldReturnMatchingBooks() throws Exception {
        // Given
        String searchQuery = "Java";
        BookDTO book1 = new BookDTO();
        book1.setId(UUID.randomUUID());
        book1.setTitle("Java Programming");

        List<BookDTO> books = Arrays.asList(book1);
        when(bookService.searchBooks(searchQuery)).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books/search?query={query}", searchQuery))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Java Programming"));

        verify(bookService).searchBooks(searchQuery);
    }

    @Test
    void findAvailableBooks_ShouldReturnAvailableBooks() throws Exception {
        // Given
        BookDTO book1 = new BookDTO();
        book1.setId(UUID.randomUUID());
        book1.setTitle("Available Book");
        book1.setStatus(BookStatus.AVAILABLE);

        List<BookDTO> books = Arrays.asList(book1);
        when(bookService.findAvailableBooks()).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Available Book"))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));

        verify(bookService).findAvailableBooks();
    }

    @Test
    void findByIsbn_ShouldReturnBook_WhenBookExists() throws Exception {
        // Given
        String isbn = "978-0123456789";
        BookDTO expectedDto = new BookDTO();
        expectedDto.setId(UUID.randomUUID());
        expectedDto.setIsbn(isbn);
        expectedDto.setTitle("Test Book");

        when(bookService.findByIsbn(isbn)).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(get("/api/books/isbn/{isbn}", isbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(isbn))
                .andExpect(jsonPath("$.title").value("Test Book"));

        verify(bookService).findByIsbn(isbn);
    }

    @Test
    void findByIsbn_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
        // Given
        String isbn = "978-0123456789";
        when(bookService.findByIsbn(isbn)).thenThrow(new BookNotFoundException("Book with ISBN " + isbn + " not found"));

        // When & Then
        mockMvc.perform(get("/api/books/isbn/{isbn}", isbn))
                .andExpect(status().isNotFound());

        verify(bookService).findByIsbn(isbn);
    }

    @Test
    void create_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        CreateBookRequest request = new CreateBookRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).create(any(CreateBookRequest.class));
    }
}