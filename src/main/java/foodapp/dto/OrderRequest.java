package foodapp.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

	@NotEmpty(message = "Order items list cannot be empty")
	private List<OrderItemRequest> orderItems;

	@NotNull
	private Integer restaurantId;
}
