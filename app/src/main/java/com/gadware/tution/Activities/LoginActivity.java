package com.gadware.tution.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.gadware.tution.R;
import com.gadware.tution.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private String email, pass;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        binding.btnLogin.setActivated(true);


        binding.btnNewAcc.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileSetupActivity.class));
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        });


        binding.btnLogin.setOnClickListener(v -> {
            if (validate()==1){
                SignInUser();
            }
        });

    }

    private void SignInUser() {

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    public int validate() {
        int valid = 1;

        email=binding.etEmail.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("enter a valid email address");
            valid = 0;
        } else {
            binding.etEmail.setError(null);
        }
        pass=binding.etPassword.getText().toString();
        if (pass.isEmpty() || pass.length() < 4) {
            binding.etPassword.setError("More Than4 Alphanumeric Characters");
            valid = 0;
        } else {
            binding.etPassword.setError(null);
        }

        return valid;
    }

}