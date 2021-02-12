package com.gadware.tution.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gadware.tution.R;
import com.gadware.tution.asset.ImageHelper;
import com.gadware.tution.databinding.ActivityMainBinding;
import com.gadware.tution.databinding.TuitionCardBinding;
import com.gadware.tution.models.TuitionInfo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AlertDialog alertDialog;

    private FirebaseDatabase firedb;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference tuitionRef;
    private String status = "", mUserId;
    StorageReference Storageref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Storageref = FirebaseStorage.getInstance().getReference("Images");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firedb = FirebaseDatabase.getInstance();
        mUserId = mUser.getUid();
        VerifyUserExistence();
        Showialog();
        RetriveImage();
        binding.tuitionRecycler.setLayoutManager(new LinearLayoutManager(this));


        binding.ProfileIcon.setOnClickListener(v -> {

                    startActivity(new Intent(MainActivity.this, UserProfile.class));
                }
        );

        binding.favAddNewTuitionBtn.setOnClickListener(v ->

                startActivity(new Intent(MainActivity.this, AddNewTuition.class))
        );


        tuitionRef = FirebaseDatabase.getInstance().getReference().child("Tuition List").child(mUserId);
        tuitionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    RetriveTuitionInfoList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        //adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

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






    }

    private void Showialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_bar_dialog, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            alertDialog.dismiss();
        }, 3500);
    }

    private void RetriveTuitionInfoList() {
        FirebaseRecyclerOptions<TuitionInfo> options = new FirebaseRecyclerOptions.Builder<TuitionInfo>().setQuery(tuitionRef, TuitionInfo.class).build();

        final FirebaseRecyclerAdapter<TuitionInfo, TuitionssViewHolder> adapter
                = new FirebaseRecyclerAdapter<TuitionInfo, TuitionssViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TuitionssViewHolder holder, int position, @NonNull TuitionInfo model) {

                final String TuitionIDs = getRef(position).getKey();
                tuitionRef.child(TuitionIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            binding.noDataLayout.setVisibility(View.GONE);
                            alertDialog.dismiss();
                            RetriveImage(TuitionIDs, holder.tBinding.ProfileIcon);
                            String tDAys = dataSnapshot.child("totalDays").getValue().toString();
                            String cDAys = dataSnapshot.child("completedDays").getValue().toString();
                            String wDAys = dataSnapshot.child("weeklyDays").getValue().toString();

                            holder.tBinding.tuitionCardTitle.setText(dataSnapshot.child("studentName").getValue().toString());
                            holder.tBinding.tuitionCardLocation.setText(dataSnapshot.child("location").getValue().toString());
                            holder.tBinding.tuitionCardDays.setText("Done " + cDAys + " of " + tDAys + " days");
                            holder.tBinding.tuitionCardWeekly.setText("Weekly " + wDAys + " days");

                            holder.itemView.setOnClickListener(v -> {
                                Intent tdIntent = new Intent(MainActivity.this, TuitionDetails.class);
                                tdIntent.putExtra("Tuition_id", TuitionIDs);
                                startActivity(tdIntent);
                            });
                            holder.tBinding.addNewSessionIcon.setOnClickListener(v -> {
                                Intent tdIntent = new Intent(MainActivity.this, AddNewSession.class);
                                tdIntent.putExtra("Tuition_id", TuitionIDs);
                                tdIntent.putExtra("completedDays", cDAys);
                                startActivity(tdIntent);
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        alertDialog.dismiss();
                    }
                });
            }

            @NonNull
            @Override
            public TuitionssViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                TuitionCardBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.tuition_card, parent, false);
                TuitionssViewHolder viewHolder = new TuitionssViewHolder(binding);
                return viewHolder;

            }
        };

        binding.tuitionRecycler.setAdapter(adapter);
        adapter.startListening();
    }


    private void VerifyUserExistence() {
        if (mUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {

            String activeUserId = mUser.getUid();
            try {
                DatabaseReference ref = firedb.getReference().child("Users").child(activeUserId).child("UserInfo").child("status");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            status = Objects.requireNonNull(snapshot.getValue()).toString();
                            if (status.equals("noImage")) {
                                Toast.makeText(MainActivity.this, "No Profile Image", Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(MainActivity.this, UserProfile.class));
//                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
    }

    public static class TuitionssViewHolder extends RecyclerView.ViewHolder {

        TuitionCardBinding tBinding;

        public TuitionssViewHolder(@NonNull TuitionCardBinding tBinding) {
            super(tBinding.getRoot());
            this.tBinding = tBinding;

        }
    }


    private void RetriveImage(String tuitionId, ImageView view) {
        final long ONE_MEGABYTE = 1024 * 1024;
        Storageref.child(tuitionId + ".jpg").getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            byte[] bytes1 = bytes;
            Bitmap bitmapx = ImageHelper.toBitmap(bytes1);
            view.setImageBitmap(bitmapx);
        }).addOnFailureListener(exception -> {
        });

    }
    private void RetriveImage() {
        final long ONE_MEGABYTE = 1024 * 1024;
        Storageref.child(mUserId+".jpg").getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            byte[] bytes1 = bytes;
            Bitmap bitmap = ImageHelper.toBitmap(bytes1);
            binding.ProfileIcon.setImageBitmap(bitmap);
        }).addOnFailureListener(exception -> {
        });

    }
}