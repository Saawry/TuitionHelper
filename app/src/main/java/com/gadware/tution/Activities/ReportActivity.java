package com.gadware.tution.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gadware.tution.R;
import com.gadware.tution.databinding.ActivityReportBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReportActivity extends AppCompatActivity {
    ActivityReportBinding binding;
    String email;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report);

        mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail();


        binding.btnFeedback.setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"gadware.tuition@gmail.com"});
            email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            email.putExtra(Intent.EXTRA_TEXT, "Hello Gadware,\nI am (name) and User ID is" + email + ", sending you a feedback described bellow.");

            //need this to prompts email client only
            email.setType("message/rfc822");

            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}