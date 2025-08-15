package foodapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import foodapp.entity.Food;
import foodapp.entity.Order;
import foodapp.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

	@Query("select r.foods from Restaurant r where r.id=:restaurantId")
	List<Food> findFoodsByRestaurantId(@Param(value = "restaurantId") Integer restaurantId);

	@Query("select r.orders from Restaurant r where r.id=:restaurantId")
	List<Order> findOrdersByRestaurantID(@Param(value = "restaurantId") Integer restaurantId);
}
