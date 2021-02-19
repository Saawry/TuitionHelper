package com.gadware.tution.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.security.crypto.EncryptedSharedPreferences;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.gadware.tution.R;
import com.gadware.tution.asset.DocHelper;
import com.gadware.tution.asset.EncryptedSharedPrefManager;
import com.gadware.tution.models.User;
import com.gadware.tution.databinding.ActivityProfileSetupBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV;
import static androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM;

public class ProfileSetupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String email, password, rePass, address, mobile, uid, name;
    private ActivityProfileSetupBinding binding;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_setup);

        mAuth = FirebaseAuth.getInstance();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);



        email = binding.inputEmail.getText().toString();
        password = binding.inputPassword.getText().toString();
        rePass = binding.inputReEnterPassword.getText().toString();
        mobile = binding.inputMobile.getText().toString();
        address = binding.inputAddress.getText().toString();
        name = binding.inputName.getText().toString();

        binding.btnSignup.setOnClickListener(v -> {
            if (validate() == 1) {
                SignInUser();
            }
        });
        binding.linkLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });

    }


    private void SignInUser() {
        Showialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser mmuser = mAuth.getCurrentUser();
                        assert mmuser != null;
                        uid = mmuser.getUid();
                        User user = new User(uid, email, address, mobile, name);
                        userRef = FirebaseDatabase.getInstance().getReference("Users").getRef();
                        userRef.child(uid).child("UserInfo").setValue(user).addOnSuccessListener(aVoid -> {
                            alertDialog.dismiss();


                            SharedPreferences sharedPreferences = null;
                            try {
                                sharedPreferences = EncryptedSharedPreferences.create(this, "mysecuredata.txt", DocHelper.getMKey(this), AES256_SIV, AES256_GCM);
                            } catch (GeneralSecurityException | IOException e) {
                                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("UserID", uid);
                            editor.apply();



                            Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ProfileSetupActivity.this, MainActivity.class));
                            finish();
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        alertDialog.dismiss();
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException error_weak_password) {
                            Toast.makeText(this, error_weak_password.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            binding.inputPassword.setError("weak Password");
                            binding.inputPassword.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException error_invalid_email) {
                            Toast.makeText(this, error_invalid_email.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            binding.inputEmail.setError("invalid Email");
                            binding.inputEmail.requestFocus();
                        } catch (FirebaseAuthUserCollisionException error_user_exists) {
                            Toast.makeText(this, "Login instead, or Reset password", Toast.LENGTH_LONG).show();
                            binding.inputEmail.setError("email already used");
                            binding.inputEmail.requestFocus();
                        } catch (Exception ex) {
                            Toast.makeText(this, "Error " + ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public int validate() {
        int valid = 1;
        email = binding.inputEmail.getText().toString();
        password = binding.inputPassword.getText().toString();
        rePass = binding.inputReEnterPassword.getText().toString();
        mobile = binding.inputMobile.getText().toString();
        address = binding.inputAddress.getText().toString();
        name = binding.inputName.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            binding.inputName.setError("Enter Proper Name");
            return 0;
        } else {
            binding.inputName.setError(null);
        }
        if (mobile.isEmpty() || mobile.length() < 10 || !android.util.Patterns.PHONE.matcher(mobile).matches()) {
            binding.inputMobile.setError("Enter Valid Number");
            return 0;
        } else {
            binding.inputMobile.setError(null);
        }

        if (address.isEmpty() || address.length() < 4) {
            binding.inputAddress.setError("Min Length 4");
            return 0;
        } else {
            binding.inputAddress.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.setError("Invalid Email");
            return 0;
        } else {
            binding.inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            binding.inputPassword.setError("Min 6 Alphanumeric");
            return 0;
        } else {
            binding.inputPassword.setError(null);
        }

        if (!rePass.equals(password)) {
            binding.inputReEnterPassword.setError("Password Mismatch");
            return 0;
        } else {
            binding.inputReEnterPassword.setError(null);
        }
        return valid;
    }

    private void Showialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProfileSetupActivity.this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = ProfileSetupActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_bar_dialog, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileSetupActivity.this,LoginActivity.class));
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}