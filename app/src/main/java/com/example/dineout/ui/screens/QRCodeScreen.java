package com.example.dineout.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dineout.R;
import com.example.dineout.data.Order;
import com.example.dineout.utils.QRCodeGenerator;

public class QRCodeScreen extends AppCompatActivity {
    private ImageView qrCodeImageView;
    private TextView orderConfirmationText;
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_screen);

        // Initialize views
        qrCodeImageView = findViewById(R.id.qr_code_image);
        orderConfirmationText = findViewById(R.id.order_confirmation_text);
        doneButton = findViewById(R.id.done_button);

        // Get order details from intent
        Order order = getIntent().getParcelableExtra("order");
        if (order != null) {
            // Generate QR code with order details
            String qrContent = String.format("Order ID: %s\nTotal: $%.2f\nStatus: %s",
                    order.getId(), order.getTotalAmount(), order.getStatus());
            qrCodeImageView.setImageBitmap(QRCodeGenerator.generateQRCode(qrContent, 512));

            // Set confirmation text
            orderConfirmationText.setText(getString(R.string.order_confirmation_message, order.getId()));
        }

        // Setup done button
        doneButton.setOnClickListener(v -> {
            finish();
        });
    }
} 