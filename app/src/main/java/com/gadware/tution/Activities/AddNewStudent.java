package com.gadware.tution.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.gadware.tution.R;
import com.gadware.tution.databinding.ActivityAddNewStudentBinding;
import com.gadware.tution.models.Batch;
import com.gadware.tution.models.StudentInfo;
import com.gadware.tution.models.StudentList;
import com.gadware.tution.viewmodel.BatchViewModel;
import com.gadware.tution.viewmodel.StudentListViewModel;
import com.gadware.tution.viewmodel.StudentViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddNewStudent extends AppCompatActivity {

    ActivityAddNewStudentBinding binding;
    private DatabaseReference StudentInfoRef,batchStudentList;
    private String batchId;
    private String id, name, className, institute, fatherName, address, phone,email,mUserId;
    private AlertDialog alertDialog;
    private long StudentCounter;
    private StudentViewModel studentViewModel;
    private StudentListViewModel studentListViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_student);

        binding= DataBindingUtil.setContentView(this,R.layout.activity_add_new_student);

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);
        studentListViewModel = new ViewModelProvider(this).get(StudentListViewModel.class);

        final View activityRootView = findViewById(R.id.activity_root_view);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
            if (heightDiff > dpToPx(AddNewStudent.this, 100)) {
                binding.adView.setVisibility(View.INVISIBLE);
            } else {
                binding.adView.setVisibility(View.VISIBLE);
            } });

        batchId=getIntent().getStringExtra("Batch_Id");

        StudentInfoRef = FirebaseDatabase.getInstance().getReference().child("StudentInfo List");
        batchStudentList = FirebaseDatabase.getInstance().getReference().child("BatchStudent List").child(batchId);
        id = StudentInfoRef.push().getKey();
        mUserId=FirebaseAuth.getInstance().getUid();


        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        //adView.setAdUnitId("ca-app-pub-7098600576446460/2003905092");
        adView.setAdUnitId("ca-app-pub-7098600576446460/2003905092");

        MobileAds.initialize(this, initializationStatus -> {

        });

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });


        GetStudentCounter();


        binding.addStudentBtnId.setOnClickListener(v -> {
            if ( StudentCounter>= 20) {
                Toast.makeText(AddNewStudent.this, "Reached Maximum(20), delete old student or contact us", Toast.LENGTH_LONG).show();
            } else if (validate()==1){
                InitNewStudent();
            }
        });

    }

    private void GetStudentCounter() {
        StudentInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                StudentCounter = dataSnapshot.getChildrenCount();

                if (StudentCounter >= 20) {
                    Toast.makeText(AddNewStudent.this, "Reached Maximum limit(20), delete old student or contact us", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    public int validate() {

        name = binding.inputName.getText().toString();
        if (name.isEmpty() || name.length() < 4) {
            binding.inputName.setError("min length 4");
            return 0;
        } else {
            binding.inputName.setError(null);
        }

        className = binding.inputClass.getText().toString();
        if (className.isEmpty() || className.length() < 3) {
            binding.inputClass.setError("invalid(ex: nine)");
            return 0;
        } else {
            binding.inputClass.setError(null);
        }


        institute = binding.inputInstitute.getText().toString();
        if (institute.isEmpty() || institute.length() < 5) {
            binding.inputInstitute.setError("min length 5");
            return 0;
        } else {
            binding.inputInstitute.setError(null);
        }

        fatherName = binding.inputFatherName.getText().toString();
        if (fatherName.isEmpty() || fatherName.length()<4 ) {
            binding.inputFatherName.setError("min length 4");
            return 0;
        } else {
            binding.inputFatherName.setError(null);
        }

        address = binding.inputAddress.getText().toString();
        if (address.isEmpty() || address.length() < 4) {
            binding.inputAddress.setError("min length 4");
            return 0;
        } else {
            binding.inputAddress.setError(null);
        }


        phone = binding.inputMobile.getText().toString();
        if (phone.isEmpty() || !android.util.Patterns.PHONE.matcher(phone).matches()) {
            binding.inputMobile.setError("invalid phone");
            return 0;
        } else {
            binding.inputMobile.setError(null);
        }

        email = binding.inputEmail.getText().toString();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.setError("invalid email");
            return 0;
        } else {
            binding.inputEmail.setError(null);
        }

        return 1;
    }
    private void InitNewStudent() {
        ShowLoadingDialog();

        StudentInfo studentInfo = new StudentInfo(id,name,className,institute,fatherName,address,phone,email,mUserId);
        assert id != null;
        StudentInfoRef.child(id).setValue(studentInfo).addOnSuccessListener(aVoid -> {

            UpdateLocalDB(studentInfo);

        }).addOnFailureListener(e ->{
            alertDialog.dismiss();
            Toast.makeText(AddNewStudent.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private void UpdateLocalDB(StudentInfo studentInfo) {
        Completable.fromAction(() ->
                studentViewModel.insertStudentInfo(studentInfo)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        AddStudentToBatch(id);
                    }

                    @Override
                    public void onError(Throwable e) {
                        alertDialog.dismiss();
                        Toast.makeText(AddNewStudent.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void AddStudentToBatch(String id) {
        batchStudentList.child(id).setValue("saved").addOnSuccessListener(aVoid -> {
            AddSTDListLocal(id);
        }).addOnFailureListener(e -> {
            alertDialog.dismiss();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void AddSTDListLocal(String id) {
        Completable.fromAction(() ->
                studentListViewModel.insertSingleStudent(new StudentList(batchId,id))).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onComplete() {
                        alertDialog.dismiss();
                        Toast.makeText(AddNewStudent.this, "Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddNewStudent.this, BatchDetails.class);
                        intent.putExtra("Batch_id", batchId);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                    }

                    @Override
                    public void onError(Throwable e) {
                        alertDialog.dismiss();
                        Toast.makeText(AddNewStudent.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void ShowLoadingDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewStudent.this);
        LayoutInflater inflater = AddNewStudent.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_bar_dialog, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
}