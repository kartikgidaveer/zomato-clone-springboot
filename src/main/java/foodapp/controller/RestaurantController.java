package foodapp.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("restaurant/api")
@Tag(name = "Restaurant Management", description = "APIs for managing restaurants, their food items, and orders")
public class RestaurantController {

	@Autowired
	private RestaurantService restaurantService;

	@PostMapping("/save")
	@Operation(summary = "Create a new restaurant", description = "Adds a new restaurant to the system")
	@ApiResponse(responseCode = "201", description = "Restaurant created successfully")
	public ResponseEntity<ResponseStructure<Restaurant>> createRestaurant(@RequestBody Restaurant restaurant) {
		ResponseStructure<Restaurant> apiResponse = new ResponseStructure<>();
		apiResponse.setData(restaurantService.createRestaurant(restaurant));
		apiResponse.setMessage("Restaurant created Successfully!!");
		apiResponse.setStatusCode(HttpStatus.CREATED.value());
		return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
	}

	@GetMapping("/get/{id}")
	@Operation(summary = "Get a restaurant by ID", description = "Retrieves restaurant details for the given ID")
	public ResponseEntity<ResponseStructure<Restaurant>> getRestaurantById(
			@Parameter(description = "ID of the restaurant to retrieve") @PathVariable Integer id) {
		ResponseStructure<Restaurant> apiResponse = new ResponseStructure<>();
		apiResponse.setData(restaurantService.getById(id));
		apiResponse.setMessage("Restaurant Found Successfully!!");
		apiResponse.setStatusCode(HttpStatus.FOUND.value());
		return new ResponseEntity<>(apiResponse, HttpStatus.FOUND);
	}

	@GetMapping("/getall")
	@Operation(summary = "Get all restaurants (paginated)", description = "Fetches all restaurants with pagination and sorting options")
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

	@PutMapping("/{restoId}/assign")
	@Operation(summary = "Assign food items to a restaurant", description = "Links existing food items to a restaurant")
	public ResponseEntity<ResponseStructure<Restaurant>> assignFoodItems(
			@Parameter(description = "Restaurant ID to assign food items to") @PathVariable Integer restoId,
			@RequestBody Set<Integer> foodId) {
		ResponseStructure<Restaurant> response = new ResponseStructure<>();
		response.setData(restaurantService.assignFoodItems(restoId, foodId));
		response.setMessage("Food items assigned Successfully!!");
		response.setStatusCode(HttpStatus.OK.value());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}/delete")
	@Operation(summary = "Delete a restaurant", description = "Deletes a restaurant by its ID")
	public ResponseEntity<?> deleteRestaurantById(
			@Parameter(description = "ID of the restaurant to delete") @PathVariable Integer id) {
		restaurantService.deleteRestaurant(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PutMapping("update/{id}")
	@Operation(summary = "Update restaurant details", description = "Updates the information of an existing restaurant")
	public ResponseEntity<ResponseStructure<Restaurant>> updateRestaurant(
			@Parameter(description = "ID of the restaurant to update") @PathVariable Integer id,
			@RequestBody Restaurant updatedRest) {
		ResponseStructure<Restaurant> apiResponse = new ResponseStructure<>();
		apiResponse.setData(restaurantService.updateRestaurant(id, updatedRest));
		apiResponse.setMessage("Restaurant updated Successfully!!");
		apiResponse.setStatusCode(HttpStatus.OK.value());
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping("/{restaurantId}/foods")
	@Operation(summary = "Get food items by restaurant ID", description = "Retrieves all food items for a given restaurant")
	public ResponseEntity<ResponseStructure<List<Food>>> getFoodByRestaurantId(
			@Parameter(description = "Restaurant ID") @PathVariable Integer restaurantId) {
		ResponseStructure<List<Food>> response = new ResponseStructure<>();
		response.setData(restaurantService.findFoodsByRestaurantId(restaurantId));
		response.setMessage("Food fetched Successfully!!");
		response.setStatusCode(HttpStatus.OK.value());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{restaurantId}/orders")
	@Operation(summary = "Get orders by restaurant ID", description = "Retrieves all orders for a given restaurant")
	public ResponseEntity<ResponseStructure<List<Order>>> findOrdersByRestaurantId(
			@Parameter(description = "Restaurant ID") @PathVariable Integer restaurantId) {
		ResponseStructure<List<Order>> apiResponse = new ResponseStructure<>();
		apiResponse.setData(restaurantService.findOrdersByRestaurantID(restaurantId));
		apiResponse.setMessage("Orders fetched successfully!!");
		apiResponse.setStatusCode(HttpStatus.OK.value());
		return ResponseEntity.ok(apiResponse);
	}
}
