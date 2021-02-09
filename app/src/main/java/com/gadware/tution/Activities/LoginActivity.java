package com.gadware.tution.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
    private AlertDialog alertDialog;


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
        Showialog();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        alertDialog.dismiss();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(e -> {
                    alertDialog.dismiss();
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
    private void Showialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = LoginActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_bar_dialog, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}