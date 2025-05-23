package com.example.dineout.ui.screens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import com.example.dineout.R;
import com.example.dineout.data.Cart;
import com.example.dineout.data.Order;
import com.example.dineout.data.Restaurant;
import com.example.dineout.repositories.CartRepository;
import com.example.dineout.repositories.OrderRepository;
import com.example.dineout.utils.OrderManager;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckoutScreen extends AppCompatActivity {
    private static final String TAG = "CheckoutScreen";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private Restaurant restaurant;
    private Cart cart;
    private String restaurantName;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationClient;
    private TextInputEditText deliveryAddressEdit;
    private RadioGroup paymentMethodGroup;
    private String paymentMethod;
    private TextView subtotalText;
    private TextView deliveryFeeText;
    private TextView totalText;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
    private CartRepository cartRepository;
    private OrderRepository orderRepository;
    private double selectedLatitude;
    private double selectedLongitude;
    private String selectedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_screen);

        // Initialize repositories
        cartRepository = new CartRepository(this);
        orderRepository = new OrderRepository(this);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            restaurant = intent.getParcelableExtra("restaurant");
            cart = intent.getParcelableExtra("cart");
            if (restaurant == null || cart == null) {
                Toast.makeText(this, R.string.error_loading_data, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            restaurantName = restaurant.getName();
        }

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);

        // Initialize views
        deliveryAddressEdit = findViewById(R.id.address_input);
        paymentMethodGroup = findViewById(R.id.payment_method_group);
        subtotalText = findViewById(R.id.subtotal_amount);
        deliveryFeeText = findViewById(R.id.delivery_fee_amount);
        totalText = findViewById(R.id.total_amount);

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.checkout);

        // Update totals
        double subtotal = cart.getTotal();
        double deliveryFee = 2.99;
        double total = subtotal + deliveryFee;

        subtotalText.setText(getString(R.string.subtotal_format, subtotal));
        deliveryFeeText.setText(getString(R.string.delivery_fee_format, deliveryFee));
        totalText.setText(getString(R.string.total_format, total));

        // Setup address input click listener
        deliveryAddressEdit.setOnClickListener(v -> {
            hideKeyboard();
            startAutocomplete();
        });

        // Setup place order button
        findViewById(R.id.place_order_button).setOnClickListener(v -> {
            hideKeyboard();
            placeOrder();
        });

        // Setup root layout click listener to hide keyboard
        findViewById(R.id.root_layout).setOnClickListener(v -> hideKeyboard());

        // Add current location button next to address input
        ImageButton currentLocationButton = findViewById(R.id.currentLocationButton);
        currentLocationButton.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                getCurrentLocation();
            } else {
                requestLocationPermission();
            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
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
                deliveryAddressEdit.setText(place.getAddress());
                if (place.getLatLng() != null) {
                    selectedLatitude = place.getLatLng().latitude;
                    selectedLongitude = place.getLatLng().longitude;
                }
                Log.i(TAG, "Place: " + place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e(TAG, "Error: " + status.getStatusMessage());
            }
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

    private boolean validateInputs() {
        String address = deliveryAddressEdit.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(this, R.string.please_enter_address, Toast.LENGTH_SHORT).show();
            return false;
        }

        int selectedPaymentId = paymentMethodGroup.getCheckedRadioButtonId();
        if (selectedPaymentId == -1) {
            Toast.makeText(this, R.string.please_select_payment_method, Toast.LENGTH_SHORT).show();
            return false;
        }

        paymentMethod = ((RadioButton) findViewById(selectedPaymentId)).getText().toString();
        return true;
    }

    private void placeOrder() {
        if (selectedLatitude == 0.0 && selectedLongitude == 0.0) {
            Toast.makeText(this, "Please select a valid delivery address", Toast.LENGTH_SHORT).show();
            return;
        }

        double distance = restaurant.calculateDistance(selectedLatitude, selectedLongitude);
        if (distance > 10) {
            Toast.makeText(this, getString(R.string.distance_too_far, distance), Toast.LENGTH_LONG).show();
            return;
        }

        // Create order
        Order order = new Order(
            cart.getItems(),
            selectedAddress,
            paymentMethod,
            cart.getTotal(),
            restaurant.getId(),
            restaurant.getName()
        );

        // Save order
        orderRepository.saveOrder(order, new OrderRepository.OnOrderSavedListener() {
            @Override
            public void onOrderSaved(Order savedOrder) {
                // Clear cart
                cart.clear();

                // Navigate to QR code screen
                Intent intent = new Intent(CheckoutScreen.this, QRCodeScreen.class);
                intent.putExtra("order", savedOrder);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CheckoutScreen.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, location -> {
                if (location != null) {
                    selectedLatitude = location.getLatitude();
                    selectedLongitude = location.getLongitude();
                    
                    // Get address from coordinates
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(selectedLatitude, selectedLongitude, 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            StringBuilder addressText = new StringBuilder();
                            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                addressText.append(address.getAddressLine(i));
                                if (i < address.getMaxAddressLineIndex()) {
                                    addressText.append(", ");
                                }
                            }
                            selectedAddress = addressText.toString();
                            deliveryAddressEdit.setText(selectedAddress);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }
} 