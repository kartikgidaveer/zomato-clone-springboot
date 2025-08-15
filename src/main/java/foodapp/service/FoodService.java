package foodapp.service;

import org.springframework.data.domain.Page;

import foodapp.entity.Food;

public interface FoodService {
	Food createFood(Food food);

	Food getFoodById(Integer id);

	Page<Food> getAllFoods(Integer pageNum, Integer pageSize);

	Food updateFood(Integer id, Food food);

	void deleteFood(Integer id);

}
