package foodapp.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import foodapp.entity.Food;
import foodapp.entity.Restaurant;
import foodapp.repository.FoodRepository;
import foodapp.repository.RestaurantRepository;
import foodapp.service.FoodService;

@Service
public class FoodServiceImpl implements FoodService {
	@Autowired
	private FoodRepository foodRepository;
	@Autowired
	private RestaurantRepository restaurantRepository;

	@Override
	public Food createFood(Food food) {
		return foodRepository.save(food);
	}

	@Override
	public Food getFoodById(Integer id) {
		Optional<Food> food = foodRepository.findById(id);
		if (food.isPresent())
			return food.get();
		throw new NoSuchElementException("No food found with ID :" + id);
	}

	@Override
	public Page<Food> getAllFoods(Integer pageNum, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNum, pageSize);
		return foodRepository.findAll(pageable);
	}

	@Override
	public Food updateFood(Integer id, Food updatedFood) {
		Food existingFood = getFoodById(id);
		existingFood.setName(updatedFood.getName());
		existingFood.setDescription(updatedFood.getDescription());
		existingFood.setPrice(updatedFood.getPrice());
		return foodRepository.save(existingFood);
	}

	@Override
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

}
