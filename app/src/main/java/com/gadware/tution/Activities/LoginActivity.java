package com.gadware.tution.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.gadware.tution.R;
import com.gadware.tution.asset.EncryptedSharedPrefManager;
import com.gadware.tution.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;


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

        mAuth = FirebaseAuth.getInstance();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        binding.btnNewAcc.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileSetupActivity.class));
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        });


        binding.btnLogin.setOnClickListener(v -> {
            if (validate() == 1) {
                SignInUser();
            }
        });

        binding.layResetPass.setOnClickListener(v -> {
            ResetEmailPassword();
        });

    }

    private void SignInUser() {
        Showialog();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        alertDialog.dismiss();
                        try {
                            EncryptedSharedPrefManager.getInstance(this).saveData("UserID",mAuth.getUid());
                        } catch (GeneralSecurityException | IOException e) {
                            e.printStackTrace();
                        }

                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
//                    else if (!task.isSuccessful()){
//                        alertDialog.dismiss();
//
//                        try {
//                            throw task.getException();
//                        } catch (FirebaseAuthInvalidCredentialsException error) {
//                            binding.etPassword.setError("Password wrong");
//                            binding.etPassword.requestFocus();
//
//                            Toast.makeText(this, "Error  "+error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                        } catch (Exception ex) {
//                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
//                        }
//                    }
                }).addOnFailureListener(e -> {
            alertDialog.dismiss();

            switch (Objects.requireNonNull(e.getMessage())) {
                case "There is no user record corresponding to this identifier. The user may have been deleted.":
                    binding.etEmail.setError("Invalid email");
                    Toast.makeText(this, "Invalid Email,Create Account instead", Toast.LENGTH_SHORT).show();
                    binding.etEmail.requestFocus();
                    break;
                case "The password is invalid or the user does not have a password.":
                    binding.etPassword.setError("Password wrong");
                    Toast.makeText(this, "Wrong password, reset if forgotten", Toast.LENGTH_SHORT).show();
                    binding.etPassword.requestFocus();
                    binding.layResetPass.setVisibility(View.VISIBLE);
                    break;
                case "A network error (such as timeout, interrupted connection or unreachable host) has occurred.":
                    Toast.makeText(this, "Error, Check Internet Connection", Toast.LENGTH_SHORT).show();
                    break;
                // ...
                default:
                    Toast.makeText(this, "Unknown Error Occurred, try again", Toast.LENGTH_SHORT).show();
            }

//            if (e.getLocalizedMessage().equals("The password is invalid or the user does not have a password.")) {
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                //show forgot password
//            } else {
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }

        });
    }


    public int validate() {
        int valid = 1;

        email = binding.etEmail.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("enter a valid email address");
            valid = 0;
        } else {
            binding.etEmail.setError(null);
        }
        pass = binding.etPassword.getText().toString();
        if (pass.isEmpty() || pass.length() < 6) {
            binding.etPassword.setError("Min 6 Alphanumeric");
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

    private void ResetEmailPassword() {

        String emailAddress = binding.etEmail.getText().toString();
        if (emailAddress.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            binding.etEmail.setError("Enter a Valid Email Address");
        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress).addOnSuccessListener(aVoid -> {
                binding.etPassword.setText("");
                binding.etPassword.requestFocus();
                Toast.makeText(this, "Check Email, Link Sent", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error ", Toast.LENGTH_SHORT).show();
            });
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this,ProfileSetupActivity.class));
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}