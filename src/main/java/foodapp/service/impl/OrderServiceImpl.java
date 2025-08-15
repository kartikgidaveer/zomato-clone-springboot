package foodapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import foodapp.dto.BillResponse;
import foodapp.dto.OrderItemRequest;
import foodapp.dto.OrderRequest;
import foodapp.dto.PaymentDto;
import foodapp.entity.Food;
import foodapp.entity.Order;
import foodapp.entity.OrderItem;
import foodapp.entity.OrderStatus;
import foodapp.entity.Restaurant;
import foodapp.entity.User;
import foodapp.exception.PaymentFailedException;
import foodapp.repository.OrderRepository;
import foodapp.service.FoodService;
import foodapp.service.OrderService;
import foodapp.service.RestaurantService;
import foodapp.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final RestaurantService restaurantService;
	private final FoodService foodService;
	private final UserService userService;
	private final OrderRepository orderRepository;

	@Override
	public BillResponse generateBill(OrderRequest orderRequest) {
		Restaurant restaurant = restaurantService.getById(orderRequest.getRestaurantId());
		StringBuilder summary = new StringBuilder();

		float totalPrice = 0;

		for (OrderItemRequest orderItem : orderRequest.getOrderItems()) {
			Food food = foodService.getFoodById(orderItem.getFoodId());
			float price = food.getPrice() * orderItem.getQuantity();
			totalPrice += price;
			summary.append(food.getName()).append(" X ").append(orderItem.getQuantity()).append(" = ").append(price)
					.append("\n");
		}

		return new BillResponse(restaurant.getName(), summary.toString(), totalPrice);
	}

	@Override
	public String payAndPlaceOrder(PaymentDto payment) {
		if (payment.isPaymentSuccessfull()) {
			Order order = new Order();

			Restaurant restaurant = restaurantService.getById(payment.getRestaurantId());
			User user = userService.getUser(payment.getUserId());
			order.setRestaurant(restaurant);
			order.setUser(user);

			List<OrderItem> items = new ArrayList<>();
			double totalPrice = 0;

			for (OrderItemRequest request : payment.getOrderItems()) {
				Food food = foodService.getFoodById(request.getFoodId());

				OrderItem orderItem = new OrderItem();
				orderItem.setFood(food);
				orderItem.setQuantity(request.getQuantity());

				orderItem.setOrder(order);
				items.add(orderItem);

				double price = food.getPrice() * request.getQuantity();
				totalPrice += price;
			}

			order.setTotalPrice(totalPrice);
			order.setOrderItems(items);
			order.setStatus(OrderStatus.PLACED);
			orderRepository.save(order);
			return "Order has been placed by " + user.getName();
		} else {
			throw new PaymentFailedException("Payment was not successful, hence order cannot be placed");
		}
	}

	@Override
	public void deleteOrderById(Integer id) {
		if (!orderRepository.existsById(id)) {
			throw new NoSuchElementException("No order present with id: " + id);
		}
		orderRepository.deleteById(id);
	}

	@Override
	public Order updateOrder(Integer id, Order updatedOrder) {
//		Order existingOrder = orderRepository.findById(id)
//				.orElseThrow(() -> new NoSuchElementException("No order found with id: " + id));
		return null;
	}

	@Override
	public Order updateOrderStatusByAdmin(Integer id, OrderStatus status) {
		Order order = getOrder(id);
		order.setStatus(status);
		return orderRepository.save(order);
	}

	@Override
	public Order getOrder(Integer id) {
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Order not found with id: " + id));
		return order;
	}

	@Override
	public String cancelOrder(Integer id) {
		Order order = getOrder(id);
		order.setStatus(OrderStatus.CANCELLED);
		orderRepository.save(order);
		return "Order has been cancelled";
	}

	@Override
	public List<Order> getAllOrders() {
		List<Order> orders = orderRepository.findAll();
		if (orders == null || orders.isEmpty()) {
			throw new NoSuchElementException("No orders present");
		}
		return orders;
	}

}