package com.jeysidg.e_notify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

public class CreateAccountActivity extends AppCompatActivity {
    EditText mNameEt, mEmailEt, mPasswordEt;
    Button mRegisterBtn;
    ImageView mBackBtn;

    AutoCompleteTextView companyDd, departmentDd;

    TextView mHaveAccountTv;
    ProgressDialog progressDialog;

    //declare instance of firebase auth
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_create_account);

        //lists of choices
        String[] companyChoices = {"SmoothMoves, Inc.", "BrandBoss", "MyRecruiter", "Aickman & Greene", "MoneyFrog", "BeanBag"};
        String[] departmentChoices = {"Recruitment", "Operations", "Sales", "HR Operations", "Admin",  "Corporate Support Services", "Finance", "Executive Office", "Creatives"};

        // Initialize the AutoCompleteTextViews
        companyDd = findViewById(R.id.dropdown_field);
        departmentDd = findViewById(R.id.dropdown_field2);

        //ArrayAdapter for the company AutoCompleteTextView
        ArrayAdapter<String> companyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, companyChoices);

        //ArrayAdapter for the department AutoCompleteTextView
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, departmentChoices);

        // Set the adapters to the AutoCompleteTextViews
        companyDd.setAdapter(companyAdapter);
        departmentDd.setAdapter(departmentAdapter);

        //init views
        mNameEt = findViewById(R.id.nameEt);
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mRegisterBtn = findViewById(R.id.register_btn);
        mHaveAccountTv = findViewById(R.id.have_accountTv);
        mBackBtn = findViewById(R.id.backbtn);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Account for User...");

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //input name, email, pass
                String name = mNameEt.getText().toString().trim();
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();
                String company = companyDd.getText().toString().trim();
                String department = departmentDd.getText().toString().trim();
                
                //validate
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                    //set error and focus to email txt

                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                }
                else if (password.length()<6){
                    //set error and focus to pass txt
                    mPasswordEt.setError("Password length must at least 6 characters");
                    mPasswordEt.setFocusable(true);
                } else if (name.isEmpty()) {
                    mNameEt.setError("Name is required");
                    mNameEt.setFocusable(true);
                } else if (company.isEmpty()) {
                    companyDd.setError("Company is required");
                    companyDd.setFocusable(true);
                } else if (department.isEmpty()) {
                    departmentDd.setError("Department is required");
                    departmentDd.setFocusable(true);
                } else {
                    registerUser(email, password); // register the user
                }

            }
        });


        mHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
                finish();
            }
        });

        //handle back btn click
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //back on prev
                onBackPressed();

            }
        });
    }

    private void registerUser(String email, String password) {

        // email and pass is valid, show progress dialog and register users
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            //get user email from auth
                            String email = user.getEmail();
                            String uid = user.getUid();
                            String name = mNameEt.getText().toString().trim();
                            String company = companyDd.getText().toString().trim();
                            String department = departmentDd.getText().toString().trim();
                            //store user info in firebase database when user is registered
                            //using hashmap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //PUT INFO IN HASHMAP
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", name); //will add later(edit profile
                            //hashMap.put("phone", ""); //will add later(edit profile
                            hashMap.put("image", ""); //will add later(edit profile
                            hashMap.put("company", company); //will add later(edit profile
                            hashMap.put("department", department); //will add later(edit profile

                            //firebase database instance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //path to store user data named "Users"
                            DatabaseReference reference = database.getReference("Users");
                            //put data within hashmap in database
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(CreateAccountActivity.this, "Registered...\n"+user.getEmail(), Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(CreateAccountActivity.this, DashboardActivity.class));
                            finish();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(CreateAccountActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();
                        Toast.makeText(CreateAccountActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }


}
