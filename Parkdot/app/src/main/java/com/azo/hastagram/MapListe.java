package com.azo.hastagram;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azo.hastagram.Models.IonClicled;
import com.azo.hastagram.Models.Park;
import com.azo.hastagram.Models.User;
import com.azo.hastagram.View.CircleTransform;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class MapListe extends AppCompatActivity {
    User user;
    TextView empty;
    ImageView imageViewProfile;
    ImageView editImageview;
    RecyclerView recyclerView;
    TextView nameTv;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference myRef;
    private StorageReference mStorageRef;
    FirebaseAuth mAuth;
    ArrayList<Park> parkList;
    Button dialogYesBtn;
    Button dialogNoBtn;
    TextView dialogText;
    Context context;
    Uri updateImageview;
    private String imagePathUpload = "";
    private String imageNameUpload = "";
    ProgressBar progressBar;
    ViewGroup circleLy, circleLyEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_map_liste);
        circleLy = findViewById(R.id.circleLy);
        circleLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageExpand();
            }
        });
        circleLyEdit = findViewById(R.id.circleLyEdit);
        circleLyEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSaveIcon();
            }
        });
        mStorageRef = FirebaseStorage.getInstance().getReference();
        imageViewProfile = findViewById(R.id.profilImageView);
        editImageview = findViewById(R.id.imageViewEdit);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recylerviewListe);
        progressBar = findViewById(R.id.progressbar);
        nameTv = findViewById(R.id.mapListName);
        empty = findViewById(R.id.empty_rcv);
        firebaseDatabase = firebaseDatabase.getInstance();
        //Dbden Veriyi Almak için
        mAuth = FirebaseAuth.getInstance();
        myRef = firebaseDatabase.getReference("users");
        myRef = myRef.child(mAuth.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parkList = new ArrayList<>();
                if (dataSnapshot.getValue() == null)
                    return;
                user = new User();
                String nameStr = ((HashMap<String, String>) dataSnapshot.getValue()).get("name");
                user.setName(nameStr);
                String surnameStr = ((HashMap<String, String>) dataSnapshot.getValue()).get("surname");
                user.setSurname(surnameStr);
                String imageStr = ((HashMap<String, String>) dataSnapshot.getValue()).get("image");
                user.setImage(imageStr);
                bindViews(nameStr, surnameStr, imageStr);
                HashMap<String, HashMap> parkHashList = ((HashMap<String, HashMap>) dataSnapshot.getValue()).get("parkList");
                if (parkHashList == null || parkHashList.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    return;
                }
                recyclerView.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
                for (String key : parkHashList.keySet()) {
                    Park park = CommanFunctions.hashToModel(parkHashList.get(key));
                    parkList.add(park);
                }
                ListAdapter listAdapter = new ListAdapter();
                sort();
                recyclerView.setAdapter(listAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MapListe.this));


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sort() {
        Collections.sort(parkList, new Comparator<Park>() {
            public int compare(Park obj1, Park obj2) {
                return obj1.getParkName().compareToIgnoreCase(obj2.getParkName());
            }
        });
    }


    private void updateImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.update_image_maplist);
        final AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(false);
        Button updateImageNo = alertDialog.findViewById(R.id.updateMaplistFotoNoBtn);
        Button updateImageYes = alertDialog.findViewById(R.id.updateMaplistFotoYesBtn);
        updateImageNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        updateImageYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageMethod();
                alertDialog.dismiss();
            }
        });

    }


    public void imageSaveIcon() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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


    public void editNameMapListe(View view) {
        BottomSheetNameEditDialog bottomSheetNameEditDialog =
                new BottomSheetNameEditDialog().setPark(user).setOnclickListener(new IonClicled() {
                    @Override
                    public void onClicked(String name, String surname) {
                        myRef.child("name").setValue(name);
                        myRef.child("surname").setValue(surname);

                    }
                });
        bottomSheetNameEditDialog.show(getSupportFragmentManager(), "bottomSheetNameEditDialog");


    }


    public void bindViews(String name, String surname, String image) {

        nameTv.setText(name.toUpperCase() + " " + surname.toUpperCase());
        Picasso.get().load(R.drawable.ekleresim).transform(new CircleTransform()).into(editImageview);
        if (!TextUtils.isEmpty(image)) {
            Picasso.get().load(image).transform(new CircleTransform()).into(imageViewProfile);
        } else
            Picasso.get().load(R.drawable.resimyok).transform(new CircleTransform()).into(imageViewProfile);

    }


    public void addLocation(View view) {

        Intent intent = new Intent(MapListe.this, HastagramMap.class);
        startActivity(intent);

    }

    public void deletePark(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.delete_alert_dialog);
        final AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(false);
        dialogYesBtn = alertDialog.findViewById(R.id.yesBtn);
        dialogNoBtn = alertDialog.findViewById(R.id.noBtn);
        dialogText = alertDialog.findViewById(R.id.deleteText);

        dialogYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef = databaseReference.child(mAuth.getUid()).child("parkListe");
            }
        });

    }


    public void logOut(View view) {
        customAlertLogout();
    }

    public void customAlertLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.alert_logout_dialog);
        final AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(false);
        dialogYesBtn = alertDialog.findViewById(R.id.yesBtn);
        dialogNoBtn = alertDialog.findViewById(R.id.noBtn);
        dialogText = alertDialog.findViewById(R.id.customText);

        dialogYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MapListe.this, SignInActivity.class);
                startActivity(intent);
                Toast.makeText(MapListe.this, getString(R.string.log_out), Toast.LENGTH_LONG).show();
                finish();

            }
        });
        dialogNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });


    }

    public void imageExpand() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.image_expand);
        final AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(true);
        final ImageView expandImage = alertDialog.findViewById(R.id.image_expand_Iv);
        if (user != null && !TextUtils.isEmpty(user.getImage()))
            Picasso.get().load(user.getImage()).into(expandImage);

    }


    class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder> {
        @NonNull
        @Override
        public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_rcview, parent, false);
            ListHolder holder = new ListHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ListHolder holder, int position) {
            //içini doldurur itemin
            holder.park = parkList.get(position);
            holder.locationName.setText(parkList.get(position).getParkName());

        }


        @Override
        public int getItemCount() {
            //Listenin boyutu kadar dönecek
            return parkList.size();
        }

        public class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView locationName;
            Park park;

            public ListHolder(@NonNull View itemView) {
                super(itemView);
                locationName = itemView.findViewById(R.id.mapName);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapListe.this, HastagramMap.class);
                intent.putExtra("park", park);
                intent.putExtra("isCreate", false);
                startActivity(intent);

            }

            public void setData(int position) {
                locationName.setText(parkList.get(position).getParkName());
                park = parkList.get(position);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            updateImageview = CropImage.getActivityResult(data).getUri();
            updateImage();
        }
    }

    public void uploadImageMethod() {
        progressBar.setVisibility(View.VISIBLE);
        imageNameUpload = Calendar.getInstance().getTimeInMillis() + "";
        StorageReference storageReference = mStorageRef.child("image/" + imageNameUpload + ".jpg");
        storageReference.putFile(updateImageview).addOnSuccessListener
                (this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        StorageReference newReference = FirebaseStorage.getInstance().getReference
                                ("image/" + imageNameUpload + ".jpg");
                        newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imagePathUpload = uri.toString();
                                myRef.child("image").setValue(imagePathUpload);
                                progressBar.setVisibility(View.GONE);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MapListe.this, getString(R.string.Error), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapListe.this, getString(R.string.Error), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }


}




