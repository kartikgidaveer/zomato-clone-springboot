package foodapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

	private Integer id;

	private String username;

	private String email;

	private String contactNumber;

	private String address;

	private String role;

	private String profileImage;
}
