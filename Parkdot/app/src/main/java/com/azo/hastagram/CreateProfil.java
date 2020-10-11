package com.azo.hastagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.azo.hastagram.Models.User;
import com.azo.hastagram.View.CircleTransform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.Calendar;

public class CreateProfil extends AppCompatActivity {

    ImageView profilImage;
    ImageView addImageviewCreate;
    EditText profilText;
    EditText profilSurname;
    Button profilButton;
    EditText epostaTextt;
    EditText profilePasswordd;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    Uri selectedImage;
    private String userId;
    private String imagePath = "";
    private String imageName = "";
    ProgressBar progressBar;
    ViewGroup profilLy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profil);
        profilLy = findViewById(R.id.profilLy);
        profilImage = findViewById(R.id.profilImageView);
        addImageviewCreate = findViewById(R.id.imageViewEdit);
        profilText = findViewById(R.id.profilText);
        profilButton = findViewById(R.id.profilButton);
        epostaTextt = findViewById(R.id.epostaText);
        profilePasswordd = findViewById(R.id.profilePassword);
        profilSurname = findViewById(R.id.profilSurnameText);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        progressBar = findViewById(R.id.progressbar);
        picasso();
        profilLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSaveIcon();
            }
        });
    }

    public void picasso() {
        Picasso.get().load(R.drawable.ekleresim).transform(new CircleTransform()).into(addImageviewCreate);
        Picasso.get().load(R.drawable.resimyok).transform(new CircleTransform()).into(profilImage);
    }

    public void signUp(View view) {
        if (TextUtils.isEmpty(profilText.getText().toString()) || TextUtils.isEmpty(profilSurname.getText().toString()) ||
                TextUtils.isEmpty(epostaTextt.getText().toString()) || TextUtils.isEmpty(profilePasswordd.getText().toString())) {
            Toast.makeText(CreateProfil.this, getString(R.string.Please_fill_in_all_required_fields), Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(epostaTextt.getText().toString(), profilePasswordd.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                userId = task.getResult().getUser().getUid();
                                upload();
                            } else {
                                Toast.makeText(CreateProfil.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CreateProfil.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }


    }

    public void upload() {
        if (selectedImage == null) {
            saveUserToDb();
        } else {
            imageName = Calendar.getInstance().getTimeInMillis() + "";
            StorageReference storageReference = mStorageRef.child("image/" + imageName + ".jpg");
            storageReference.putFile(selectedImage).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    StorageReference newReference = FirebaseStorage.getInstance().getReference("image/" + imageName + ".jpg");
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            imagePath = uri.toString();
                            saveUserToDb();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateProfil.this, getString(R.string.Error), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CreateProfil.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    saveUserToDb();
                }
            });
        }
    }

    public void saveUserToDb() {
        progressBar.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(userId)) {
            myRef = FirebaseDatabase.getInstance().getReference();
            databaseReference = myRef.child("users").child(userId);
            databaseReference.setValue(new User().setName(profilText.getText().toString()).setImage(imagePath)
                    .setSurname(profilSurname.getText().toString()));
            Toast.makeText(CreateProfil.this, getString(R.string.Registration_successful), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CreateProfil.this, MapListe.class);
            startActivity(intent);
            finishAffinity();
        }

    }

    public void backToSignIn(View view) {
        onBackPressed();
    }


    public void imageSaveIcon() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedImage = CropImage.getActivityResult(data).getUri();
            Picasso.get().load(selectedImage).transform(new CircleTransform()).into(profilImage);

        }

    }

}
