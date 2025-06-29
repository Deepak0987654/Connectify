package com.example.connectify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.connectify.Utils.FirebaseUtil;
import com.example.connectify.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserRegister extends AppCompatActivity {
    String phoneNumber;
    EditText username;
    Button registerButton;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        phoneNumber = getIntent().getStringExtra("phone_number");
        username = findViewById(R.id.name_input);
        registerButton = findViewById(R.id.register_button);
        getUsername();
        registerButton.setOnClickListener(view -> {
            setUsername();
        });
    }

    void getUsername() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                userModel = task.getResult().toObject(UserModel.class);
                if(userModel != null){
                    username.setText(userModel.getUsername());
                }
            }
        });
    }

    void setUsername(){
       String name = username.getText().toString().trim();
       if(name.isEmpty() || name.length() < 3){
           username.setError("Username should be at least 3 characters!");
       }else{
           if(userModel!=null){
               userModel.setUsername(name);
           }else {
               userModel = new UserModel(phoneNumber, name, Timestamp.now(), FirebaseUtil.currentUserId());
           }
           FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
               if(task.isSuccessful()){
                   Intent intent = new Intent(UserRegister.this, HomeScreen.class);
                   startActivity(intent);
                   finish();
               }
           });
       }
    }


}