package foodapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import foodapp.dto.BillResponse;
import foodapp.dto.OrderRequest;
import foodapp.dto.PaymentDto;
import foodapp.dto.ResponseStructure;
import foodapp.entity.Order;
import foodapp.entity.OrderStatus;
import foodapp.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing customer orders, payments, and order statuses")
public class OrderController {

	private final OrderService orderService;

	@PostMapping("/bill")
	@Operation(summary = "Generate bill", description = "Generates a bill based on the order request before placing an order")
	@ApiResponse(responseCode = "201", description = "Bill generated successfully")
	public ResponseEntity<ResponseStructure<BillResponse>> generateBill(@Valid @RequestBody OrderRequest orderRequest) {
		ResponseStructure<BillResponse> response = new ResponseStructure<>();
		response.setData(orderService.generateBill(orderRequest));
		response.setMessage("Bill generated");
		response.setStatusCode(HttpStatus.CREATED.value());
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/pay")
	@Operation(summary = "Pay and place order", description = "Processes payment and places the order")
	public ResponseEntity<ResponseStructure<String>> payAndPlaceOrder(@Valid @RequestBody PaymentDto payment) {
		String data = orderService.payAndPlaceOrder(payment);
		ResponseStructure<String> apiResponse = new ResponseStructure<>();
		apiResponse.setData(data);
		apiResponse.setMessage("Order placed");
		apiResponse.setStatusCode(HttpStatus.CREATED.value());
		return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
	}

	@DeleteMapping("/{orderId}")
	@Operation(summary = "Delete an order", description = "Deletes an order by its ID")
	public ResponseEntity<?> deleteOrderById(
			@Parameter(description = "ID of the order to delete") @PathVariable Integer orderId) {
		orderService.deleteOrderById(orderId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an order", description = "Updates order details for the given ID")
	public ResponseEntity<Order> updateOrder(
			@Parameter(description = "ID of the order to update") @PathVariable Integer id,
			@Valid @RequestBody Order updatedOrder) {
		Order savedOrder = orderService.updateOrder(id, updatedOrder);
		return ResponseEntity.ok(savedOrder);
	}

	@PatchMapping("/{id}/status")
	@Operation(summary = "Update order status", description = "Updates the status of an existing order (Admin action)")
	public ResponseEntity<ResponseStructure<Order>> updateOrderStatus(
			@Parameter(description = "ID of the order") @PathVariable Integer id,
			@Parameter(description = "New status for the order") @RequestParam OrderStatus status) {
		Order updatedOrder = orderService.updateOrderStatusByAdmin(id, status);
		return ResponseEntity
				.ok(new ResponseStructure<>(HttpStatus.OK.value(), "Order status updated successfully", updatedOrder));
	}

	@PatchMapping("/{id}/cancel")
	@Operation(summary = "Cancel an order", description = "Cancels an existing order by its ID")
	public ResponseEntity<ResponseStructure<String>> cancelOrder(
			@Parameter(description = "ID of the order to cancel") @PathVariable Integer id) {
		String cancelOrder = orderService.cancelOrder(id);
		return ResponseEntity
				.ok(new ResponseStructure<>(HttpStatus.OK.value(), "Order status updated successfully", cancelOrder));
	}

	@GetMapping
	@Operation(summary = "Get all orders", description = "Fetches a list of all orders in the system")
	public ResponseEntity<ResponseStructure<List<Order>>> getAllOrders() {
		List<Order> allOrders = orderService.getAllOrders();
		return ResponseEntity
				.ok(new ResponseStructure<>(HttpStatus.OK.value(), "Orders fetched successfully", allOrders));
	}
}
