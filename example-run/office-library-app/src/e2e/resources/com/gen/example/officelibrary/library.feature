Feature: Complete Library journey testing all Book and Borrowing endpoints

  Background:
    * url demoBaseUrl

  Scenario: Complete Library CRUD and Borrowing journey
    * def timestamp = new java.util.Date().getTime()
    * def uniqueAuthorEmail = 'test.author.' + timestamp + '@example.com'
    * def uniqueBorrowerEmail1 = 'john.borrower.' + timestamp + '@example.com'
    * def uniqueBorrowerEmail2 = 'another.borrower.' + timestamp + '@example.com'
    * def uniqueBorrowerEmail3 = 'third.borrower.' + timestamp + '@example.com'
    * def uniqueIsbn1 = '9781234' + timestamp.toString().substring(6)
    * def uniqueIsbn2 = '9790987' + timestamp.toString().substring(6)
    * def duplicateTestIsbn = '9781234' + timestamp.toString().substring(6)
    
    # Setup: Create an author first (needed for books)
    Given path '/api/authors'
    And request
    """
    {
        "firstName": "Test",
        "lastName": "Author",
        "biography": "Test author for library tests",
        "birthDate": "1980-01-01",
        "nationality": "Test",
        "email": "#(uniqueAuthorEmail)"
    }
    """
    When method POST
    Then status 201
    * def authorId = response.id

    # 1. Create a new book
    Given path '/api/books'
    And request 
    """
    {
        "isbn": "#(uniqueIsbn1)",
        "title": "Test Book One",
        "authorId": "#(authorId)",
        "publisher": "Test Publisher",
        "publicationYear": 2023,
        "genre": "Fiction",
        "status": "AVAILABLE",
        "dateAdded": "2024-01-01",
        "location": "Shelf A1"
    }
    """
    When method POST
    Then status 201
    And match response.isbn == uniqueIsbn1
    And match response.title == 'Test Book One'
    And match response.authorId == authorId
    And match response.publisher == 'Test Publisher'
    And match response.publicationYear == 2023
    And match response.genre == 'Fiction'
    And match response.status == 'AVAILABLE'
    And match response.location == 'Shelf A1'
    And match response.id == '#uuid'
    * def bookId = response.id

    # 2. Get the created book by ID
    Given path '/api/books', bookId
    When method GET
    Then status 200
    And match response.id == bookId
    And match response.title == 'Test Book One'
    And match response.status == 'AVAILABLE'

    # 3. Create another book for testing
    Given path '/api/books'
    And request 
    """
    {
        "isbn": "#(uniqueIsbn2)",
        "title": "Test Book Two",
        "authorId": "#(authorId)",
        "publisher": "Another Publisher",
        "publicationYear": 2022,
        "genre": "Mystery",
        "status": "AVAILABLE",
        "dateAdded": "2024-01-02",
        "location": "Shelf B2"
    }
    """
    When method POST
    Then status 201
    * def bookId2 = response.id

    # 4. List all books
    Given path '/api/books'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[*].id contains bookId
    And match response[*].id contains bookId2
    And assert response.length >= 2

    # 5. List only available books
    Given path '/api/books/available'
    When method GET
    Then status 200
    And match response == '#array'
    And match each response[*].status == 'AVAILABLE'

    # 6. Search books by title
    Given path '/api/books/search'
    And param query = 'Test Book One'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0].title == 'Test Book One'

    # 7. Search books by ISBN
    Given path '/api/books/search'
    And param query = '9781234567890'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0].isbn == '9781234567890'

    # 8. Update book information
    Given path '/api/books', bookId
    And request 
    """
    {
        "isbn": "#(uniqueIsbn1)",
        "title": "Test Book One - Updated Edition",
        "authorId": "#(authorId)",
        "publisher": "Updated Publisher",
        "publicationYear": 2024,
        "genre": "Science Fiction",
        "status": "AVAILABLE",
        "dateAdded": "2024-01-01",
        "location": "Shelf A1-Updated"
    }
    """
    When method PUT
    Then status 200
    And match response.title == 'Test Book One - Updated Edition'
    And match response.publisher == 'Updated Publisher'
    And match response.publicationYear == 2024
    And match response.genre == 'Science Fiction'
    And match response.location == 'Shelf A1-Updated'

    # 9. Borrow the book
    Given path '/api/books', bookId, 'borrow'
    And request 
    """
    {
        "borrowerName": "John Borrower",
        "borrowerEmail": "#(uniqueBorrowerEmail1)",
        "notes": "Borrowing for research"
    }
    """
    When method POST
    Then status 201
    And match response.borrowerName == 'John Borrower'
    And match response.borrowerEmail == uniqueBorrowerEmail1
    And match response.status == 'ACTIVE'
    And match response.notes == 'Borrowing for research'
    And match response.id == '#uuid'
    * def borrowingRecordId = response.id

    # 10. Verify book status changed to BORROWED
    Given path '/api/books', bookId
    When method GET
    Then status 200
    And match response.status == 'BORROWED'

    # 11. Get the borrowing record
    Given path '/api/borrowing-records', borrowingRecordId
    When method GET
    Then status 200
    And match response.id == borrowingRecordId
    And match response.borrowerName == 'John Borrower'
    And match response.status == 'ACTIVE'

    # 12. List all borrowing records
    Given path '/api/borrowing-records'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[*].id contains borrowingRecordId

    # 13. Get borrowing records by borrower email
    Given path '/api/borrowing-records/by-borrower'
    And param email = uniqueBorrowerEmail1
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0].borrowerEmail == uniqueBorrowerEmail1

    # 14. Get borrowing history for the book
    Given path '/api/books', bookId, 'borrowing-history'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0].id == borrowingRecordId

    # 15. Return the book
    Given path '/api/borrowing-records', borrowingRecordId, 'return'
    When method PUT
    Then status 200
    And match response.status == 'RETURNED'
    And match response.returnDate == '#present'

    # 16. Verify book status changed back to AVAILABLE
    Given path '/api/books', bookId
    When method GET
    Then status 200
    And match response.status == 'AVAILABLE'

    # 17. Verify borrowing record is now RETURNED
    Given path '/api/borrowing-records', borrowingRecordId
    When method GET
    Then status 200
    And match response.status == 'RETURNED'

    # 18. Test books by author
    Given path '/api/authors', authorId, 'books'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[*].id contains bookId
    And match response[*].id contains bookId2

    # 19. Test error cases - Try to borrow already borrowed book
    # First borrow the second book
    Given path '/api/books', bookId2, 'borrow'
    And request 
    """
    {
        "borrowerName": "Another Borrower",
        "borrowerEmail": "#(uniqueBorrowerEmail2)"
    }
    """
    When method POST
    Then status 201
    * def borrowingRecordId2 = response.id

    # Try to borrow the same book again
    Given path '/api/books', bookId2, 'borrow'
    And request 
    """
    {
        "borrowerName": "Third Borrower",
        "borrowerEmail": "#(uniqueBorrowerEmail3)"
    }
    """
    When method POST
    Then status 400

    # 20. Test error cases - Create book with duplicate ISBN
    Given path '/api/books'
    And request 
    """
    {
        "isbn": "#(duplicateTestIsbn)",
        "title": "Duplicate ISBN Book",
        "authorId": "#(authorId)",
        "publisher": "Test Publisher",
        "publicationYear": 2023,
        "genre": "Fiction",
        "status": "AVAILABLE",
        "dateAdded": "2024-01-01",
        "location": "Shelf C3"
    }
    """
    When method POST
    Then status 400

    # 21. Test error cases - Create book with invalid author
    Given path '/api/books'
    And request 
    """
    {
        "isbn": "9781111111111",
        "title": "Invalid Author Book",
        "authorId": "00000000-0000-0000-0000-000000000000",
        "publisher": "Test Publisher",
        "publicationYear": 2023,
        "genre": "Fiction",
        "status": "AVAILABLE",
        "dateAdded": "2024-01-01",
        "location": "Shelf D4"
    }
    """
    When method POST
    Then status 404

    # 22. Test error cases - Try to delete borrowed book
    Given path '/api/books', bookId2
    When method DELETE
    Then status 400

    # 23. Return the second book and then delete it
    Given path '/api/borrowing-records', borrowingRecordId2, 'return'
    When method PUT
    Then status 200

    Given path '/api/books', bookId2
    When method DELETE
    Then status 204

    # 24. Verify book was deleted
    Given path '/api/books', bookId2
    When method GET
    Then status 404

    # 25. Test overdue records (this might be empty if no overdue logic is implemented)
    Given path '/api/borrowing-records/overdue'
    When method GET
    Then status 200
    And match response == '#array'

    # Clean up
    Given path '/api/books', bookId
    When method DELETE
    Then status 204

    Given path '/api/authors', authorId
    When method DELETE
    Then status 204