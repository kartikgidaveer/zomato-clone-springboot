package foodapp.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import foodapp.entity.Food;
import foodapp.entity.Restaurant;
import foodapp.repository.FoodRepository;
import foodapp.repository.RestaurantRepository;
import foodapp.service.FoodService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {
	private final FoodRepository foodRepository;
	private final RestaurantRepository restaurantRepository;

	@Override
	public Food createFood(Food food) {
		return foodRepository.save(food);
	}

	@Override
	@Cacheable(value = "food_cache", key = "#id")
	public Food getFoodById(Integer id) {
		Optional<Food> food = foodRepository.findById(id);
		if (food.isPresent())
			return food.get();
		throw new NoSuchElementException("No food found with ID :" + id);
	}

	@Override
	@Cacheable(value = "food_page_cache", key = "'PAGE_' + #pageNum + '_' + #pageSize")
	public Page<Food> getAllFoods(Integer pageNum, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNum, pageSize);
		return foodRepository.findAll(pageable);
	}

	@Override
	@CachePut(value = "food_cache", key = "#id")
	@CacheEvict(value = "food_page_cache", allEntries = true)
	public Food updateFood(Integer id, Food updatedFood) {
		Food existingFood = getFoodById(id);
		existingFood.setName(updatedFood.getName());
		existingFood.setDescription(updatedFood.getDescription());
		existingFood.setPrice(updatedFood.getPrice());
		return foodRepository.save(existingFood);
	}

	@Override
	@Caching(evict = { @CacheEvict(value = "food_cache", key = "#id"),
			@CacheEvict(value = "food_page_cache", allEntries = true) })
	public void deleteFood(Integer id) {
		Food food = getFoodById(id);
		List<Restaurant> restaurants = food.getRestaurants();
		if (restaurants.size() == 0) {
			foodRepository.delete(food);
			return;
		}
		restaurants.forEach(restaurant -> restaurant.getFoods().remove(food));
		restaurantRepository.saveAll(restaurants);
		foodRepository.delete(food);
	}

	@Scheduled(cron = "0 0 0 * * ?") // At midnight
	@CacheEvict(value = { "food_cache", "food_page_cache" }, allEntries = true)
	public void clearFoodCachesDaily() {
		System.out.println("Daily food cache cleared at midnight.");
	}
}
