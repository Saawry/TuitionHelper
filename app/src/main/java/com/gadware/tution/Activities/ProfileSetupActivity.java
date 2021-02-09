package com.gadware.tution.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.gadware.tution.R;
import com.gadware.tution.models.User;
import com.gadware.tution.databinding.ActivityProfileSetupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        mAuth=FirebaseAuth.getInstance();

        email = binding.inputEmail.getText().toString();
        password = binding.inputPassword.getText().toString();
        rePass = binding.inputReEnterPassword.getText().toString();
        mobile = binding.inputMobile.getText().toString();
        address = binding.inputAddress.getText().toString();
        name = binding.inputName.getText().toString();

        binding.btnSignup.setOnClickListener(v->{
            if (validate()==1){
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
                        User user = new User(uid, email, password, address, mobile, name);
                        userRef = FirebaseDatabase.getInstance().getReference("Users").getRef();
                        userRef.child(uid).child("UserInfo").setValue(user).addOnSuccessListener(aVoid -> {
                            alertDialog.dismiss();
                            Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ProfileSetupActivity.this, MainActivity.class));
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }).addOnFailureListener(e -> {
                    alertDialog.dismiss();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

        if (name.isEmpty() || name.length()<3) {
            binding.inputName.setError("Enter Proper Name");
            return 0;
        } else {
            binding.inputName.setError(null);
        }
        if (mobile.isEmpty() || mobile.length()<10 || !android.util.Patterns.PHONE.matcher(mobile).matches()) {
            binding.inputMobile.setError("Enter Valid Address");
            return 0;
        } else {
            binding.inputMobile.setError(null);
        }

        if (address.isEmpty() || address.length()<5) {
            binding.inputAddress.setError("Enter Proper Address");
            return 0;
        } else {
            binding.inputAddress.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.setError("Enter a Valid Email Address");
            return 0;
        } else {
            binding.inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            binding.inputPassword.setError("More than 4 Alphanumeric Characters");
            return  0;
        } else {
            binding.inputPassword.setError(null);
        }

        if (!rePass.equals(password)) {
            binding.inputReEnterPassword.setError("Password Mismatch");
            return  0;
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
}