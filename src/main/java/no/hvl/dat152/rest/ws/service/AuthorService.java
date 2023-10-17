/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.AuthorRepository;

/**
 * @author tdoy
 */
@Service
public class AuthorService {

	@Autowired
	private AuthorRepository authorRepository;

	public Author saveAuthor(Author author) {

		return authorRepository.save(author);

	}

	public List<Author> findAll() {

		return (List<Author>) authorRepository.findAll();

	}

	public Author findById(long id) throws AuthorNotFoundException {

		Author author = authorRepository.findById(id)
				.orElseThrow(() -> new AuthorNotFoundException("Author with the id: " + id + "not found!"));

		return author;
	}

	public Author updateAuthor(Author author) throws AuthorNotFoundException{

		long id = author.getAuthorId();

		try {
			findById(id);
			authorRepository.save(author);
		} catch (AuthorNotFoundException e) {
			e.printStackTrace();
		}
		
		return author;

	}

	public Author deleteByID(long id) {

		Author author = null;

		try {
			author = findById(id);
		} catch (AuthorNotFoundException e) {
			e.printStackTrace();
		}

		authorRepository.delete(author);

		return author;

	}

}
