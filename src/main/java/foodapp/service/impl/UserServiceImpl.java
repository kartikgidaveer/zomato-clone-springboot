package foodapp.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import foodapp.dto.UserRequest;
import foodapp.dto.UserResponse;
import foodapp.entity.User;
import foodapp.repository.UserRepository;
import foodapp.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserResponse createUser(UserRequest request) {
		User user = toEntity(request);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if (user.getRole() == null || user.getRole().isBlank()) {
			user.setRole("ROLE_USER");
		}
		return toResponse(userRepository.save(user));
	}

	@Override
	@Cacheable(value = "user_cache", key = "#id")
	public UserResponse getUser(Integer id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("User not found with id :" + id));
		return toResponse(user);
	}

	@Override
	@Cacheable(value = "user_cache", key = "'ALL_USERS'")
	public List<UserResponse> getAllUsers() {
		List<User> users = userRepository.findAll();
		if (users.isEmpty()) {
			throw new NoSuchElementException("No users found");
		}

		return users.stream()
				.map(user -> UserResponse.builder().id(user.getId()).username(user.getUsername()).email(user.getEmail())
						.contactNumber(user.getContactNumber()).address(user.getAddress()).role(user.getRole())
						.profileImage(user.getImage() != null ? "/api/users/" + user.getId() + "/profile-image" : null)
						.build())
				.collect(Collectors.toList());
	}

	private User getUserEntity(Integer id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("User not found with id :" + id));
	}

	@Override
	@CachePut(value = "user_cache", key = "#id")
	@CacheEvict(value = "user_cache", key = "'ALL_USERS'")
	public UserResponse updateUser(UserRequest request, Integer id) {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("User not found with id :" + id));

		existingUser.setUsername(request.getUsername());
		existingUser.setAddress(request.getAddress());
		existingUser.setContactNumber(request.getContactNumber());
		existingUser.setEmail(request.getEmail());

		if (request.getPassword() != null && !request.getPassword().isBlank()) {
			existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
		}

		return toResponse(userRepository.save(existingUser));
	}

	@Override
	@CachePut(value = "user_cache", key = "#id")
	@CacheEvict(value = "user_cache", key = "'ALL_USERS'")
	public String uploadImage(MultipartFile file, Integer id) throws IOException {
		byte[] image = file.getBytes();
		User user = getUserEntity(id);
		user.setImage(image);

		userRepository.save(user);
		return "Image uploaded";
	}

	@Override
	public byte[] getImage(Integer id) {
		User user = getUserEntity(id);
		byte[] image = user.getImage();
		if (image == null || image.length == 0) {
			throw new NoSuchElementException("No image uploaded for user");
		}
		return image;
	}

	@Override
	@Caching(evict = { @CacheEvict(value = "user_cache", key = "#id"),
			@CacheEvict(value = "user_cache", key = "'ALL_USERS'") })
	public void deleteUser(Integer id) {
		User user = getUserEntity(id);
		userRepository.delete(user);

	}

	@Scheduled(fixedRate = 120000)
	@CacheEvict(value = "user_cache", allEntries = true)
	public void evictAllCache() {
		System.out.println("Evicting all user cache...");
	}

	public UserResponse toResponse(User user) {
		UserResponse dto = new UserResponse();
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setEmail(user.getEmail());
		dto.setContactNumber(user.getContactNumber());
		dto.setAddress(user.getAddress());
		dto.setRole(user.getRole());

		if (user.getImage() != null) {
			dto.setProfileImage("/api/users/" + user.getId() + "/image");
		}

		return dto;
	}

	public User toEntity(UserRequest dto) {
		User user = new User();
		user.setUsername(dto.getUsername());
		user.setEmail(dto.getEmail());
		user.setContactNumber(dto.getContactNumber());
		user.setAddress(dto.getAddress());
		user.setRole(dto.getRole());
		user.setPassword(dto.getPassword()); // hash before saving
		return user;
	}

}