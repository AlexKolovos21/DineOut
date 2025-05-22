package com.example.dineout.ui.screens;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineout.R;
import com.example.dineout.adapters.OrderItemAdapter;
import com.example.dineout.data.Order;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderDetailScreen extends AppCompatActivity {
    private TextView restaurantNameText;
    private TextView orderDateText;
    private TextView orderStatusText;
    private TextView deliveryAddressText;
    private TextView paymentMethodText;
    private TextView subtotalText;
    private TextView deliveryFeeText;
    private TextView totalText;
    private RecyclerView itemsRecyclerView;
    private ImageView qrCodeImage;
    private OrderItemAdapter orderItemAdapter;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_screen);

        // Get order from intent
        Order order = getIntent().getParcelableExtra("order");
        if (order == null) {
            finish();
            return;
        }

        // Initialize views
        restaurantNameText = findViewById(R.id.restaurant_name);
        orderDateText = findViewById(R.id.order_date);
        orderStatusText = findViewById(R.id.order_status);
        deliveryAddressText = findViewById(R.id.delivery_address);
        paymentMethodText = findViewById(R.id.payment_method);
        subtotalText = findViewById(R.id.subtotal_amount);
        deliveryFeeText = findViewById(R.id.delivery_fee_amount);
        totalText = findViewById(R.id.total_amount);
        itemsRecyclerView = findViewById(R.id.items_recycler_view);
        qrCodeImage = findViewById(R.id.qr_code_image);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.order_details);

        // Setup RecyclerView
        orderItemAdapter = new OrderItemAdapter();
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsRecyclerView.setAdapter(orderItemAdapter);

        // Set order details
        restaurantNameText.setText(order.getRestaurantName());
        orderDateText.setText(order.getFormattedDate());
        orderStatusText.setText(order.getStatus());
        deliveryAddressText.setText(order.getDeliveryAddress());
        paymentMethodText.setText(order.getPaymentMethod());

        // Set order items
        orderItemAdapter.submitList(new ArrayList<>(order.getItems().keySet()), order.getItems());

        double subtotal = order.getTotalAmount() - 2.99; // Subtract delivery fee
        subtotalText.setText(currencyFormat.format(subtotal));
        deliveryFeeText.setText(currencyFormat.format(2.99));
        totalText.setText(currencyFormat.format(order.getTotalAmount()));

        // Generate QR code
        generateQRCode(order.getId());
    }

    private void generateQRCode(String orderId) {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(orderId, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            if (bitmap != null) {
                qrCodeImage.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, R.string.qr_code_generation_failed, Toast.LENGTH_SHORT).show();
            }
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.qr_code_generation_error, e.getMessage()), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 