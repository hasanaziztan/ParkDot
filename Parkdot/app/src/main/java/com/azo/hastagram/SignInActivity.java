package com.azo.hastagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText emailText;
    EditText passawordText;
    TextView paswordForgotText;
   ProgressBar progressBar;
    Button signInBtn;
    RelativeLayout relativeLayout1,relativeLayout2;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            relativeLayout1.setVisibility(View.VISIBLE);
            relativeLayout2.setVisibility(View.VISIBLE);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.giris_email);
        passawordText = findViewById(R.id.giris_sifre);
        paswordForgotText = findViewById(R.id.sifre_unutma_baglantisi);
       progressBar = findViewById(R.id.progressbar);
        signInBtn = findViewById(R.id.giris_butonu);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), MapListe.class);
            startActivity(intent);
            finish();
        }

        paswordForgotText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SignInActivity.this, PasswordActivity.class));


            }
        });

        relativeLayout1 = findViewById(R.id.rellay1);
        relativeLayout2 = findViewById(R.id.rellay2);
        handler.postDelayed(runnable,2000);


    }


    public void girisbtn(View view) {

        if (!TextUtils.isEmpty(emailText.getText()) && !TextUtils.isEmpty(passawordText.getText())) {
           progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(emailText.getText().toString(),
                    passawordText.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(), MapListe.class);
                                startActivity(intent);
                                Toast.makeText(SignInActivity.this,getString(R.string.Login_successful),Toast.LENGTH_LONG).show();
                                finish();
                            }


                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignInActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }

            });
        } else {
            Toast.makeText(SignInActivity.this,getString(R.string.Please_fill_in_all_required_fields),Toast.LENGTH_SHORT).show();

        }


    }

    public void hesapAl(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateProfil.class);
        startActivity(intent);
    }


}

