package com.example.dineout.ui.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.dineout.R;
import com.example.dineout.data.Order;
import com.example.dineout.utils.QRCodeGenerator;
import java.text.NumberFormat;
import java.util.Locale;

public class QRCodeScreen extends AppCompatActivity {
    private ImageView qrCodeImageView;
    private TextView orderConfirmationText;
    private TextView orderDetailsText;
    private Button doneButton;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_screen);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.order_confirmation);
        
        // Set home button click listener - directly navigate to main menu
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Initialize views
        qrCodeImageView = findViewById(R.id.qr_code_image);
        orderConfirmationText = findViewById(R.id.order_confirmation_text);
        orderDetailsText = findViewById(R.id.order_details_text);
        doneButton = findViewById(R.id.done_button);

        try {
            // Get order details from intent
            Order order = getIntent().getParcelableExtra("order");
            if (order != null) {
                // Generate QR code with order details
                String qrContent = String.format("Order ID: %s\nRestaurant: %s\nTotal: %s\nStatus: %s",
                        order.getId(),
                        order.getRestaurantName(),
                        currencyFormat.format(order.getTotalAmount()),
                        order.getStatus());
                
                Bitmap qrCode = QRCodeGenerator.generateQRCode(qrContent, 512);
                if (qrCode != null) {
                    qrCodeImageView.setImageBitmap(qrCode);
                } else {
                    Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
                }

                // Set confirmation text
                orderConfirmationText.setText(getString(R.string.order_confirmation_message, order.getId()));
                
                // Set order details
                String orderDetails = String.format("Restaurant: %s\nTotal Amount: %s\nStatus: %s",
                        order.getRestaurantName(),
                        currencyFormat.format(order.getTotalAmount()),
                        order.getStatus());
                orderDetailsText.setText(orderDetails);
            } else {
                Toast.makeText(this, "Error: Order details not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error displaying order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }

        // Setup done button - directly navigate to main menu
        doneButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
} 