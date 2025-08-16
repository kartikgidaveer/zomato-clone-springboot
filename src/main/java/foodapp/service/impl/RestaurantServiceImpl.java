package foodapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import foodapp.entity.Food;
import foodapp.entity.Order;
import foodapp.entity.Restaurant;
import foodapp.exception.NoFoodsAssignedException;
import foodapp.repository.FoodRepository;
import foodapp.repository.RestaurantRepository;
import foodapp.service.RestaurantService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepo;
    private final FoodRepository foodRepository;

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepo.save(restaurant);
    }

    @Override
    @Cacheable(value = "restaurant_cache", key = "#id")
    public Restaurant getById(Integer id) {
        return restaurantRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Restaurant with ID:" + id + " not found"));
    }

    @Override
    @Caching(evict = {
    	    @CacheEvict(value = {"restaurant_cache", "restaurant_foods_cache"}, key = "#id"),
    	    @CacheEvict(value = "restaurant_cache", allEntries = true) // clears paginated list cache
    	})
    public void deleteRestaurant(Integer id) {
        Restaurant response = getById(id);
        restaurantRepo.delete(response);
    }

    @Override
    @Cacheable(value = "restaurant_cache", key = "'ALL_' + #pageNum + '_' + #pageSize + '_' + #sortBy")
    public Page<Restaurant> getAllRestaurants(int pageNum, int pageSize, String sortBy) {
        Sort sort = Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        return restaurantRepo.findAll(pageable);
    }

    @Override
    @CachePut(value = "restaurant_cache", key = "#id")
    @CacheEvict(value = "restaurant_cache", allEntries = true) // clear paginated cache
    public Restaurant updateRestaurant(Integer id, Restaurant updatedRest) {
        Restaurant restaurant = getById(id);
        restaurant.setName(updatedRest.getName());
        restaurant.setAddress(updatedRest.getAddress());
        restaurant.setContactNumber(updatedRest.getContactNumber());
        restaurant.setEmail(updatedRest.getEmail());
        return restaurantRepo.save(restaurant);
    }

    @Override
    @CachePut(value = "restaurant_cache", key = "#id")
    @CacheEvict(value = "restaurant_foods_cache", key = "#id") // clear foods cache
    public Restaurant assignFoodItems(Integer id, Set<Integer> foodId) {
        Restaurant restaurant = getById(id);
        List<Food> foodItems = new ArrayList<>();
        for (Integer food_id : foodId) {
            Food food = foodRepository.findById(food_id)
                    .orElseThrow(() -> new NoSuchElementException("Food with ID: " + food_id + " not found"));
            foodItems.add(food);
        }
        restaurant.setFoods(foodItems);
        return restaurantRepo.save(restaurant);
    }

    @Override
    @Cacheable(value = "restaurant_foods_cache", key = "#restaurantId")
    public List<Food> findFoodsByRestaurantId(Integer restaurantId) {
        List<Food> foods = restaurantRepo.findFoodsByRestaurantId(restaurantId);
        if (foods.isEmpty()) {
            throw new NoFoodsAssignedException("No menu items are currently listed for Restaurant "
                    + ". Please check back later or try refreshing.");
        }
        return foods;
    }

    @Override
    public List<Order> findOrdersByRestaurantID(Integer restaurantId) {
        List<Order> orders = restaurantRepo.findOrdersByRestaurantID(restaurantId);
        if (orders == null || orders.isEmpty())
            throw new NoSuchElementException("Restaurant got no orders to process, Try again");
        return orders;
    }

    /**
     * Periodically evict all restaurant caches to avoid stale data.
     */
    @Scheduled(fixedRate = 3600000) // every hour
    @CacheEvict(value = {"restaurant_cache", "restaurant_foods_cache"}, allEntries = true)
    public void clearCachesPeriodically() {
        System.out.println("Clearing restaurant caches...");
    }
}
