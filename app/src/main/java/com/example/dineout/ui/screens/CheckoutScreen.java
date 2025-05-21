package com.example.dineout.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dineout.R;
import com.example.dineout.data.Order;
import com.example.dineout.utils.CartManager;
import com.example.dineout.utils.OrderManager;

public class CheckoutScreen extends AppCompatActivity {
    private EditText addressInput;
    private RadioGroup paymentMethodGroup;
    private TextView subtotalText;
    private TextView deliveryFeeText;
    private TextView totalText;
    private CartManager cartManager;
    private OrderManager orderManager;
    private String restaurantId;
    private String restaurantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_screen);

        // Get restaurant information from intent
        restaurantId = getIntent().getStringExtra("restaurant_id");
        restaurantName = getIntent().getStringExtra("restaurant_name");

        if (restaurantId == null || restaurantName == null) {
            Toast.makeText(this, "Error: Restaurant information not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        addressInput = findViewById(R.id.address_input);
        paymentMethodGroup = findViewById(R.id.payment_method_group);
        subtotalText = findViewById(R.id.subtotal_amount);
        deliveryFeeText = findViewById(R.id.delivery_fee_amount);
        totalText = findViewById(R.id.total_amount);

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.checkout);

        // Initialize managers
        cartManager = CartManager.getInstance();
        orderManager = OrderManager.getInstance();

        // Update totals
        double subtotal = cartManager.getTotal();
        double deliveryFee = 2.99;
        double total = subtotal + deliveryFee;

        subtotalText.setText(getString(R.string.subtotal_format, subtotal));
        deliveryFeeText.setText(getString(R.string.delivery_fee_format, deliveryFee));
        totalText.setText(getString(R.string.total_format, total));

        // Setup place order button
        findViewById(R.id.place_order_button).setOnClickListener(v -> {
            String address = addressInput.getText().toString().trim();
            if (address.isEmpty()) {
                Toast.makeText(this, R.string.please_enter_address, Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedPaymentId = paymentMethodGroup.getCheckedRadioButtonId();
            if (selectedPaymentId == -1) {
                Toast.makeText(this, R.string.please_select_payment_method, Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedPayment = findViewById(selectedPaymentId);
            String paymentMethod = selectedPayment.getText().toString();

            // Create and save order
            Order order = new Order(
                cartManager.getCartItems(),
                address,
                paymentMethod,
                total,
                restaurantId,
                restaurantName
            );

            // Save order to history
            orderManager.addOrder(order);

            // Clear cart
            cartManager.clearCart();

            // Navigate to QR code screen
            Intent intent = new Intent(this, QRCodeScreen.class);
            intent.putExtra("order", order);
            startActivity(intent);
            finish();
        });
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