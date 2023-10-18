/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@GetMapping("/orders")
	public ResponseEntity<Object> getAllBorrowOrders(@RequestParam(required = false) LocalDate expiry,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {

		Pageable paging = PageRequest.of(page, size);
		
		List<Order> orderList;
		try {
		    if (expiry != null) {
		        orderList = orderService.findByExpiryDate(expiry, paging);
		    } else {
		        orderList = orderService.findAllOrders();
		    }
		} catch (Exception e) {
		    return new ResponseEntity<>("Error fetching: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// Create links for pagination
	    List<Link> links = new ArrayList<>();
	    if (orderList.size() > size) {
	        Link nextLink = linkTo(methodOn(OrderController.class).getAllBorrowOrders(expiry, page + 1, size))
	                .withRel("next");
	        links.add(nextLink);
	    }

		// Create a response object with orders and links
	    ResponseEntity<Object> responseEntity = new ResponseEntity<>(orderList, HttpStatus.OK);
	    if (!links.isEmpty()) {
	        responseEntity.getHeaders().add("Links", links.toString());
	    }

		return responseEntity;
	}

	@GetMapping("/orders/{id}")
	public ResponseEntity<Object> getBorrowOrder(@PathVariable("id") Long id) throws OrderNotFoundException {

		try {
			Order order = orderService.findOrder(id);
			return new ResponseEntity<>(order, HttpStatus.OK);

		} catch (OrderNotFoundException e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PutMapping("/orders/{id}")
	public ResponseEntity<Object> updateOrder(@PathVariable("id") Long id, @RequestBody Order order)
			throws OrderNotFoundException, UserNotFoundException {

		order = orderService.updateOrder(order, id);
		Link rordersLink = linkTo(methodOn(OrderController.class).returnBookOrder(id))
				.withRel("Update_Return_or_Cancel");
		order.add(rordersLink);

		return new ResponseEntity<>(order, HttpStatus.OK);
	}

	@DeleteMapping("/orders/{id}")
	public ResponseEntity<Object> returnBookOrder(@PathVariable("id") Long id) throws OrderNotFoundException {

		try {
			orderService.deleteOrder(id);
			return new ResponseEntity<>("Order with id: " + id + " is deleted." , HttpStatus.OK);
		} catch (OrderNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
