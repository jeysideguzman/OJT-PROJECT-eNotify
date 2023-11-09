package com.jeysidg.e_notify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditprofileActivity extends AppCompatActivity {

    Button saveBtn;
    ImageView backBtn;
    AutoCompleteTextView companyDd, departmentDd;
    EditText mNameEt;

    ProgressDialog progressDialog;

    //declare instance of firebase auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);


        //lists of choices
        String[] companyChoices = {"SmoothMoves, Inc.", "BrandBoss", "MyRecruiter", "Aickman & Greene", "MoneyFrog", "BeanBag"};
        String[] departmentChoices = {"Recruitment", "Operations", "Sales", "HR Operations", "Admin",  "Corporate Support Services", "Finance", "Executive Office", "Creatives"};
        // Initialize the AutoCompleteTextViews
        companyDd = findViewById(R.id.dropdown_company);
        departmentDd = findViewById(R.id.dropdown_department);

        //ArrayAdapter for the company AutoCompleteTextView
        ArrayAdapter<String> companyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, companyChoices);

        //ArrayAdapter for the department AutoCompleteTextView
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, departmentChoices);

        // Set the adapters to the AutoCompleteTextViews
        companyDd.setAdapter(companyAdapter);
        departmentDd.setAdapter(departmentAdapter);

        //init views
        mNameEt = findViewById(R.id.nameEt);
        //mPhoneEt = findViewById(R.id.phoneEt);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backbtn);




        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");

        //handle save btn
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUpdatedDetails();
            }
        });

        //handle back btn click
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //back to profile fragment
                onBackPressed();
            }
        });
    }

    private void saveUpdatedDetails() {
        progressDialog.show();

        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        String name = mNameEt.getText().toString().trim();
        String company = companyDd.getText().toString().trim();
        String department = departmentDd.getText().toString().trim();

        HashMap<String, Object> hashMap = new HashMap<>();

        // Check if fields are not empty before updating the database
        if (!name.isEmpty()) {
            hashMap.put("name", name);
        }
        if (!company.isEmpty()) {
            hashMap.put("company", company);
        }
        if (!department.isEmpty()) {
            hashMap.put("department", department);
        }

        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        profileRef.updateChildren(hashMap)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(EditprofileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(EditprofileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserStatus(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){


        } else {

            startActivity(new Intent(EditprofileActivity.this, SplashActivity.class));
            finish();

        }
    }


    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    //inflate options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_search).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    //handle item menu clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout){
            mAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}



