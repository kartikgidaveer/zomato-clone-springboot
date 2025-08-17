package foodapp.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import foodapp.dto.UserRequest;
import foodapp.dto.UserResponse;

public interface UserService {

	UserResponse createUser(UserRequest request);

	UserResponse getUser(Integer id);

	List<UserResponse> getAllUsers();

	UserResponse updateUser(UserRequest request, Integer id);

	void deleteUser(Integer id);

	String uploadImage(MultipartFile file, Integer id) throws IOException;

	byte[] getImage(Integer id);

}