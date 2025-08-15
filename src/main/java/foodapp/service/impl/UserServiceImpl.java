package foodapp.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import foodapp.entity.User;
import foodapp.repository.UserRepository;
import foodapp.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	@Override
	public User createUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public User getUser(Integer id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("User not found with id :" + id));
	}

	@Override
	public List<User> getAllUsers() {
		List<User> users = userRepository.findAll();
		if (users == null || users.isEmpty())
			throw new NoSuchElementException("No users found");
		return users;
	}

	@Override
	public User updateUser(User user, Integer id) {
		User existingUser = getUser(id);
		existingUser.setName(user.getName());
		existingUser.setAddress(user.getAddress());
		existingUser.setContactNumber(user.getContactNumber());
		existingUser.setEmail(user.getEmail());
		existingUser.setPassword(user.getPassword());
		return userRepository.save(existingUser);
	}

	@Override
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
	public void deleteUser(Integer id) {
		User user = getUser(id);
		userRepository.delete(user);

	}

}