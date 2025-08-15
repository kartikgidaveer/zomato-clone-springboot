package foodapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillResponse {
	private String restaurantName;
	private String orderSummary;
	private float totalprice;
}
