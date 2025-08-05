package com.gen.example.officelibrary.author.application;

import com.gen.example.officelibrary.author.domain.*;
import com.gen.example.officelibrary.author.persistence.AuthorRepository;
import com.gen.example.officelibrary.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorService authorService;

    private UUID authorId;
    private Author author;
    private AuthorDTO authorDTO;
    private CreateAuthorRequest createRequest;
    private UpdateAuthorRequest updateRequest;

    @BeforeEach
    void setUp() {
        authorId = UUID.randomUUID();
        
        author = new Author("John", "Doe", "Famous author",
                           LocalDate.of(1970, 1, 1), "American", "john.doe@example.com");
        author.setId(authorId);
        
        authorDTO = new AuthorDTO(authorId, "John", "Doe", "Famous author",
                                 LocalDate.of(1970, 1, 1), "American", "john.doe@example.com");
        
        createRequest = new CreateAuthorRequest("John", "Doe", "Famous author",
                                               LocalDate.of(1970, 1, 1), "American", "john.doe@example.com");
        
        updateRequest = new UpdateAuthorRequest("Jane", "Smith", "Updated biography",
                                               LocalDate.of(1975, 5, 15), "British", "jane.smith@example.com");
    }

    @Test
    void create_ShouldReturnAuthorDTO_WhenValidRequest() {
        // Given
        when(authorRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(authorMapper.toEntity(createRequest)).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.toDto(author)).thenReturn(authorDTO);

        // When
        AuthorDTO result = authorService.create(createRequest);

        // Then
        assertNotNull(result);
        assertEquals(authorDTO, result);
        verify(authorRepository).existsByEmail(createRequest.getEmail());
        verify(authorMapper).toEntity(createRequest);
        verify(authorRepository).save(author);
        verify(authorMapper).toDto(author);
    }

    @Test
    void create_ShouldThrowBusinessRuleException_WhenEmailAlreadyExists() {
        // Given
        when(authorRepository.existsByEmail(createRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(BusinessRuleException.class, () -> authorService.create(createRequest));
        verify(authorRepository).existsByEmail(createRequest.getEmail());
        verify(authorMapper, never()).toEntity(any());
        verify(authorRepository, never()).save(any());
    }

    @Test
    void create_ShouldNotCheckEmail_WhenEmailIsNull() {
        // Given
        createRequest.setEmail(null);
        when(authorMapper.toEntity(createRequest)).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.toDto(author)).thenReturn(authorDTO);

        // When
        AuthorDTO result = authorService.create(createRequest);

        // Then
        assertNotNull(result);
        verify(authorRepository, never()).existsByEmail(any());
        verify(authorMapper).toEntity(createRequest);
        verify(authorRepository).save(author);
        verify(authorMapper).toDto(author);
    }

    @Test
    void findAll_ShouldReturnListOfAuthorDTOs() {
        // Given
        List<Author> authors = Arrays.asList(author);
        when(authorRepository.findAll()).thenReturn(authors);
        when(authorMapper.toDto(author)).thenReturn(authorDTO);

        // When
        List<AuthorDTO> result = authorService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(authorDTO, result.get(0));
        verify(authorRepository).findAll();
        verify(authorMapper).toDto(author);
    }

    @Test
    void findById_ShouldReturnAuthorDTO_WhenAuthorExists() {
        // Given
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(authorMapper.toDto(author)).thenReturn(authorDTO);

        // When
        AuthorDTO result = authorService.findById(authorId);

        // Then
        assertNotNull(result);
        assertEquals(authorDTO, result);
        verify(authorRepository).findById(authorId);
        verify(authorMapper).toDto(author);
    }

    @Test
    void findById_ShouldThrowAuthorNotFoundException_WhenAuthorDoesNotExist() {
        // Given
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AuthorNotFoundException.class, () -> authorService.findById(authorId));
        verify(authorRepository).findById(authorId);
        verify(authorMapper, never()).toDto(any());
    }

    @Test
    void searchByName_ShouldReturnListOfAuthorDTOs() {
        // Given
        String searchTerm = "John";
        List<Author> authors = Arrays.asList(author);
        when(authorRepository.searchByFullName(searchTerm)).thenReturn(authors);
        when(authorMapper.toDto(author)).thenReturn(authorDTO);

        // When
        List<AuthorDTO> result = authorService.searchByName(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(authorDTO, result.get(0));
        verify(authorRepository).searchByFullName(searchTerm);
        verify(authorMapper).toDto(author);
    }

    @Test
    void findByNationality_ShouldReturnListOfAuthorDTOs() {
        // Given
        String nationality = "American";
        List<Author> authors = Arrays.asList(author);
        when(authorRepository.findByNationalityIgnoreCase(nationality)).thenReturn(authors);
        when(authorMapper.toDto(author)).thenReturn(authorDTO);

        // When
        List<AuthorDTO> result = authorService.findByNationality(nationality);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(authorDTO, result.get(0));
        verify(authorRepository).findByNationalityIgnoreCase(nationality);
        verify(authorMapper).toDto(author);
    }

    @Test
    void update_ShouldReturnUpdatedAuthorDTO_WhenAuthorExists() {
        // Given
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(authorRepository.existsByEmail(updateRequest.getEmail())).thenReturn(false);
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.toDto(author)).thenReturn(authorDTO);

        // When
        AuthorDTO result = authorService.update(authorId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(authorDTO, result);
        verify(authorRepository).findById(authorId);
        verify(authorRepository).existsByEmail(updateRequest.getEmail());
        verify(authorMapper).updateEntity(author, updateRequest);
        verify(authorRepository).save(author);
        verify(authorMapper).toDto(author);
    }

    @Test
    void update_ShouldThrowAuthorNotFoundException_WhenAuthorDoesNotExist() {
        // Given
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AuthorNotFoundException.class, () -> authorService.update(authorId, updateRequest));
        verify(authorRepository).findById(authorId);
        verify(authorRepository, never()).existsByEmail(any());
        verify(authorMapper, never()).updateEntity(any(), any());
        verify(authorRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrowBusinessRuleException_WhenEmailAlreadyExists() {
        // Given
        author.setEmail("old@example.com");
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(authorRepository.existsByEmail(updateRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(BusinessRuleException.class, () -> authorService.update(authorId, updateRequest));
        verify(authorRepository).findById(authorId);
        verify(authorRepository).existsByEmail(updateRequest.getEmail());
        verify(authorMapper, never()).updateEntity(any(), any());
        verify(authorRepository, never()).save(any());
    }

    @Test
    void update_ShouldNotCheckEmail_WhenEmailIsNotChanged() {
        // Given
        updateRequest.setEmail(author.getEmail());
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.toDto(author)).thenReturn(authorDTO);

        // When
        AuthorDTO result = authorService.update(authorId, updateRequest);

        // Then
        assertNotNull(result);
        verify(authorRepository).findById(authorId);
        verify(authorRepository, never()).existsByEmail(any());
        verify(authorMapper).updateEntity(author, updateRequest);
        verify(authorRepository).save(author);
        verify(authorMapper).toDto(author);
    }

    @Test
    void deleteById_ShouldDeleteAuthor_WhenAuthorExists() {
        // Given
        when(authorRepository.existsById(authorId)).thenReturn(true);

        // When
        authorService.deleteById(authorId);

        // Then
        verify(authorRepository).existsById(authorId);
        verify(authorRepository).deleteById(authorId);
    }

    @Test
    void deleteById_ShouldThrowAuthorNotFoundException_WhenAuthorDoesNotExist() {
        // Given
        when(authorRepository.existsById(authorId)).thenReturn(false);

        // When & Then
        assertThrows(AuthorNotFoundException.class, () -> authorService.deleteById(authorId));
        verify(authorRepository).existsById(authorId);
        verify(authorRepository, never()).deleteById(any());
    }
}