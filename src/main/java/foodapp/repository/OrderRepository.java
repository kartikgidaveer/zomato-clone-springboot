package foodapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import foodapp.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}
