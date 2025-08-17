package foodapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import foodapp.entity.Order;
import foodapp.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("SELECT u.orders from User u where id=:userId ")
	List<Order> findOrdersByUserId(@Param(value = "userId") Integer userId);

	Optional<User> findByUsername(String username);
}
