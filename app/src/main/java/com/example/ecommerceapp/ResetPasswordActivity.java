package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerceapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    private String check = "";
    private TextView pageTitle, titleQuestions;
    private EditText phoneNumber, question1, question2;
    private Button verifyBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        check = getIntent().getStringExtra("check");
        pageTitle = findViewById(R.id.page_title);
        titleQuestions = findViewById(R.id.title_questions);
        phoneNumber = findViewById(R.id.find_phone_number);
        question1 = findViewById(R.id.question_1);
        question2 = findViewById(R.id.question_2);
        verifyBtn = findViewById(R.id.verify_btn);

    }

    @Override
    protected void onStart() {
        super.onStart();

        phoneNumber.setVisibility(View.GONE);

        if (check.equals("settings")){


            pageTitle.setText("Set Questions");
            titleQuestions.setText("Please set answers to following security questions:");
            verifyBtn.setText("Set");
            displayPreviousInfo();

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnswers();


                }
            });




        }else if (check.equals("login")){

            phoneNumber.setVisibility(View.VISIBLE);


            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    verifyUser();

                }
            });

        }

    }

    private void verifyUser() {


        final String phone = phoneNumber.getText().toString();
        final String answer1 = question1.getText().toString().toLowerCase();
        final String answer2 = question2.getText().toString().toLowerCase();

        if (!phone.equals("") && !answer1.equals("") && !answer2.equals("")){

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(phone);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){

                        if (dataSnapshot.hasChild("Security Questions")){
                            String ans1 = dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                            String ans2 = dataSnapshot.child("Security Questions").child("answer2").getValue().toString();

                            if (!ans1.equals(answer1)){
                                Toast.makeText(ResetPasswordActivity.this,"Your First answer is wrong",Toast.LENGTH_SHORT).show();
                            }else if(!ans2.equals(answer2)){
                                Toast.makeText(ResetPasswordActivity.this,"Your Second answer is wrong",Toast.LENGTH_SHORT).show();

                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                                builder.setTitle("New Password: ");

                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("Enter New Password Here");
                                builder.setView(newPassword);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (!newPassword.getText().toString().equals("")){
                                            ref.child("password")
                                                    .setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(ResetPasswordActivity.this,
                                                                        "Password change successfully",Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));


                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                                builder.show();
                            }
                        }

                        else{
                            Toast.makeText(ResetPasswordActivity.this,"You haven't updated Security Questions",Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(ResetPasswordActivity.this,"This User doesnt exist",Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{
            Toast.makeText(ResetPasswordActivity.this,"Please complete the form",Toast.LENGTH_SHORT).show();
        }





    }

    private void displayPreviousInfo(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());

        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){



                        String ans1 = dataSnapshot.child("answer1").getValue().toString();
                        String ans2 = dataSnapshot.child("answer2").getValue().toString();


                        question1.setText(ans1);
                        question2.setText(ans2);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setAnswers(){
        String answer1 = question1.getText().toString().toLowerCase();
        String answer2 = question2.getText().toString().toLowerCase();

        if (question1.equals("") && question2.equals("")){
            Toast.makeText(ResetPasswordActivity.this,"Please answer both questions",Toast.LENGTH_SHORT).show();

        }else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(Prevalent.currentOnlineUser.getPhone());

            HashMap<String, Object> userDataMap = new HashMap<>();
            userDataMap.put("answer1",answer1);
            userDataMap.put("answer2",answer2);

            ref.child("Security Questions").updateChildren(userDataMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this,"Security Questions Set Successfully",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this,HomeActivity.class));
                            }

                        }
                    });

        }
    }
}
