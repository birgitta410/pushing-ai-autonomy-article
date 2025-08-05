package com.gen.example.officelibrary.library.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gen.example.officelibrary.library.application.BorrowingService;
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

@WebMvcTest({BorrowingController.class, GlobalExceptionHandler.class})
class BorrowingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BorrowingService borrowingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void borrowBook_ShouldReturnCreatedBorrowingRecord_WhenValidRequest() throws Exception {
        // Given
        UUID bookId = UUID.randomUUID();
        BorrowBookRequest request = new BorrowBookRequest();
        request.setBorrowerName("John Doe");
        request.setBorrowerEmail("john.doe@example.com");
        request.setNotes("Urgent reading");

        BorrowingRecordDTO expectedDto = new BorrowingRecordDTO();
        expectedDto.setId(UUID.randomUUID());
        expectedDto.setBorrowerName("John Doe");
        expectedDto.setBorrowerEmail("john.doe@example.com");
        expectedDto.setBorrowDate(LocalDate.now());
        expectedDto.setDueDate(LocalDate.now().plusDays(14));
        expectedDto.setStatus(BorrowingStatus.ACTIVE);
        expectedDto.setNotes("Urgent reading");
        expectedDto.setBookId(bookId);

        when(borrowingService.borrowBook(eq(bookId), any(BorrowBookRequest.class))).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(post("/api/books/{bookId}/borrow", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedDto.getId().toString()))
                .andExpect(jsonPath("$.borrowerName").value("John Doe"))
                .andExpect(jsonPath("$.borrowerEmail").value("john.doe@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.notes").value("Urgent reading"))
                .andExpect(jsonPath("$.bookId").value(bookId.toString()));

        verify(borrowingService).borrowBook(eq(bookId), any(BorrowBookRequest.class));
    }

    @Test
    void borrowBook_ShouldReturnBadRequest_WhenBookNotAvailable() throws Exception {
        // Given
        UUID bookId = UUID.randomUUID();
        BorrowBookRequest request = new BorrowBookRequest();
        request.setBorrowerName("John Doe");
        request.setBorrowerEmail("john.doe@example.com");

        when(borrowingService.borrowBook(eq(bookId), any(BorrowBookRequest.class)))
                .thenThrow(new BusinessRuleException("Book is not available for borrowing"));

        // When & Then
        mockMvc.perform(post("/api/books/{bookId}/borrow", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(borrowingService).borrowBook(eq(bookId), any(BorrowBookRequest.class));
    }

    @Test
    void borrowBook_ShouldReturnBadRequest_WhenBorrowingLimitReached() throws Exception {
        // Given
        UUID bookId = UUID.randomUUID();
        BorrowBookRequest request = new BorrowBookRequest();
        request.setBorrowerName("John Doe");
        request.setBorrowerEmail("john.doe@example.com");

        when(borrowingService.borrowBook(eq(bookId), any(BorrowBookRequest.class)))
                .thenThrow(new BusinessRuleException("Maximum borrowing limit (3 books) reached for user: john.doe@example.com"));

        // When & Then
        mockMvc.perform(post("/api/books/{bookId}/borrow", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(borrowingService).borrowBook(eq(bookId), any(BorrowBookRequest.class));
    }

    @Test
    void returnBook_ShouldReturnUpdatedBorrowingRecord_WhenValidRequest() throws Exception {
        // Given
        UUID borrowingRecordId = UUID.randomUUID();
        BorrowingRecordDTO expectedDto = new BorrowingRecordDTO();
        expectedDto.setId(borrowingRecordId);
        expectedDto.setBorrowerName("John Doe");
        expectedDto.setBorrowerEmail("john.doe@example.com");
        expectedDto.setBorrowDate(LocalDate.now().minusDays(7));
        expectedDto.setDueDate(LocalDate.now().plusDays(7));
        expectedDto.setReturnDate(LocalDate.now());
        expectedDto.setStatus(BorrowingStatus.RETURNED);

        when(borrowingService.returnBook(borrowingRecordId)).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(put("/api/borrowing-records/{id}/return", borrowingRecordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(borrowingRecordId.toString()))
                .andExpect(jsonPath("$.status").value("RETURNED"))
                .andExpect(jsonPath("$.returnDate").exists());

        verify(borrowingService).returnBook(borrowingRecordId);
    }

    @Test
    void returnBook_ShouldReturnNotFound_WhenBorrowingRecordDoesNotExist() throws Exception {
        // Given
        UUID borrowingRecordId = UUID.randomUUID();
        when(borrowingService.returnBook(borrowingRecordId))
                .thenThrow(new BorrowingRecordNotFoundException(borrowingRecordId));

        // When & Then
        mockMvc.perform(put("/api/borrowing-records/{id}/return", borrowingRecordId))
                .andExpect(status().isNotFound());

        verify(borrowingService).returnBook(borrowingRecordId);
    }

    @Test
    void returnBook_ShouldReturnBadRequest_WhenBorrowingRecordNotActive() throws Exception {
        // Given
        UUID borrowingRecordId = UUID.randomUUID();
        when(borrowingService.returnBook(borrowingRecordId))
                .thenThrow(new BusinessRuleException("Borrowing record is not active"));

        // When & Then
        mockMvc.perform(put("/api/borrowing-records/{id}/return", borrowingRecordId))
                .andExpect(status().isBadRequest());

        verify(borrowingService).returnBook(borrowingRecordId);
    }

    @Test
    void findAll_ShouldReturnListOfBorrowingRecords() throws Exception {
        // Given
        BorrowingRecordDTO record1 = new BorrowingRecordDTO();
        record1.setId(UUID.randomUUID());
        record1.setBorrowerName("John Doe");
        record1.setStatus(BorrowingStatus.ACTIVE);

        BorrowingRecordDTO record2 = new BorrowingRecordDTO();
        record2.setId(UUID.randomUUID());
        record2.setBorrowerName("Jane Smith");
        record2.setStatus(BorrowingStatus.RETURNED);

        List<BorrowingRecordDTO> records = Arrays.asList(record1, record2);
        when(borrowingService.findAll()).thenReturn(records);

        // When & Then
        mockMvc.perform(get("/api/borrowing-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].borrowerName").value("John Doe"))
                .andExpect(jsonPath("$[1].borrowerName").value("Jane Smith"));

        verify(borrowingService).findAll();
    }

    @Test
    void findAll_ShouldReturnFilteredRecords_WhenFiltersProvided() throws Exception {
        // Given
        BorrowingStatus status = BorrowingStatus.ACTIVE;
        String borrowerEmail = "john.doe@example.com";
        LocalDate fromDate = LocalDate.now().minusDays(30);
        LocalDate toDate = LocalDate.now();

        BorrowingRecordDTO record1 = new BorrowingRecordDTO();
        record1.setId(UUID.randomUUID());
        record1.setBorrowerName("John Doe");
        record1.setBorrowerEmail("john.doe@example.com");
        record1.setStatus(BorrowingStatus.ACTIVE);

        List<BorrowingRecordDTO> records = Arrays.asList(record1);
        when(borrowingService.findRecordsWithFilters(status, borrowerEmail, fromDate, toDate)).thenReturn(records);

        // When & Then
        mockMvc.perform(get("/api/borrowing-records")
                .param("status", status.toString())
                .param("borrowerEmail", borrowerEmail)
                .param("fromDate", fromDate.toString())
                .param("toDate", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].borrowerEmail").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(borrowingService).findRecordsWithFilters(status, borrowerEmail, fromDate, toDate);
        verify(borrowingService, never()).findAll();
    }

    @Test
    void findById_ShouldReturnBorrowingRecord_WhenRecordExists() throws Exception {
        // Given
        UUID recordId = UUID.randomUUID();
        BorrowingRecordDTO expectedDto = new BorrowingRecordDTO();
        expectedDto.setId(recordId);
        expectedDto.setBorrowerName("John Doe");
        expectedDto.setStatus(BorrowingStatus.ACTIVE);

        when(borrowingService.findById(recordId)).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(get("/api/borrowing-records/{id}", recordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recordId.toString()))
                .andExpect(jsonPath("$.borrowerName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(borrowingService).findById(recordId);
    }

    @Test
    void findById_ShouldReturnNotFound_WhenRecordDoesNotExist() throws Exception {
        // Given
        UUID recordId = UUID.randomUUID();
        when(borrowingService.findById(recordId)).thenThrow(new BorrowingRecordNotFoundException(recordId));

        // When & Then
        mockMvc.perform(get("/api/borrowing-records/{id}", recordId))
                .andExpect(status().isNotFound());

        verify(borrowingService).findById(recordId);
    }

    @Test
    void findOverdueRecords_ShouldReturnOverdueRecords() throws Exception {
        // Given
        BorrowingRecordDTO overdueRecord = new BorrowingRecordDTO();
        overdueRecord.setId(UUID.randomUUID());
        overdueRecord.setBorrowerName("John Doe");
        overdueRecord.setStatus(BorrowingStatus.OVERDUE);
        overdueRecord.setDueDate(LocalDate.now().minusDays(5));

        List<BorrowingRecordDTO> records = Arrays.asList(overdueRecord);
        when(borrowingService.findOverdueRecords()).thenReturn(records);

        // When & Then
        mockMvc.perform(get("/api/borrowing-records/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("OVERDUE"));

        verify(borrowingService).findOverdueRecords();
    }

    @Test
    void findByBorrowerEmail_ShouldReturnBorrowerRecords() throws Exception {
        // Given
        String email = "john.doe@example.com";
        BorrowingRecordDTO record1 = new BorrowingRecordDTO();
        record1.setId(UUID.randomUUID());
        record1.setBorrowerName("John Doe");
        record1.setBorrowerEmail(email);
        record1.setStatus(BorrowingStatus.ACTIVE);

        List<BorrowingRecordDTO> records = Arrays.asList(record1);
        when(borrowingService.findByBorrowerEmail(email)).thenReturn(records);

        // When & Then
        mockMvc.perform(get("/api/borrowing-records/by-borrower?email={email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].borrowerEmail").value(email));

        verify(borrowingService).findByBorrowerEmail(email);
    }

    @Test
    void findBorrowingHistoryByBookId_ShouldReturnBookBorrowingHistory() throws Exception {
        // Given
        UUID bookId = UUID.randomUUID();
        BorrowingRecordDTO record1 = new BorrowingRecordDTO();
        record1.setId(UUID.randomUUID());
        record1.setBorrowerName("John Doe");
        record1.setBookId(bookId);
        record1.setStatus(BorrowingStatus.RETURNED);

        BorrowingRecordDTO record2 = new BorrowingRecordDTO();
        record2.setId(UUID.randomUUID());
        record2.setBorrowerName("Jane Smith");
        record2.setBookId(bookId);
        record2.setStatus(BorrowingStatus.ACTIVE);

        List<BorrowingRecordDTO> records = Arrays.asList(record1, record2);
        when(borrowingService.findBorrowingHistoryByBookId(bookId)).thenReturn(records);

        // When & Then
        mockMvc.perform(get("/api/books/{bookId}/borrowing-history", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].bookId").value(bookId.toString()))
                .andExpect(jsonPath("$[1].bookId").value(bookId.toString()));

        verify(borrowingService).findBorrowingHistoryByBookId(bookId);
    }

    @Test
    void borrowBook_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        UUID bookId = UUID.randomUUID();
        BorrowBookRequest request = new BorrowBookRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/books/{bookId}/borrow", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(borrowingService, never()).borrowBook(eq(bookId), any(BorrowBookRequest.class));
    }
}