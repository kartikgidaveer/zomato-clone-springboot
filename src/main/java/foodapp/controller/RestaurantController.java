package foodapp.controller;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import foodapp.dto.ResponseStructure;
import foodapp.entity.Food;
import foodapp.entity.Order;
import foodapp.entity.Restaurant;
import foodapp.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurant Management", description = "APIs for managing restaurants, their food items, and orders")
public class RestaurantController {

	private final RestaurantService restaurantService;

	@PostMapping
	@Operation(summary = "Create a new restaurant", description = "Adds a new restaurant to the system")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Restaurant created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Restaurant.class))),
			@ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content) })
	public ResponseEntity<ResponseStructure<Restaurant>> createRestaurant(@Valid @RequestBody Restaurant restaurant) {
		ResponseStructure<Restaurant> apiResponse = new ResponseStructure<>();
		apiResponse.setData(restaurantService.createRestaurant(restaurant));
		apiResponse.setMessage("Restaurant created Successfully!!");
		apiResponse.setStatusCode(HttpStatus.CREATED.value());
		return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a restaurant by ID", description = "Retrieves restaurant details for the given ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Restaurant found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Restaurant.class))),
			@ApiResponse(responseCode = "404", description = "Restaurant not found", content = @Content) })
	public ResponseEntity<ResponseStructure<Restaurant>> getRestaurantById(
			@Parameter(description = "ID of the restaurant to retrieve") @PathVariable @Positive(message = "Restaurant ID must be positive") Integer id) {
		ResponseStructure<Restaurant> apiResponse = new ResponseStructure<>();
		apiResponse.setData(restaurantService.getById(id));
		apiResponse.setMessage("Restaurant Found Successfully!!");
		apiResponse.setStatusCode(HttpStatus.OK.value());
		return ResponseEntity.ok(apiResponse);
	}

	@GetMapping
	@Operation(summary = "Get all restaurants (paginated)", description = "Fetches all restaurants with pagination and sorting options")
	@ApiResponse(responseCode = "200", description = "Restaurants fetched successfully")
	public ResponseEntity<ResponseStructure<Page<Restaurant>>> getAllRestaurants(
			@Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int pageNum,
			@Parameter(description = "Number of records per page") @RequestParam(defaultValue = "5") int pageSize,
			@Parameter(description = "Field name to sort by") @RequestParam(defaultValue = "createdAt") String sortBy) {
		ResponseStructure<Page<Restaurant>> apiResponse = new ResponseStructure<>();
		apiResponse.setData(restaurantService.getAllRestaurants(pageNum, pageSize, sortBy));
		apiResponse.setMessage("Restaurants fetched Successfully!!");
		apiResponse.setStatusCode(HttpStatus.OK.value());
		return ResponseEntity.ok(apiResponse);
	}

	@PutMapping("/{restoId}/foods")
	@Operation(summary = "Assign food items to a restaurant", description = "Links existing food items to a restaurant")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Food items assigned successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid food item list or restaurant ID"),
			@ApiResponse(responseCode = "404", description = "Restaurant or food item not found") })
	public ResponseEntity<ResponseStructure<Restaurant>> assignFoodItems(
			@Parameter(description = "Restaurant ID to assign food items to") @PathVariable @Positive(message = "Restaurant ID must be positive") Integer restoId,
			@RequestBody @NotEmpty(message = "Food ID list cannot be empty") Set<@Positive(message = "Food ID must be positive") Integer> foodId) {
		ResponseStructure<Restaurant> response = new ResponseStructure<>();
		response.setData(restaurantService.assignFoodItems(restoId, foodId));
		response.setMessage("Food items assigned Successfully!!");
		response.setStatusCode(HttpStatus.OK.value());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a restaurant", description = "Deletes a restaurant by its ID")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Restaurant deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Restaurant not found") })
	public ResponseEntity<Void> deleteRestaurantById(
			@Parameter(description = "ID of the restaurant to delete") @PathVariable @Positive(message = "Restaurant ID must be positive") Integer id) {
		restaurantService.deleteRestaurant(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update restaurant details", description = "Updates the information of an existing restaurant")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Restaurant updated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request body"),
			@ApiResponse(responseCode = "404", description = "Restaurant not found") })
	public ResponseEntity<ResponseStructure<Restaurant>> updateRestaurant(
			@Parameter(description = "ID of the restaurant to update") @PathVariable @Positive(message = "Restaurant ID must be positive") Integer id,
			@Valid @RequestBody Restaurant updatedRest) {
		ResponseStructure<Restaurant> apiResponse = new ResponseStructure<>();
		apiResponse.setData(restaurantService.updateRestaurant(id, updatedRest));
		apiResponse.setMessage("Restaurant updated Successfully!!");
		apiResponse.setStatusCode(HttpStatus.OK.value());
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping("/{restaurantId}/foods")
	@Operation(summary = "Get food items by restaurant ID", description = "Retrieves all food items for a given restaurant")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Food fetched successfully"),
			@ApiResponse(responseCode = "404", description = "Restaurant not found") })
	public ResponseEntity<ResponseStructure<List<Food>>> getFoodByRestaurantId(
			@Parameter(description = "Restaurant ID") @PathVariable @Positive(message = "Restaurant ID must be positive") Integer restaurantId) {
		ResponseStructure<List<Food>> response = new ResponseStructure<>();
		response.setData(restaurantService.findFoodsByRestaurantId(restaurantId));
		response.setMessage("Food fetched Successfully!!");
		response.setStatusCode(HttpStatus.OK.value());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{restaurantId}/orders")
	@Operation(summary = "Get orders by restaurant ID", description = "Retrieves all orders for a given restaurant")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Orders fetched successfully"),
			@ApiResponse(responseCode = "404", description = "Restaurant not found") })
	public ResponseEntity<ResponseStructure<List<Order>>> findOrdersByRestaurantId(
			@Parameter(description = "Restaurant ID") @PathVariable @Positive(message = "Restaurant ID must be positive") Integer restaurantId) {
		ResponseStructure<List<Order>> apiResponse = new ResponseStructure<>();
		apiResponse.setData(restaurantService.findOrdersByRestaurantID(restaurantId));
		apiResponse.setMessage("Orders fetched successfully!!");
		apiResponse.setStatusCode(HttpStatus.OK.value());
		return ResponseEntity.ok(apiResponse);
	}
}
