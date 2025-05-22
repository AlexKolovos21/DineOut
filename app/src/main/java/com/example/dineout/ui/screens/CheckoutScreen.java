package com.example.dineout.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.dineout.R;
import com.example.dineout.data.Order;
import com.example.dineout.managers.CartManager;
import com.example.dineout.utils.OrderManager;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckoutScreen extends AppCompatActivity {
    private static final String TAG = "CheckoutScreen";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private TextInputEditText addressInput;
    private RadioGroup paymentMethodGroup;
    private TextView subtotalText;
    private TextView deliveryFeeText;
    private TextView totalText;
    private CartManager cartManager;
    private OrderManager orderManager;
    private String restaurantId;
    private String restaurantName;
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_screen);

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);

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
        orderManager = OrderManager.getInstance(this);

        // Update totals
        double subtotal = cartManager.getTotal();
        double deliveryFee = 2.99;
        double total = subtotal + deliveryFee;

        subtotalText.setText(getString(R.string.subtotal_format, subtotal));
        deliveryFeeText.setText(getString(R.string.delivery_fee_format, deliveryFee));
        totalText.setText(getString(R.string.total_format, total));

        // Setup address input click listener
        addressInput.setOnClickListener(v -> startAutocomplete());

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

            try {
                // Get cart items
                Map<com.example.dineout.data.MenuItem, Integer> cartItems = cartManager.getCartItems();
                if (cartItems.isEmpty()) {
                    Toast.makeText(this, "Error: Cart is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(TAG, "Creating order with " + cartItems.size() + " items");

                // Create and save order
                Order order = new Order(
                    cartItems,
                    address,
                    paymentMethod,
                    total,
                    restaurantId,
                    restaurantName
                );

                // Save order to history
                orderManager.addOrder(order);
                Log.d(TAG, "Order saved with ID: " + order.getId());

                // Clear cart
                cartManager.clearCart();

                // Navigate to QR code screen
                Intent intent = new Intent(this, QRCodeScreen.class);
                intent.putExtra("order", order);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e(TAG, "Error creating order", e);
                Toast.makeText(this, "Error creating order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startAutocomplete() {
        List<Place.Field> fields = Arrays.asList(
            Place.Field.ID,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.NAME
        );

        Intent intent = new Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY,
            fields)
            .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                addressInput.setText(place.getAddress());
                Log.i(TAG, "Place: " + place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e(TAG, "Error: " + status.getStatusMessage());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 