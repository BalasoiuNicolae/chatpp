package com.example.balasoiunicolae.mychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.AuthResult;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private FloatingActionButton send;
    private ListView listMess;
    private EditText message;
    private FirebaseAuth firebaseAuth;
    private FirebaseListAdapter<Message> adapter;
    private FirebaseDatabase mIns;
    private DatabaseReference mRef;
    private DatabaseReference mRef2;
    private FirebaseUser user;
    private String mChatUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send = (FloatingActionButton)findViewById(R.id.sendBtn);
        message = (EditText)findViewById(R.id.msgET);

        send.setOnClickListener(this);


        displayMessage();
    }



    private void displayMessage() {

        listMess = (ListView) findViewById(R.id.ChatListView);


       Query query = FirebaseDatabase.getInstance().getReference().child("Chats");

       FirebaseListOptions<Message> options =
                new FirebaseListOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .setLayout(R.layout.message_model)
                        .build();

        adapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(View v, Message model, int position) {
                BubbleTextView messageText = (BubbleTextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMess());

                firebaseAuth = FirebaseAuth.getInstance();
                user = firebaseAuth.getCurrentUser();
                messageUser.setText(model.getUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getTime()));
            }
        };

        listMess.setAdapter(adapter);

        listMess.post(new Runnable(){
            public void run() {
                listMess.setSelection(listMess.getCount() - 1);
            }});

    }

    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    public void onClick(View view){


        mIns = FirebaseDatabase.getInstance();
        mRef = mIns.getReference("Chats");
        mRef.push().setValue(new Message(message.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
        message.setText("");
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
                            Toast.makeText(MainActivity.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            //finish();
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
