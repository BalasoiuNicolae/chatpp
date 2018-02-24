package com.example.balasoiunicolae.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog prg;
    private DatabaseReference mRef;
    private FirebaseDatabase mIns;
    private EditText editTextNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        editTextEmail = (EditText) findViewById(R.id.loguser);
        editTextPassword = (EditText) findViewById(R.id.passuser);
        editTextNickname = (EditText)findViewById(R.id.nickname);

        prg = new ProgressDialog(RegisterActivity.this);

        buttonRegister.setOnClickListener(this);

    }

   /* @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        //updateUI(currentUser);
    }*/

    private void register(){

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String nickname = editTextNickname.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(nickname)){

            Toast.makeText(this,"Please enter your nickname",Toast.LENGTH_LONG).show();
        }

       prg.setMessage("Registering...");
       prg.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override

            public void onComplete(@NonNull Task<AuthResult> task) {
             //   Log.d("FirebaseAuth", "createUserWithEmail:onComplete:" + task.isSuccessful());

                if(task.isSuccessful()){

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    mRef.setValue(new UserModel("I am user",nickname,"Default","Default"));

                    Toast.makeText(RegisterActivity.this,"Succesfully registered",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));


                }
                else{

                    Toast.makeText(RegisterActivity.this,"Error on registration",Toast.LENGTH_LONG).show();
                }
                prg.dismiss();


            }

        });





    }

    public void onClick(View view){

        register();
    }

}
