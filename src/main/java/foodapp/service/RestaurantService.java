package foodapp.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import foodapp.entity.Food;
import foodapp.entity.Order;
import foodapp.entity.Restaurant;

public interface RestaurantService {
	Restaurant createRestaurant(Restaurant restaurant);

	Restaurant getById(Integer id);

	Page<Restaurant> getAllRestaurants(int pageNum, int pageSize, String sortBy);

	Restaurant assignFoodItems(Integer id, Set<Integer> foodId);

	void deleteRestaurant(Integer id);

	Restaurant updateRestaurant(Integer id, Restaurant updatedRest);

	List<Food> findFoodsByRestaurantId(Integer restaurantId);

	List<Order> findOrdersByRestaurantID(Integer restaurantId);

}
