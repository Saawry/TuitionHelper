package com.gadware.tution.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gadware.tution.R;
import com.gadware.tution.databinding.ActivityMainBinding;
import com.gadware.tution.databinding.TuitionCardBinding;
import com.gadware.tution.models.TuitionInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private AlertDialog alertDialog;

    private FirebaseDatabase firedb;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference tuitionRef;
    private String status = "", mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firedb = FirebaseDatabase.getInstance();
        mUserId = mUser.getUid();
        VerifyUserExistence();
        Showialog();
        binding.tuitionRecycler.setLayoutManager(new LinearLayoutManager(this));


        binding.ProfileIcon.setOnClickListener(v -> {

                    startActivity(new Intent(MainActivity.this, UserProfile.class));
                }
        );

        binding.favAddNewTuitionBtn.setOnClickListener(v ->

                startActivity(new Intent(MainActivity.this, AddNewTuition.class))
        );


        tuitionRef = FirebaseDatabase.getInstance().getReference().child("Tuition List").child(mUserId);
        //tuitionInfoRef=FirebaseDatabase.getInstance().getReference().child("Tuition List").child(mUserId);
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
    }

    private void RetriveTuitionInfoList() {
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<TuitionInfo>().setQuery(tuitionRef, TuitionInfo.class).build();

        final FirebaseRecyclerAdapter<TuitionInfo, TuitionssViewHolder> adapter
                = new FirebaseRecyclerAdapter<TuitionInfo, TuitionssViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TuitionssViewHolder holder, int position, @NonNull TuitionInfo model) {

                final String TuitionIDs = getRef(position).getKey();
                final String[] TuitonImage = {"default_image"};
                tuitionRef.child(TuitionIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        alertDialog.dismiss();
                        if (dataSnapshot.hasChild("ImageUri")) {
                            Glide.with(MainActivity.this).load(dataSnapshot.child("ImageUri").getValue().toString()).into(holder.tBinding.ProfileIcon);
                        }

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
                //return new SessionsViewHolder.ViewHolder(binding);
//                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tuition_card, viewGroup,false);
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
}