package foodapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import foodapp.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

}
