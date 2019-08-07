package com.example.ecommerceapp.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ecommerceapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, description, price, name, saveCurrentDate, saveCurrentTime;
    private Button addNewProductButton;
    private EditText inputProductName, inputProductDescription, inputProductPrice;
    private ImageView inputProductImage;
    private static final int galleryPick = 1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference productImagesRef;
    private DatabaseReference productRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        Toast.makeText(this,"Welcome",Toast.LENGTH_SHORT).show();


        categoryName = getIntent().getExtras().get("category").toString();
        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        loadingBar = new ProgressDialog(AdminAddNewProductActivity.this);

        Toast.makeText(this,categoryName,Toast.LENGTH_SHORT).show();

        addNewProductButton = findViewById(R.id.add_new_product);
        inputProductName = findViewById(R.id.product_name);
        inputProductDescription = findViewById(R.id.product_description);
        inputProductPrice = findViewById(R.id.product_price);
        inputProductImage = findViewById(R.id.select_product_image);


        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OpenGallery();

            }
        });



        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateProductData();
            }
        });




    }

    private void validateProductData() {

        description = inputProductDescription.getText().toString();
        price = inputProductPrice.getText().toString();
        name = inputProductName.getText().toString();

        if (ImageUri == null){
            Toast.makeText(AdminAddNewProductActivity.this, "Product image is mandatory",Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(description)){
            Toast.makeText(AdminAddNewProductActivity.this, "Product description is mandatory",Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(name)){
            Toast.makeText(AdminAddNewProductActivity.this, "Product name is mandatory",Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(price)){
            Toast.makeText(AdminAddNewProductActivity.this, "Product price is mandatory",Toast.LENGTH_SHORT).show();

        }else{

            StoreProductInformation();

        }


    }

    private void StoreProductInformation() {

        loadingBar.setTitle("Add new Product");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("mm, dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;


        final StorageReference filePath = productImagesRef.child(ImageUri.getLastPathSegment()+ productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.getMessage();
                Toast.makeText(AdminAddNewProductActivity.this,message,Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this,"Image uploaded successfully",Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();

                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()){

                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminAddNewProductActivity.this,"Getting Image Product successfull!!!",Toast.LENGTH_SHORT).show();

                            saveProductInfoToDatabase();
                        }

                    }
                });

            }
        });


    }

    private void OpenGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
            inputProductImage.setImageURI(ImageUri);
        }

    }

    private void saveProductInfoToDatabase(){

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",description);
        productMap.put("image",downloadImageUrl);
        productMap.put("category",categoryName);
        productMap.put("price",price);
        productMap.put("name",name);

        productRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);
                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this,"Product is added successfully",Toast.LENGTH_SHORT).show();
                        }else{
                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}
