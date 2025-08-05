package com.gen.example.officelibrary.author.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gen.example.officelibrary.author.application.AuthorService;
import com.gen.example.officelibrary.author.domain.AuthorDTO;
import com.gen.example.officelibrary.author.domain.AuthorNotFoundException;
import com.gen.example.officelibrary.author.domain.CreateAuthorRequest;
import com.gen.example.officelibrary.author.domain.UpdateAuthorRequest;
import com.gen.example.officelibrary.library.application.BookService;
import com.gen.example.officelibrary.library.domain.BookDTO;
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

@WebMvcTest({AuthorController.class, GlobalExceptionHandler.class})
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturnCreatedAuthor_WhenValidRequest() throws Exception {
        // Given
        CreateAuthorRequest request = new CreateAuthorRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setBiography("A great author");
        request.setBirthDate(LocalDate.of(1970, 1, 1));
        request.setNationality("American");
        request.setEmail("john.doe@example.com");

        AuthorDTO expectedDto = new AuthorDTO();
        expectedDto.setId(UUID.randomUUID());
        expectedDto.setFirstName("John");
        expectedDto.setLastName("Doe");
        expectedDto.setBiography("A great author");
        expectedDto.setBirthDate(LocalDate.of(1970, 1, 1));
        expectedDto.setNationality("American");
        expectedDto.setEmail("john.doe@example.com");

        when(authorService.create(any(CreateAuthorRequest.class))).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedDto.getId().toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.biography").value("A great author"))
                .andExpect(jsonPath("$.nationality").value("American"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(authorService).create(any(CreateAuthorRequest.class));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenEmailAlreadyExists() throws Exception {
        // Given
        CreateAuthorRequest request = new CreateAuthorRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("existing@example.com");

        when(authorService.create(any(CreateAuthorRequest.class)))
                .thenThrow(new BusinessRuleException("Author with email existing@example.com already exists"));

        // When & Then
        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authorService).create(any(CreateAuthorRequest.class));
    }

    @Test
    void findAll_ShouldReturnListOfAuthors() throws Exception {
        // Given
        AuthorDTO author1 = new AuthorDTO();
        author1.setId(UUID.randomUUID());
        author1.setFirstName("John");
        author1.setLastName("Doe");

        AuthorDTO author2 = new AuthorDTO();
        author2.setId(UUID.randomUUID());
        author2.setFirstName("Jane");
        author2.setLastName("Smith");

        List<AuthorDTO> authors = Arrays.asList(author1, author2);
        when(authorService.findAll()).thenReturn(authors);

        // When & Then
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));

        verify(authorService).findAll();
    }

    @Test
    void findAll_ShouldReturnFilteredAuthors_WhenNationalityProvided() throws Exception {
        // Given
        AuthorDTO author1 = new AuthorDTO();
        author1.setId(UUID.randomUUID());
        author1.setFirstName("John");
        author1.setLastName("Doe");
        author1.setNationality("American");

        List<AuthorDTO> authors = Arrays.asList(author1);
        when(authorService.findByNationality("American")).thenReturn(authors);

        // When & Then
        mockMvc.perform(get("/api/authors?nationality=American"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nationality").value("American"));

        verify(authorService).findByNationality("American");
        verify(authorService, never()).findAll();
    }

    @Test
    void findById_ShouldReturnAuthor_WhenAuthorExists() throws Exception {
        // Given
        UUID authorId = UUID.randomUUID();
        AuthorDTO expectedDto = new AuthorDTO();
        expectedDto.setId(authorId);
        expectedDto.setFirstName("John");
        expectedDto.setLastName("Doe");

        when(authorService.findById(authorId)).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(get("/api/authors/{id}", authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(authorId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(authorService).findById(authorId);
    }

    @Test
    void findById_ShouldReturnNotFound_WhenAuthorDoesNotExist() throws Exception {
        // Given
        UUID authorId = UUID.randomUUID();
        when(authorService.findById(authorId)).thenThrow(new AuthorNotFoundException(authorId));

        // When & Then
        mockMvc.perform(get("/api/authors/{id}", authorId))
                .andExpect(status().isNotFound());

        verify(authorService).findById(authorId);
    }

    @Test
    void update_ShouldReturnUpdatedAuthor_WhenValidRequest() throws Exception {
        // Given
        UUID authorId = UUID.randomUUID();
        UpdateAuthorRequest request = new UpdateAuthorRequest();
        request.setFirstName("Updated John");
        request.setLastName("Updated Doe");
        request.setBiography("Updated biography");

        AuthorDTO expectedDto = new AuthorDTO();
        expectedDto.setId(authorId);
        expectedDto.setFirstName("Updated John");
        expectedDto.setLastName("Updated Doe");
        expectedDto.setBiography("Updated biography");

        when(authorService.update(eq(authorId), any(UpdateAuthorRequest.class))).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(put("/api/authors/{id}", authorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(authorId.toString()))
                .andExpect(jsonPath("$.firstName").value("Updated John"))
                .andExpect(jsonPath("$.lastName").value("Updated Doe"))
                .andExpect(jsonPath("$.biography").value("Updated biography"));

        verify(authorService).update(eq(authorId), any(UpdateAuthorRequest.class));
    }

    @Test
    void update_ShouldReturnNotFound_WhenAuthorDoesNotExist() throws Exception {
        // Given
        UUID authorId = UUID.randomUUID();
        UpdateAuthorRequest request = new UpdateAuthorRequest();
        request.setFirstName("Updated John");
        request.setLastName("Updated Doe");

        when(authorService.update(eq(authorId), any(UpdateAuthorRequest.class)))
                .thenThrow(new AuthorNotFoundException(authorId));

        // When & Then
        mockMvc.perform(put("/api/authors/{id}", authorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(authorService).update(eq(authorId), any(UpdateAuthorRequest.class));
    }

    @Test
    void deleteById_ShouldReturnNoContent_WhenAuthorExists() throws Exception {
        // Given
        UUID authorId = UUID.randomUUID();
        doNothing().when(authorService).deleteById(authorId);

        // When & Then
        mockMvc.perform(delete("/api/authors/{id}", authorId))
                .andExpect(status().isNoContent());

        verify(authorService).deleteById(authorId);
    }

    @Test
    void deleteById_ShouldReturnNotFound_WhenAuthorDoesNotExist() throws Exception {
        // Given
        UUID authorId = UUID.randomUUID();
        doThrow(new AuthorNotFoundException(authorId)).when(authorService).deleteById(authorId);

        // When & Then
        mockMvc.perform(delete("/api/authors/{id}", authorId))
                .andExpect(status().isNotFound());

        verify(authorService).deleteById(authorId);
    }

    @Test
    void searchByName_ShouldReturnMatchingAuthors() throws Exception {
        // Given
        String searchQuery = "John";
        AuthorDTO author1 = new AuthorDTO();
        author1.setId(UUID.randomUUID());
        author1.setFirstName("John");
        author1.setLastName("Doe");

        List<AuthorDTO> authors = Arrays.asList(author1);
        when(authorService.searchByName(searchQuery)).thenReturn(authors);

        // When & Then
        mockMvc.perform(get("/api/authors/search?query={query}", searchQuery))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));

        verify(authorService).searchByName(searchQuery);
    }

    @Test
    void findBooksByAuthor_ShouldReturnAuthorBooks() throws Exception {
        // Given
        UUID authorId = UUID.randomUUID();
        BookDTO book1 = new BookDTO();
        book1.setId(UUID.randomUUID());
        book1.setTitle("Book 1");
        book1.setAuthorId(authorId);

        BookDTO book2 = new BookDTO();
        book2.setId(UUID.randomUUID());
        book2.setTitle("Book 2");
        book2.setAuthorId(authorId);

        List<BookDTO> books = Arrays.asList(book1, book2);
        when(bookService.findByAuthorId(authorId)).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/authors/{id}/books", authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Book 1"))
                .andExpect(jsonPath("$[1].title").value("Book 2"));

        verify(bookService).findByAuthorId(authorId);
    }

    @Test
    void create_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        CreateAuthorRequest request = new CreateAuthorRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authorService, never()).create(any(CreateAuthorRequest.class));
    }
}