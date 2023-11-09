package com.jeysidg.e_notify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jeysidg.e_notify.notification.APIService;
import com.jeysidg.e_notify.notification.Client;
import com.jeysidg.e_notify.notification.Data;
import com.jeysidg.e_notify.notification.Response;
import com.jeysidg.e_notify.notification.Sender;
import com.jeysidg.e_notify.notification.Token;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;

public class VisitorformActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbRef;
    StorageReference storageReference;

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    //ARRAYS PERMISSION
    String cameraPermissions[];
    String storagePermissions[];

    String storagePath = "Users_ID_Imgs/";

    //uri of picked image
    Uri image_uri;
    //for checking profile
    String profileOrCoverPhoto;

    ProgressDialog pd;
    String idPicUrl;
    Button notifyBtn, captureBtn;
    ImageView idPicIv, backBtn;
    TextView timestampTextView, emailTv;
    EditText nameEt, contactPersonEt, purposeOfVisitEt;
    AutoCompleteTextView companyClientDd;
    String hisUid;
    //String hisImage;
    String myUid;
    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitorform);

        notifyBtn = findViewById(R.id.notifyBtn);
        idPicIv = findViewById(R.id.idPicIv);
        captureBtn = findViewById(R.id.captureBtn);
        nameEt = findViewById(R.id.vnameEt);
        contactPersonEt = findViewById(R.id.vContactPersonEt);
        companyClientDd = findViewById(R.id.dropdown_companyClient);
        purposeOfVisitEt = findViewById(R.id.vPurposeOfVisitEt);
        backBtn = findViewById(R.id.backbtn);
        timestampTextView = findViewById(R.id.timestampTextView);
        emailTv = findViewById(R.id.emailTv);

        //Api service
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        //get user to visit info
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");

        //init
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference("Users");

        Query userQuery = dbRef.orderByChild("uid").equalTo(hisUid);
        //get user pic and name
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String name = ""+ ds.child("name").getValue();
                    String email = ""+ ds.child("email").getValue();
                    hisUid = ""+ ds.child("uid").getValue();

                    // set data
                    contactPersonEt.setText(name);
                    emailTv.setText(email);
                    // Load the ID picture from the correct storage path

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        //lists of choices
        String[] companyChoices = {"SmoothMoves, Inc.", "BrandBoss", "MyRecruiter", "Aickman & Greene", "MoneyFrog", "BeanBag"};

        // Initialize the AutoCompleteTextViews
        companyClientDd = findViewById(R.id.dropdown_companyClient);

        //ArrayAdapter for the company AutoCompleteTextView
        ArrayAdapter<String> companyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, companyChoices);

        // Set the adapters to the AutoCompleteTextViews
        companyClientDd.setAdapter(companyAdapter);

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pd.setMessage("Updating your Profile Picture");
                pd = new ProgressDialog(VisitorformActivity.this);
                pd.setMessage("Uploading Image...");
                profileOrCoverPhoto = "IDImages/";
                pickFromCamera();

            }
        });



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        notifyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                notify = true;
                if (TextUtils.isEmpty(nameEt.getText()) || TextUtils.isEmpty(contactPersonEt.getText()) || TextUtils.isEmpty(companyClientDd.getText()) || TextUtils.isEmpty(purposeOfVisitEt.getText()) || idPicIv.getDrawable() == null) {
                    Toast.makeText(VisitorformActivity.this, "Please fill in all the fields and provide a picture of your ID", Toast.LENGTH_SHORT).show();
                } else {
                    saveVisitorInfo();

                    // Send email when "e-notify" is clicked
                    sendEmailToUser();
                }


            }
        });
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestStoragePermission() {

        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);

    }
    private boolean checkCameraPermission() {

        boolean result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {

        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {

                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        //Toast.makeText(getActivity(), "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                        pickFromCamera();
                    }
                }

            }
            break;
            case STORAGE_REQUEST_CODE: {

                if (grantResults.length > 0) {

                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (writeStorageAccepted) {
                        pickFromGallery();
                        //Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    } else {

                        pickFromGallery();

                    }

                }

            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //image is picked from gallery, get uri

                image_uri = data.getData();
                uploadProfileCoverPhoto(image_uri);


            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //image is picked from camera, get uri
                uploadProfileCoverPhoto(image_uri);

            }

        }
        super.onActivityResult(requestCode, resultCode, data);


    }


    private void uploadProfileCoverPhoto(final Uri uri) {
        //show progress
        pd.show();

        //path and name of image to be stored
        String filePathAndName = storagePath + "" + profileOrCoverPhoto + "_" + user.getUid();
        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();

                        if (uriTask.isSuccessful()) {
                            // Image uploaded successfully
                            // Add/update URI in user

                            HashMap<String, Object> results = new HashMap<>();
                            results.put(profileOrCoverPhoto, downloadUri.toString());
                            idPicUrl = downloadUri.toString(); // Save the URL
                            dbRef.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // Dismiss the progress dialog
                                            pd.dismiss();
                                            Toast.makeText(VisitorformActivity.this, "Image Uploaded...", Toast.LENGTH_SHORT).show();
                                            // Load the captured image into idPicIv
                                            Picasso.get().load(image_uri).into(idPicIv);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(VisitorformActivity.this, "Error Updating Image...", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Error
                            pd.dismiss();
                            Toast.makeText(VisitorformActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(VisitorformActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }


    private void pickFromCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image
        image_uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {

        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }






    private void sendEmailToUser() {
        // Replace 'userEmail' with the actual email address of the user
        String userEmail = emailTv.getText().toString().trim();

        // Get visitor information
        String purposeOfVisit = purposeOfVisitEt.getText().toString().trim();
        String visitorName = nameEt.getText().toString().trim();
        String contactPerson = contactPersonEt.getText().toString().trim();
        String companyClient = companyClientDd.getText().toString().trim();
        String timestamp = timestampTextView.getText().toString().trim();

        // Create the email subject and body
        String subject = "Subject: " + purposeOfVisit;
        String body = "Good day, " + contactPerson + "." + "\n" + "\n" +
                "You have a visitor." + "\n" + "\n" +
                "Visitor information:" + "\n" + "\n" +
                "Visitor Name: " + visitorName + "\n" +
                "Company Client: " + companyClient + "\n" +
                "Timestamp: " + timestamp + "\n";

        // Create an intent to send an email
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.setPackage("com.google.android.gm"); // Specify the Gmail package name
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { userEmail });
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        // Start the Gmail app directly
        startActivity(emailIntent);
    }


    private void saveVisitorInfo() {

        String name = nameEt.getText().toString().trim();
        String contactPerson = contactPersonEt.getText().toString().trim();
        String companyClient = companyClientDd.getText().toString().trim();
        String purposeOfVisit = purposeOfVisitEt.getText().toString().trim();
        //ts
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy, hh:mm a", Locale.getDefault());
        String formattedTimestamp = sdf.format(new Date());

        HashMap<String, Object> hashMap = new HashMap<>();
        // PUT INFO IN HASHMAP
        hashMap.put("VisitorsName", name);
        hashMap.put("ContactPerson", contactPerson);
        hashMap.put("CompanyClient", companyClient);
        hashMap.put("PurposeofVisit", purposeOfVisit);
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("timestamp", formattedTimestamp);
        hashMap.put("IDPicURL", idPicUrl);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Visitors' Information").push().setValue(hashMap);


        DatabaseReference databaseeReference = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        databaseeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification(hisUid, name, purposeOfVisit, contactPerson, formattedTimestamp);
                }
                notify = false;

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(String hisUid, String name, String purposeOfVisit, String contactPerson, String formattedTimestamp) {

        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUid, name+":"+purposeOfVisit, "You have a visitor" + "\n" +formattedTimestamp, hisUid, R.drawable.applogo);


                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                                    // Save visitor information in the "Notifications" section
                                    saveVisitorInfoToNotifications(name, contactPerson, companyClientDd.getText().toString(), purposeOfVisit, myUid, hisUid, formattedTimestamp);
                                    Toast.makeText(VisitorformActivity.this, "Notification Sent", Toast.LENGTH_SHORT).show();
                                    onBackPressed();

                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveVisitorInfoToNotifications(String name, String contactPerson, String companyClient, String purposeOfVisit, String senderUid, String receiverUid, String formattedTimestamp) {
        DatabaseReference notificationsRef = firebaseDatabase.getReference("Notifications");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("VisitorsName", name);
        hashMap.put("ContactPerson", contactPerson);
        hashMap.put("CompanyClient", companyClient);
        hashMap.put("PurposeofVisit", purposeOfVisit);
        hashMap.put("sender", senderUid);
        hashMap.put("receiver", receiverUid);
        hashMap.put("timestamp", formattedTimestamp);

        notificationsRef.push().setValue(hashMap);
    }


    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){

            myUid = user.getUid();

        } else {

            startActivity(new Intent(VisitorformActivity.this, SplashActivity.class));
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
}