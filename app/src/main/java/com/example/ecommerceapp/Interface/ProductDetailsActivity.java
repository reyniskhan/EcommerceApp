package com.example.ecommerceapp.Interface;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecommerceapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

public class ProductDetailsActivity extends AppCompatActivity {

    private FloatingActionButton addToCart;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice, productName, productDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        addToCart = findViewById(R.id.add_product_to_cart);
        productImage = findViewById(R.id.product_image_detail);
        productName = findViewById(R.id.product_name_details);
        productDescription = findViewById(R.id.product_description_details);
        productPrice = findViewById(R.id.product_price_details);

    }
}
