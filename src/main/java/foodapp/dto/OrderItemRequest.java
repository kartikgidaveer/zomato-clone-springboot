package foodapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequest {

	@Schema(description = "ID of the food item to order", example = "101")
	@NotNull
	@Min(value = 1, message = "Food ID must be greater than 0")
	private Integer foodId;

	@Schema(description = "Quantity of the food item", example = "2")
	@NotNull
	@Min(value = 1, message = "Quantity must be at least 1")
	private Integer quantity;
}
