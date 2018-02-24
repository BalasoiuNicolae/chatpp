package com.example.balasoiunicolae.mychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {

    private TextView status;
    private TextView nick;
    private CircleImageView image;
    private Button friendRequest;


    private DatabaseReference mRef;
    private DatabaseReference mRequest;
    private DatabaseReference mFriend;
    private FirebaseUser current_user;
    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        final String user_id = getIntent().getStringExtra("user_id");
        status = (TextView)findViewById(R.id.keyView);
        nick = (TextView)findViewById(R.id.nickView);

        image = (CircleImageView)findViewById(R.id.imgView);
        friendRequest = (Button) findViewById(R.id.friendReq);

        mCurrent_state = "not friends";

        mRequest = FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        mFriend = FirebaseDatabase.getInstance().getReference().child("Friends");

        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("nickname").getValue().toString();
                String image2 = dataSnapshot.child("image").getValue().toString();
                String status2 = dataSnapshot.child("status").getValue().toString();

                status.setText(status2);
                nick.setText(name);
                Picasso.with(ProfileActivity.this).load(image2).into(image);


                mRequest.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request type").getValue().toString();
                            if(req_type.equals("received")){
                                mCurrent_state = "req received";
                                friendRequest.setText("Accept friend request");

                            }

                            else
                                if(req_type.equals("sent")){

                                mCurrent_state = "req sent";
                                friendRequest.setText("Cancel friend requests");
                                }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        current_user = FirebaseAuth.getInstance().getCurrentUser();

        friendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                friendRequest.setEnabled(false);

                if(mCurrent_state.equals("not friends")){

                    mRequest.child(current_user.getUid()).child(user_id).child("request type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mRequest.child(user_id).child(current_user.getUid()).child("request type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ProfileActivity.this,"Request Sent Succesfully",Toast.LENGTH_LONG).show();

                                        mCurrent_state = "req sent";
                                        friendRequest.setText("Cancel friend request");
                                        friendRequest.setEnabled(true);
                                    }
                                });
                            }

                            else
                                Toast.makeText(ProfileActivity.this,"Failed to send request",Toast.LENGTH_LONG).show();

                        }
                    });

                }
///Cancel request
                else
                    if(mCurrent_state.equals("req sent")){

                    mRequest.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mRequest.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    friendRequest.setEnabled(true);
                                    friendRequest.setText("Send friend request");
                                    mCurrent_state = "not friends";

                                }
                            });
                        }
                    });


                }

                // Accept

                if(mCurrent_state.equals("req received")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    mFriend.child(current_user.getUid()).child(user_id).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriend.child(user_id).child(current_user.getUid()).child("date").setValue(currentDate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mRequest.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mRequest.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            friendRequest.setEnabled(true);
                                                            friendRequest.setText("Unfriend");
                                                            mCurrent_state = "friends";

                                                        }
                                                    });
                                                }
                                            });

                                        }
                                    });
                        }
                    });

                }

            }
        });



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
                            Toast.makeText(ProfileActivity.this,
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
