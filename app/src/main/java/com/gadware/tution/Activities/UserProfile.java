package com.gadware.tution.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.gadware.tution.asset.ImageHelper;
import com.gadware.tution.databinding.ActivityUserProfileBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {

    ActivityUserProfileBinding binding;
    private AlertDialog alertDialog;
    private AlertDialog alertDialogx;
    StorageReference Storageref;
    DatabaseReference userInfoRef;
    String muserId;
    private static final int PERMISSION_ALL = 222;
    private static final String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);

        muserId = FirebaseAuth.getInstance().getUid();
        Storageref = FirebaseStorage.getInstance().getReference("Images").child(muserId + ".jpg");
        userInfoRef = FirebaseDatabase.getInstance().getReference("Users").child(muserId).child("UserInfo");
        Showialog();
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
            GetValueAndUpdate("email", binding.inputEmail.getText().toString());
        });
        binding.inputMobile.setOnClickListener(v -> {
            GetValueAndUpdate("mobile", binding.inputMobile.getText().toString());
        });
        binding.inputAddress.setOnClickListener(v -> {
            GetValueAndUpdate("address", binding.inputAddress.getText().toString());
        });


        binding.signoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
        });

    }

    private void RetriveUserImage() {
        final long ONE_MEGABYTE = 1024 * 1024;
        Storageref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            byte[] bytes1 = bytes;
            Bitmap bitmap = ImageHelper.toBitmap(bytes1);
            binding.userImageIV.setImageBitmap(bitmap);
        }).addOnFailureListener(exception -> {
            Toast.makeText(this, "No Image", Toast.LENGTH_SHORT).show();
        });

    }

    private void RetrieveUserInfo() {
        userInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alertDialog.dismiss();
//                if (snapshot.child("ImageUri").exists()) {
//                    Glide.with(UserProfile.this).load(Objects.requireNonNull(snapshot.child("ImageUri").getValue()).toString()).into(binding.userImageIV);
//                }

                binding.inputName.setText(snapshot.child("name").getValue().toString());
                binding.inputMobile.setText(snapshot.child("mobile").getValue().toString());
                binding.inputEmail.setText(snapshot.child("email").getValue().toString());
                binding.inputAddress.setText(snapshot.child("address").getValue().toString());
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

            Bitmap bitmap=ImageHelper.getImageFromResult(this,RESULT_OK,data);

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
                Uri downloadUri = task.getResult();
                assert downloadUri != null;
                alertDialog.dismiss();
            }
        }).addOnFailureListener(e ->
                alertDialog.dismiss()
        );


    }

    private void Showialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserProfile.this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = UserProfile.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_bar_dialog, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
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
                userInfoRef.child(key).setValue(newValue).addOnSuccessListener(aVoid -> {
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


}