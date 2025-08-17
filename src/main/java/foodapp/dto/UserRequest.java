package foodapp.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
	@NotBlank
	private String username;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	private String email;

	private String contactNumber;

	private String address;

	private String role;

	@NotBlank
	@Column(nullable = false)
	private String password;
}
