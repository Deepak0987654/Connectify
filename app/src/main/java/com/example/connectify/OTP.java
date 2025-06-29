package com.example.connectify;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.time.Duration;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OTP extends AppCompatActivity {
    String verificationId;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String phoneNumber;
    Long timeoutDuration = 60L;
    PhoneAuthProvider.ForceResendingToken resendToken;
    PinView pin;
    TextView timerText;
    ImageButton back_btn;
    TextView phoneNumberTxt;
    Button verifyButton;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back_btn = findViewById(R.id.back_arrow_icon_btn);
        phoneNumberTxt = findViewById(R.id.phone_number_txt);
        verifyButton = findViewById(R.id.verify_btn);
        timerText = findViewById(R.id.timer_txt);
        pin = findViewById(R.id.pinview);
        phoneNumber = getIntent().getStringExtra("phone_number");
        phoneNumberTxt.setText(phoneNumber);
        timerText.setEnabled(false);
        sendOtp(phoneNumber,false);

        back_btn.setOnClickListener(v -> {
            onBackPressed();
        });

        verifyButton.setOnClickListener(view -> {

            String enterOtp = Objects.requireNonNull(pin.getText()).toString().trim();
            if(enterOtp.isEmpty()){
                Toast.makeText(getApplicationContext(),"Please enter OTP",Toast.LENGTH_LONG).show();
                return;
            }
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enterOtp);
            signIn(credential);
        });
        timerText.setOnClickListener(view -> {
            sendOtp(phoneNumber,true);
            verifyButton.setVisibility(View.VISIBLE);
            timerText.setEnabled(false);
        });

    }

    private void sendOtp(String phoneNumber, boolean isResend) {
        startResendTimer();
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeoutDuration, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(getApplicationContext(),"OTP VERIFICATION FAILED",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        resendToken = forceResendingToken;
                        Toast.makeText(getApplicationContext(),"OTP SENT SUCCESSFULLY",Toast.LENGTH_LONG).show();
                    }
                });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }


    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        // login and go to next activity
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Intent intent = new Intent(OTP.this,UserRegister.class);
                intent.putExtra("phone_number",phoneNumber);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"OTP VERIFICATION FAILED",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void startResendTimer() {
        timerText.setEnabled(false);
        new CountDownTimer(60000, 1000) { // 60 seconds, tick every 1 second
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                timeoutDuration = millisUntilFinished / 1000;
                timerText.setText("Resend OTP in " + timeoutDuration + " seconds");
            }

            @Override
            public void onFinish() {
                timeoutDuration = 60L;
                timerText.setEnabled(true);
                verifyButton.setVisibility(View.GONE);
            }
        }.start();
    }



}