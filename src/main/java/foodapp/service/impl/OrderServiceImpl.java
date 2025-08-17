package foodapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
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
import foodapp.repository.UserRepository;
import foodapp.service.FoodService;
import foodapp.service.OrderService;
import foodapp.service.RestaurantService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final RestaurantService restaurantService;
	private final FoodService foodService;
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;

	/**
	 * Generates bill for an order request. Caching is used so repeated bill
	 * calculations for the same restaurant & items are fast.
	 */
	@Override
	@Cacheable(value = "bills", key = "#orderRequest.restaurantId + '-' + #orderRequest.orderItems.hashCode()")
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

	/**
	 * Processes payment and places order. Clears bill cache since prices might
	 * change after an order.
	 */
	@Override
	@CacheEvict(value = "bills", allEntries = true)
	public String payAndPlaceOrder(PaymentDto payment) {
		if (payment.isPaymentSuccessful()) {
			Order order = new Order();

			Restaurant restaurant = restaurantService.getById(payment.getRestaurantId());
			User user = userRepository.findById(payment.getUserId())
					.orElseThrow(() -> new NoSuchElementException("User not found with id :" + payment.getUserId()));
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
			return "Order has been placed by " + user.getUsername();
		} else {
			throw new PaymentFailedException("Payment was not successful, hence order cannot be placed");
		}
	}

	@Override
	@CacheEvict(value = "bills", allEntries = true)
	public void deleteOrderById(Integer id) {
		if (!orderRepository.existsById(id)) {
			throw new NoSuchElementException("No order present with id: " + id);
		}
		orderRepository.deleteById(id);
	}

	@Override
	public Order updateOrder(Integer id, Order updatedOrder) {
		return null;
	}

	@Override
	@CachePut(value = "orders", key = "#id")
	public Order updateOrderStatusByAdmin(Integer id, OrderStatus status) {
		Order order = getOrder(id);
		order.setStatus(status);
		return orderRepository.save(order);
	}

	@Override
	@Cacheable(value = "orders", key = "#id")
	public Order getOrder(Integer id) {
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Order not found with id: " + id));
		return order;
	}

	@Override
	@CacheEvict(value = "orders", key = "#id")
	public String cancelOrder(Integer id) {
		Order order = getOrder(id);
		order.setStatus(OrderStatus.CANCELLED);
		orderRepository.save(order);
		return "Order has been cancelled";
	}

	@Override
	@Cacheable(value = "ordersAll")
	public List<Order> getAllOrders() {
		List<Order> orders = orderRepository.findAll();
		if (orders == null || orders.isEmpty()) {
			throw new NoSuchElementException("No orders present");
		}
		return orders;
	}

	/**
	 * Scheduled cache clearing to avoid old prices. Runs every hour.
	 */
	@Scheduled(fixedRate = 60 * 60 * 1000)
	@CacheEvict(value = "bills", allEntries = true)
	public void clearBillCacheScheduled() {
		System.out.println(("Scheduled task: Cleared all bill caches to avoid old data."));
	}

}