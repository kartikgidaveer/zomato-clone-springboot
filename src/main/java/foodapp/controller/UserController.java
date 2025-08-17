package foodapp.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import foodapp.dto.ResponseStructure;
import foodapp.dto.UserRequest;
import foodapp.dto.UserResponse;
import foodapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users in FoodApp")
public class UserController {

	private final UserService userService;

	@PostMapping
	@Operation(summary = "Create a new user", description = "Registers a new user in the system")
	public ResponseEntity<ResponseStructure<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
		UserResponse savedUser = userService.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ResponseStructure<>(HttpStatus.CREATED.value(), "User created Successfully!!", savedUser));
	}

	@Operation(summary = "Get a user by ID", description = "Retrieves a user’s details using their ID")
	@GetMapping("/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> getUser(@PathVariable Integer userId) {
		UserResponse user = userService.getUser(userId);
		ResponseStructure<UserResponse> apiResponse = new ResponseStructure<>(HttpStatus.OK.value(),
				"User found successfully!!", user);
		return ResponseEntity.ok(apiResponse);
	}

	@Operation(summary = "Get all users", description = "Fetches all users from the system. Cached for better performance.")
	@GetMapping
	public ResponseEntity<ResponseStructure<List<UserResponse>>> getAllUsers() {
		List<UserResponse> users = userService.getAllUsers();
		ResponseStructure<List<UserResponse>> apiResponse = new ResponseStructure<>(HttpStatus.OK.value(),
				"Users fetched successfully!!", users);
		return ResponseEntity.ok(apiResponse);
	}

	@Operation(summary = "Update a user", description = "Updates user details for a given ID")
	@PutMapping("/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> updateUser(@PathVariable Integer userId,
			@Valid @RequestBody UserRequest request) {
		UserResponse updated = userService.updateUser(request, userId);
		ResponseStructure<UserResponse> apiResponse = new ResponseStructure<>(HttpStatus.OK.value(),
				"User updated successfully!!", updated);
		return ResponseEntity.ok(apiResponse);
	}

	@DeleteMapping("/{userId}")
	@Operation(summary = "Delete a user", description = "Deletes a user by their ID and evicts them from cache")
	public ResponseEntity<?> deleteUser(
			@Parameter(description = "ID of the user to delete") @PathVariable Integer userId) {
		userService.deleteUser(userId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PatchMapping("/{userId}/profile-image")
	@Operation(summary = "Upload user profile image", description = "Uploads an image file for the user")
	public ResponseEntity<ResponseStructure<String>> uploadImage(
			@Parameter(description = "Profile image file") @RequestParam MultipartFile file,
			@Parameter(description = "ID of the user to upload image for") @PathVariable Integer userId)
			throws IOException {
		ResponseStructure<String> apiResponse = new ResponseStructure<>();
		apiResponse.setData(userService.uploadImage(file, userId));
		apiResponse.setMessage("Uploaded!!");
		apiResponse.setStatusCode(HttpStatus.OK.value());
		return ResponseEntity.ok(apiResponse);
	}

	@GetMapping("/{userId}/profile-image")
	@Operation(summary = "Get user profile image", description = "Retrieves the profile image of a user in JPEG format")
	public ResponseEntity<byte[]> getImage(
			@Parameter(description = "ID of the user whose image to retrieve") @PathVariable Integer userId) {
		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(userService.getImage(userId));
	}
}
