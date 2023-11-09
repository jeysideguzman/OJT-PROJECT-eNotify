package com.jeysidg.e_notify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ImageView avatarIv, backBtn;
    TextView nameTv, emailTv, companyTv, departmentTv;
    String uid;
    Button notifHistoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);


        //init views
        backBtn = findViewById(R.id.backbtn);
        avatarIv = findViewById(R.id.avatarIv);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        companyTv = findViewById(R.id.companyTv);
        departmentTv = findViewById(R.id.departmentTv);
        notifHistoryBtn = findViewById(R.id.notifHistoryBtn);



        firebaseAuth = FirebaseAuth.getInstance();

        //get uid clicked user to retrieve his posts
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        notifHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ViewProfileActivity.this, "You can only view your own notification history.", Toast.LENGTH_SHORT).show();
            }
        });

        //..//
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener()    {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //check until required data
                for (DataSnapshot ds : snapshot.getChildren()){

                    //get data
                    String name = ""+ds.child("name").getValue();
                    String email = ""+ds.child("email").getValue();
                    String image = ""+ds.child("image").getValue();
                    String company = ""+ds.child("company").getValue();
                    String department = ""+ds.child("department").getValue();

                    //set data
                    nameTv.setText(name);
                    emailTv.setText(email);
                    //phoneTv.setText(phone);
                    companyTv.setText(company);
                    departmentTv.setText(department);


                    try {
                        //image received/set
                        Picasso.get().load(image).into(avatarIv);
                    }
                    catch (Exception e) {
                        //any exception while getting image
                        Picasso.get().load(R.drawable.profpic).into(avatarIv);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        checkUserStatus();


    }
    private void checkUserStatus(){

        //get user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user stay signed in

        } else {
            startActivity(new Intent(ViewProfileActivity.this, SplashActivity.class));
            finish();

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_search).setVisible(false); // hide search btn

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}