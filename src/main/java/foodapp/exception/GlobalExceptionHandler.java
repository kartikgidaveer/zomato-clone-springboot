package foodapp.exception;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import foodapp.dto.ResponseStructure;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler
	public ResponseEntity<ResponseStructure<String>> noSuchElementException(NoSuchElementException exception) {
		ResponseStructure<String> response = new ResponseStructure<>();
		response.setData(exception.getMessage());
		response.setMessage("Exception handled");
		response.setStatusCode(HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(PaymentFailedException.class)
	public ResponseEntity<ResponseStructure<String>> paymentFailedException(PaymentFailedException exception) {
		ResponseStructure<String> apiResponse = new ResponseStructure<>();
		apiResponse.setData(exception.getMessage());
		apiResponse.setMessage("Exception handled");
		apiResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
		return new ResponseEntity<>(apiResponse, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(NoFoodsAssignedException.class)
	public ResponseEntity<ResponseStructure<String>> NoFoodsAssignedException(NoFoodsAssignedException exception) {
		ResponseStructure<String> apiResponse = new ResponseStructure<>();
		apiResponse.setData(exception.getMessage());
		apiResponse.setMessage("Exception handled");
		apiResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
	}

}