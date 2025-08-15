package foodapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import foodapp.dto.ResponseStructure;
import foodapp.entity.Food;
import foodapp.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/food/api")
@Tag(name = "Food Management", description = "APIs for creating, updating, retrieving, and deleting food items")
public class FoodController {

	@Autowired
	private FoodService foodService;

	@PostMapping("/save")
	@Operation(summary = "Create a new food item", description = "Adds a new food item to the database")
	@ApiResponse(responseCode = "201", description = "Food item created successfully")
	public ResponseEntity<ResponseStructure<Food>> createFood(@RequestBody Food food) {
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseStructure<>(HttpStatus.CREATED.value(),
				"Food added Successfully!!", foodService.createFood(food)));
	}

	@GetMapping("/{id}/get")
	@Operation(summary = "Get food by ID", description = "Retrieves a specific food item by its ID")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Food item retrieved successfully"),
			@ApiResponse(responseCode = "404", description = "Food item not found") })
	public ResponseEntity<ResponseStructure<Food>> getFoodById(
			@Parameter(description = "ID of the food item to retrieve") @PathVariable Integer id) {
		return ResponseEntity
				.ok(new ResponseStructure<>(HttpStatus.OK.value(), "Food retrieved", foodService.getFoodById(id)));
	}

	@GetMapping("/getAll")
	@Operation(summary = "Get all food items", description = "Retrieves a paginated list of all food items")
	public ResponseEntity<ResponseStructure<Page<Food>>> getAllFoods(
			@Parameter(description = "Page number, default is 0") @RequestParam(defaultValue = "0") Integer pageNum,
			@Parameter(description = "Page size, default is 5") @RequestParam(defaultValue = "5") Integer pageSize) {
		return ResponseEntity.ok(new ResponseStructure<>(HttpStatus.OK.value(), "Foods fetched Successfully!!",
				foodService.getAllFoods(pageNum, pageSize)));
	}

	@PutMapping("/{id}/update")
	@Operation(summary = "Update food item", description = "Updates details of an existing food item by ID")
	public ResponseEntity<ResponseStructure<Food>> updateFood(
			@Parameter(description = "ID of the food item to update") @PathVariable Integer id,
			@RequestBody Food updatedFood) {
		return ResponseEntity.ok(new ResponseStructure<>(HttpStatus.OK.value(), "Food updated Successfully!!",
				foodService.updateFood(id, updatedFood)));
	}

	@DeleteMapping("/{id}/delete")
	@Operation(summary = "Delete food item", description = "Deletes a specific food item by its ID")
	public ResponseEntity<?> deleteFoodById(
			@Parameter(description = "ID of the food item to delete") @PathVariable Integer id) {
		foodService.deleteFood(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
