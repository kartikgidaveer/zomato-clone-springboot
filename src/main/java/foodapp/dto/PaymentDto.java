package foodapp.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentDto {

	@NotEmpty(message = "Order items list cannot be empty")
	@Valid
	private List<OrderItemRequest> orderItems;

	private boolean paymentSuccessful;

	@NotNull
	@Min(value = 1)
	private Integer restaurantId;

	@NotNull
	@Min(value = 1)
	private Integer userId;
}
