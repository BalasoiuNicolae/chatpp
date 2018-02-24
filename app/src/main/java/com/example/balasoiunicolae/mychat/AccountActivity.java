package com.example.balasoiunicolae.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {

    private CircleImageView Image;
    private DatabaseReference mRef;
    private FirebaseUser currentUser;
    private TextView mStatus;
    private TextView mNickname;
    private Button statusBtn;
    private Button imageBtn;
    private ProgressDialog mPrg;
    private StorageReference sRef;

    private static final int GALLERY_PICK = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Image = (CircleImageView)findViewById(R.id.ImageID);
        mStatus = (TextView)findViewById(R.id.status);
        mNickname = (TextView)findViewById(R.id.nicknameID);
        statusBtn = (Button)findViewById(R.id.change_status);
        imageBtn = (Button) findViewById(R.id.change_avatar);
        sRef = FirebaseStorage.getInstance().getReference();


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();

        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("nickname").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                mStatus.setText(status);
                mNickname.setText(name);
                Picasso.with(AccountActivity.this).load(image).into(Image);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sIntent = new Intent(AccountActivity.this,StatusActivity.class);
                startActivity(sIntent);
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AccountActivity.this);

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            String imageUri = data.getDataString();

            CropImage.activity()
                    .setAspectRatio(1,1)
                    .start(this);

           // Toast.makeText(AccountActivity.this,imageUri,Toast.LENGTH_LONG).show();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mPrg = new ProgressDialog(AccountActivity.this);
                mPrg.setTitle("Uploading image..");
                mPrg.setMessage("Wait...");
                mPrg.show();

                String uId = currentUser.getUid();
                Uri resultUri = result.getUri();
                StorageReference filepath = sRef.child("profile_pictures").child(uId + "jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {

                            String download_url = task.getResult().getDownloadUrl().toString();

                            mRef.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mPrg.dismiss();
                                    }
                                }
                            });

                        }

                        else
                        {
                            Toast.makeText(AccountActivity.this,"Error with image", Toast.LENGTH_LONG).show();
                            mPrg.dismiss();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(15);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AccountActivity.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();

                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));

                        }
                    });
        }

        else if(item.getItemId() == R.id.menu_profile){
            startActivity(new Intent(getApplicationContext(),AccountActivity.class));

        }

        else if(item.getItemId() == R.id.all_users){
            startActivity(new Intent(getApplicationContext(),AllUsersActivity.class));
        }


        else if(item.getItemId() == R.id.all_friends){
            startActivity(new Intent(getApplicationContext(),SeeFriendsActivity.class));
        }

        else if(item.getItemId() == R.id.group_chat){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        return true;
    }
}
