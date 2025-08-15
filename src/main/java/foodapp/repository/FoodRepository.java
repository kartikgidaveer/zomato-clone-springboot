package foodapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import foodapp.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Integer> {

}
