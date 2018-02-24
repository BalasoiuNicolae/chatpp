package com.example.balasoiunicolae.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username;
    private EditText password;
    private Button signIn;
    private Button register;
    private ProgressDialog prg;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){

            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }


        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.PassW);
        signIn = (Button) findViewById(R.id.LgnBtn);
        register = (Button) findViewById(R.id.RgrBtn);

        prg = new ProgressDialog(LoginActivity.this);

        signIn.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    public void onClick(View view){

        if(view == signIn){
            Login();
        }
        else

            if(view == register)
            {

                finish();
                startActivity(new Intent(this,RegisterActivity.class));

            }

    }

    private void Login(){

        String email = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        prg.setMessage("Logging in...");
        prg.show();

        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Succesfully logged in",Toast.LENGTH_LONG).show();

                    finish();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                }
                else{

                    Toast.makeText(LoginActivity.this,"Error occured when trying to log in",Toast.LENGTH_LONG).show();
                }
                prg.dismiss();
            }
        });
    }

}
