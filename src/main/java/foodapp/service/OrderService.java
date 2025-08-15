package foodapp.service;

import java.util.List;

import foodapp.dto.BillResponse;
import foodapp.dto.OrderRequest;
import foodapp.dto.PaymentDto;
import foodapp.entity.Order;
import foodapp.entity.OrderStatus;

public interface OrderService {
	BillResponse generateBill(OrderRequest orderRequest);

	String payAndPlaceOrder(PaymentDto payment);

	void deleteOrderById(Integer id);

	Order getOrder(Integer id);

	List<Order> getAllOrders();

	Order updateOrderStatusByAdmin(Integer id, OrderStatus status);

	String cancelOrder(Integer id);

	Order updateOrder(Integer id, Order updatedOrder);
}