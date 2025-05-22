package com.example.dineout.repositories;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.dineout.data.AppDatabase;
import com.example.dineout.data.Order;
import com.example.dineout.data.OrderDao;
import com.example.dineout.data.OrderEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderRepository {
    private final OrderDao orderDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public interface OnOrderSavedListener {
        void onOrderSaved(Order order);
        void onError(String error);
    }

    public OrderRepository(Context context) {
        orderDao = AppDatabase.getInstance(context).orderDao();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void saveOrder(Order order, OnOrderSavedListener listener) {
        executorService.execute(() -> {
            try {
                OrderEntity orderEntity = new OrderEntity(order);
                orderDao.insert(orderEntity);
                mainHandler.post(() -> listener.onOrderSaved(order));
            } catch (Exception e) {
                mainHandler.post(() -> listener.onError("Failed to save order: " + e.getMessage()));
            }
        });
    }

    public LiveData<List<Order>> getOrders() {
        MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<OrderEntity> orderEntities = orderDao.getAllOrders().getValue();
            if (orderEntities != null) {
                List<Order> orders = new ArrayList<>();
                for (OrderEntity entity : orderEntities) {
                    orders.add(entity.toOrder());
                }
                mainHandler.post(() -> ordersLiveData.setValue(orders));
            }
        });
        return ordersLiveData;
    }

    public LiveData<Order> getOrder(String orderId) {
        MutableLiveData<Order> orderLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            OrderEntity orderEntity = orderDao.getOrder(orderId).getValue();
            if (orderEntity != null) {
                mainHandler.post(() -> orderLiveData.setValue(orderEntity.toOrder()));
            }
        });
        return orderLiveData;
    }

    public void updateOrderStatus(String orderId, String newStatus) {
        executorService.execute(() -> {
            OrderEntity orderEntity = orderDao.getOrder(orderId).getValue();
            if (orderEntity != null) {
                orderEntity.setStatus(newStatus);
                orderDao.update(orderEntity);
            }
        });
    }
} 