package foodapp.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import foodapp.entity.User;
import foodapp.repository.UserRepository;
import foodapp.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public User createUser(User user) {
		return userRepository.save(user);
	}

	@Override
	@Cacheable(value = "user_cache", key = "#id")
	public User getUser(Integer id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("User not found with id :" + id));
	}

	@Override
	@Cacheable(value = "user_cache", key = "'ALL_USERS'")
	public List<User> getAllUsers() {
		List<User> users = userRepository.findAll();
		if (users.isEmpty())
			throw new NoSuchElementException("No users found");
		return users;
	}

	@Override
	@CachePut(value = "user_cache", key = "#id")
	@CacheEvict(value = "user_cache", key = "'ALL_USERS'")
	public User updateUser(User user, Integer id) {
		User existingUser = getUser(id);
		existingUser.setUsername(user.getUsername());
		existingUser.setAddress(user.getAddress());
		existingUser.setContactNumber(user.getContactNumber());
		existingUser.setEmail(user.getEmail());
		existingUser.setPassword(user.getPassword());
		return userRepository.save(existingUser);
	}

	@Override
	@CachePut(value = "user_cache", key = "#id")
	@CacheEvict(value = "user_cache", key = "'ALL_USERS'")
	public String uploadImage(MultipartFile file, Integer id) throws IOException {
		byte[] image = file.getBytes();
		User user = getUser(id);
		user.setImage(image);

		userRepository.save(user);
		return "Image uploaded";
	}

	@Override
	public byte[] getImage(Integer id) {
		User user = getUser(id);
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
		User user = getUser(id);
		userRepository.delete(user);

	}

	@Scheduled(fixedRate = 120000)
	@CacheEvict(value = "user_cache", allEntries = true)
	public void evictAllCache() {
		System.out.println("Evicting all user cache...");
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Username not found"));
	}
}