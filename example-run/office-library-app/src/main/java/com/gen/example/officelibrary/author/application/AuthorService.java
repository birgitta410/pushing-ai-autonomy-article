package com.gen.example.officelibrary.author.application;

import com.gen.example.officelibrary.author.domain.*;
import com.gen.example.officelibrary.author.persistence.AuthorRepository;
import com.gen.example.officelibrary.shared.exception.BusinessRuleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    public AuthorDTO create(CreateAuthorRequest request) {
        log.info("Creating new author: {} {}", request.getFirstName(), request.getLastName());
        
        // Check if email already exists
        if (request.getEmail() != null && authorRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Author with email " + request.getEmail() + " already exists");
        }
        
        Author author = authorMapper.toEntity(request);
        Author savedAuthor = authorRepository.save(author);
        
        log.info("Successfully created author with id: {}", savedAuthor.getId());
        return authorMapper.toDto(savedAuthor);
    }

    @Transactional(readOnly = true)
    public List<AuthorDTO> findAll() {
        log.debug("Retrieving all authors");
        return authorRepository.findAll()
                .stream()
                .map(authorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AuthorDTO findById(UUID id) {
        log.debug("Retrieving author with id: {}", id);
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));
        return authorMapper.toDto(author);
    }

    @Transactional(readOnly = true)
    public List<AuthorDTO> searchByName(String searchTerm) {
        log.debug("Searching authors by name: {}", searchTerm);
        return authorRepository.searchByFullName(searchTerm)
                .stream()
                .map(authorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuthorDTO> findByNationality(String nationality) {
        log.debug("Finding authors by nationality: {}", nationality);
        return authorRepository.findByNationalityIgnoreCase(nationality)
                .stream()
                .map(authorMapper::toDto)
                .collect(Collectors.toList());
    }

    public AuthorDTO update(UUID id, UpdateAuthorRequest request) {
        log.info("Updating author with id: {}", id);
        
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));
        
        // Check if email is being changed and if new email already exists
        if (request.getEmail() != null && 
            !request.getEmail().equals(author.getEmail()) && 
            authorRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Author with email " + request.getEmail() + " already exists");
        }
        
        authorMapper.updateEntity(author, request);
        Author updatedAuthor = authorRepository.save(author);
        
        log.info("Successfully updated author with id: {}", id);
        return authorMapper.toDto(updatedAuthor);
    }

    public void deleteById(UUID id) {
        log.info("Deleting author with id: {}", id);
        
        if (!authorRepository.existsById(id)) {
            throw new AuthorNotFoundException(id);
        }
        
        // TODO: Check if author has books before deletion
        // This would require a reference to BookRepository or a service method
        // For now, we'll allow deletion and let database constraints handle it
        
        authorRepository.deleteById(id);
        log.info("Successfully deleted author with id: {}", id);
    }
}