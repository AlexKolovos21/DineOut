package com.example.dineout.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    void insert(OrderEntity order);

    @Update
    void update(OrderEntity order);

    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    LiveData<List<OrderEntity>> getAllOrders();

    @Query("SELECT * FROM orders WHERE id = :orderId")
    LiveData<OrderEntity> getOrder(String orderId);

    @Query("DELETE FROM orders")
    void deleteAllOrders();
} 