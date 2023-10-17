/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.AuthorRepository;
import no.hvl.dat152.rest.ws.service.AuthorService;

/**
 * 
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class AuthorController {

	@Autowired
	private AuthorService authorService;

	@GetMapping("/authors")
	public ResponseEntity<Object> getAllAuthors() {
		List<Author> authors = authorService.findAll();

		if (authors.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<>(authors, HttpStatus.OK);
	}

	@PostMapping("/authors")
	public ResponseEntity<Author> createAuthor(@RequestBody Author author) {

		Author nauthor = authorService.saveAuthor(author);

		return new ResponseEntity<>(nauthor, HttpStatus.CREATED);

	}

	@GetMapping("/authors/{id}")
	public ResponseEntity<Object> getAuthor(@PathVariable("id") long id) throws AuthorNotFoundException {

		Author author;
		try {
			author = authorService.findById(id);

			return new ResponseEntity<>(author, HttpStatus.OK);

		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/authors/{id}")
	public ResponseEntity<Object> updateAuthor(@RequestBody Author author, @PathVariable("id") long id) {

		Author nauthor;

		try {
			nauthor = authorService.updateAuthor(author);
		} catch (AuthorNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(nauthor, HttpStatus.OK);

	}
	
	@GetMapping("/authors/{id}/books")
	public ResponseEntity<Object> getAllBooksAuthor(@PathVariable("id") long id) {
	    try {
	    	Author author = authorService.findById(id);
	    	Set<Book> books = author.getBooks();

	        if (books.isEmpty()) {
	            return new ResponseEntity<>("No books found for the author with ID: " + id, HttpStatus.NO_CONTENT);
	        }

	        return new ResponseEntity<>(books, HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
}
