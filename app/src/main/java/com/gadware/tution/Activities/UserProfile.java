package com.gadware.tution.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.security.crypto.EncryptedSharedPreferences;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gadware.tution.Activities.LoginActivity;
import com.gadware.tution.R;
import com.gadware.tution.asset.DocHelper;
import com.gadware.tution.asset.EncryptedSharedPrefManager;
import com.gadware.tution.asset.ImageHelper;
import com.gadware.tution.databinding.ActivityUserProfileBinding;
import com.gadware.tution.models.Batch;
import com.gadware.tution.models.ImageDetails;
import com.gadware.tution.viewmodel.BatchViewModel;
import com.gadware.tution.viewmodel.ImageViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV;
import static androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM;

public class UserProfile extends AppCompatActivity {

    ActivityUserProfileBinding binding;
    private AlertDialog alertDialog;
    private AlertDialog alertDialogx;
    private AlertDialog alert;
    String newValue = "";
    List<String> dl = new ArrayList<>();
    StorageReference Storageref, tuitionImages;
    DatabaseReference userInfoRef, tuitionRef, scheduleRef, sessionRef;
    String muserId;
    private ImageViewModel imageViewModel;
    private static final int PERMISSION_ALL = 222;
    private static final String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private static final int PICK_IMAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);

        muserId = FirebaseAuth.getInstance().getUid();
        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        //adView.setAdUnitId("ca-app-pub-7098600576446460/8504173760");
        adView.setAdUnitId("ca-app-pub-7098600576446460/8504173760");

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


        Storageref = FirebaseStorage.getInstance().getReference("Images").child(muserId + ".jpg");
        tuitionImages = FirebaseStorage.getInstance().getReference("Images");
        userInfoRef = FirebaseDatabase.getInstance().getReference("Users").child(muserId);
        sessionRef = FirebaseDatabase.getInstance().getReference("Session List").child(muserId);
        tuitionRef = FirebaseDatabase.getInstance().getReference("Tuition List").child(muserId);
        scheduleRef = FirebaseDatabase.getInstance().getReference("Schedule List").child(muserId);
        ShowDialog();
        RetrieveUserInfo();
        RetriveUserImage();
        binding.userImageIV.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(PERMISSIONS, PERMISSION_ALL);
                } else {
                    openGallery();
                }
            } else {
                openGallery();
            }


        });

        binding.inputName.setOnClickListener(v -> {
            GetValueAndUpdate("name", binding.inputName.getText().toString());
        });
        binding.inputEmail.setOnClickListener(v -> {
            Toast.makeText(this, "You can't change Email Address", Toast.LENGTH_SHORT).show();
        });
        binding.inputMobile.setOnClickListener(v -> {
            GetValueAndUpdate("mobile", binding.inputMobile.getText().toString());
        });
        binding.inputAddress.setOnClickListener(v -> {
            GetValueAndUpdate("address", binding.inputAddress.getText().toString());
        });


        binding.signoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();


            SharedPreferences sharedPreferences = null;
            try {
                sharedPreferences = EncryptedSharedPreferences.create(this, "mysecuredata.txt", DocHelper.getMKey(this), AES256_SIV, AES256_GCM);
            } catch (GeneralSecurityException | IOException e) {
                //Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("UserID");
            editor.apply();


            startActivity(new Intent(this, LoginActivity.class));
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });

        binding.deleteAccBtn.setOnClickListener(v -> {
            ShowDeleteDialog();
        });

        binding.favAddNewReportBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ReportActivity.class));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
    }

    private void DeleteAccount() {
        alert.dismiss();
        ShowDialog();
        getTuitionList();

        tuitionRef.removeValue().addOnSuccessListener(aVoid12 ->
                Storageref.delete().addOnSuccessListener(aVoid ->
                        userInfoRef.removeValue().addOnSuccessListener(aVoid1 -> {
                                    DeleteAuth();

                                }

                        )
                )
        );

    }

    private void getTuitionList() {


        tuitionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot k : snapshot.getChildren()) {
                    String key = k.getKey();
                    dl.add(key);
                }
                binding.inputName.setText(String.valueOf(dl.size()));
                for (String ky : dl) {
                    tuitionImages.child(ky + ".jpg").delete().addOnSuccessListener(aVoid ->
                    {
                        sessionRef.child(ky).removeValue().addOnSuccessListener(aVoid1 -> {
                                    scheduleRef.child(ky).removeValue().addOnSuccessListener(aVoid2 -> {
                                        Toast.makeText(UserProfile.this, "Deleting", Toast.LENGTH_SHORT).show();
                                    });
                                }
                        );
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void DeleteAuth() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(this, "mysecuredata.txt", DocHelper.getMKey(this), AES256_SIV, AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            alertDialog.dismiss();
            //Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("UserID");
        editor.apply();


        user.delete().addOnSuccessListener(aVoid -> {
            FirebaseAuth.getInstance().signOut();
            alertDialog.dismiss();
            Toast.makeText(this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ProfileSetupActivity.class));
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        }).addOnFailureListener(e -> {
            alertDialog.dismiss();
            //binding.inputName.setText(e.getLocalizedMessage());
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            GetPassword();
            while (true) {
                if (newValue.isEmpty() || newValue.length() < 6) {
                    GetPassword();
                } else
                    break;
            }
            assert email != null;
            AuthCredential credential = EmailAuthProvider
                    .getCredential(email, newValue);


            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        DeleteAuth();
                    });

            new Handler(Looper.getMainLooper()).postDelayed(this::DeleteAuth, 2000);
        });

    }


    private void ShowDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this, R.style.AppTheme_Dark_Dialog);
        builder.setMessage("Really Want to Delete Account ?\nAll of your Data will be removed !\nSelect 'Yes' if you are sure.")
                .setPositiveButton("Yes", (dialog, id) -> {
                    DeleteAccount();

                })
                .setNegativeButton("No", (dialog, id) -> {
                    alert.dismiss();

                });

        alert = builder.create();
        alert.setTitle("Permanently Delete Account");
        alert.show();
    }

    private void RetriveUserImage() {
        final long ONE_MEGABYTE = 1024 * 1024;

        try {
            Storageref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                byte[] bytes1 = bytes;
                Bitmap bitmap = ImageHelper.toBitmap(bytes1);
                binding.userImageIV.setImageBitmap(bitmap);
            }).addOnFailureListener(exception -> {
                Toast.makeText(this, "No Image", Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {

        }


    }

    private void RetrieveUserInfo() {
        userInfoRef.child("UserInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alertDialog.dismiss();

                if (snapshot.hasChild("name")) {
                    binding.inputName.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                }
                if (snapshot.hasChild("mobile")) {
                    binding.inputMobile.setText(Objects.requireNonNull(snapshot.child("mobile").getValue()).toString());
                }
                if (snapshot.hasChild("email")) {
                    binding.inputEmail.setText(Objects.requireNonNull(snapshot.child("email").getValue()).toString());
                }
                if (snapshot.hasChild("address")) {
                    binding.inputAddress.setText(Objects.requireNonNull(snapshot.child("address").getValue()).toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                alertDialog.dismiss();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "permission Denied...!!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void openGallery() {

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            //imageUri = data.getData();

            Bitmap bitmap = ImageHelper.getImageFromResult(this, RESULT_OK, data);

            Bitmap bitmapx = ImageHelper.generateThumb(bitmap, 25000);
            binding.userImageIV.setImageBitmap(bitmapx);

            alertDialog.show();
            byte[] bytes = ImageHelper.toByteArray(bitmapx);
            UploadImage(bytes);
        }
    }

    private void UploadImage(byte[] imageUri) {

        final StorageReference ref = Storageref;
        UploadTask uploadTask = ref.putBytes(imageUri);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UpdateLocalDB(imageUri);
                //Uri downloadUri = task.getResult();
                //assert downloadUri != null;
                //alertDialog.dismiss();
            }
        }).addOnFailureListener(e -> {
            alertDialog.dismiss();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });


    }

    private void ShowDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserProfile.this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = UserProfile.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_bar_dialog, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    private void GetPassword() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_value, null);
        dialogBuilder.setView(dialogView);

        TextView tv = dialogView.findViewById(R.id.title);
        EditText editText = dialogView.findViewById(R.id.dialog_input);
        Button cancelBtn = dialogView.findViewById(R.id.dialog_cancel_btn);
        Button updateBtn = dialogView.findViewById(R.id.dialog_update_btn);
        updateBtn.setText("Confirm");
        tv.setText("Enter Password");
        cancelBtn.setOnClickListener(v ->
                alertDialogx.dismiss()
        );

        updateBtn.setOnClickListener(v -> {
            newValue = editText.getText().toString();
            if (newValue.equals("") || newValue.isEmpty() || newValue.length() < 6) {
                editText.setError("Password Length min 6");

            } else {
                alertDialogx.dismiss();
            }
        });

        alertDialogx = dialogBuilder.create();
        alertDialogx.show();

    }

    private void GetValueAndUpdate(String key, String value) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_value, null);
        dialogBuilder.setView(dialogView);

        EditText editText = dialogView.findViewById(R.id.dialog_input);
        Button cancelBtn = dialogView.findViewById(R.id.dialog_cancel_btn);
        Button updateBtn = dialogView.findViewById(R.id.dialog_update_btn);
        editText.setText(value);

        cancelBtn.setOnClickListener(v ->
                alertDialogx.dismiss()
        );

        updateBtn.setOnClickListener(v -> {
            String newValue = editText.getText().toString();
            if (newValue.equals(value)) {
                alertDialogx.dismiss();
            } else if (newValue.length() < 4) {
                editText.setError("Minimum length 4");
            } else {
                userInfoRef.child("UserInfo").child(key).setValue(newValue).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Updated " + key, Toast.LENGTH_SHORT).show();
                    alertDialogx.dismiss();
                }).addOnFailureListener(e -> {
                            Toast.makeText(UserProfile.this, "Couldn't update " + key, Toast.LENGTH_SHORT).show();
                            alertDialogx.dismiss();
                        }

                );

            }
        });
        alertDialogx = dialogBuilder.create();
        alertDialogx.show();
    }

    private void UpdateLocalDB(byte[] imageBytes) {
        Completable.fromAction(() ->
                imageViewModel.insertSingleImage(new ImageDetails(muserId, "user", imageBytes))).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        alertDialog.dismiss();
                        Toast.makeText(UserProfile.this, "Successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        alert.dismiss();
                        Toast.makeText(UserProfile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}