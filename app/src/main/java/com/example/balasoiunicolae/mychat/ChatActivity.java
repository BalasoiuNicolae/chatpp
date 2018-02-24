package com.example.balasoiunicolae.mychat;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference mRef;
    private DatabaseReference mRef2;
    private DatabaseReference mRef3;
    private FirebaseUser currentUser;
    private ListView mListView;
    private EditText mEditText;
    private FloatingActionButton mSend;
    private String mChatUser;
    private FirebaseListAdapter adapter;
    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mListView = (ListView) findViewById(R.id.ChatListView);
        mEditText = (EditText) findViewById(R.id.msgChat);
        mSend = (FloatingActionButton) findViewById(R.id.sendChat);

        mChatUser = getIntent().getStringExtra("user_id");

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_message();
            }
        });

        display_messages();



    }

    private void display_messages() {

        Query query = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUser.getUid()).child(mChatUser);

        FirebaseListOptions<MessageModel> options = new FirebaseListOptions.Builder<MessageModel>()
                .setQuery(query,MessageModel.class)
                .setLayout(R.layout.chat_model)
                .build();

        adapter = new FirebaseListAdapter<MessageModel>(options) {
            @Override
            protected void populateView(View v, MessageModel model, int position) {

                BubbleTextView messageText = (BubbleTextView) v.findViewById(R.id.message_text_chat);
                messageText.setText(model.getMessage());


                TextView messageTime = (TextView)v.findViewById(R.id.message_time_chat);
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getDate()));

                final CircleImageView messageImage = (CircleImageView)v.findViewById(R.id.imageChat);
                Picasso.with(ChatActivity.this).load(model.getImage()).into(messageImage);

              /* mRef3.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Picasso.with(ChatActivity.this).load(model.getImage()).into(messageImage);

                    }

                     @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                })*/

            }
        };

        mListView.setAdapter(adapter);

        mListView.post(new Runnable(){
            public void run() {
                mListView.setSelection(mListView.getCount() - 1);
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

    private void send_message() {

    mRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUser.getUid()).child(mChatUser);
    mRef2 = FirebaseDatabase.getInstance().getReference().child("Messages").child(mChatUser).child(currentUser.getUid());

    DatabaseReference mRefImage = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
    mRefImage.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

             URL = dataSnapshot.child("image").getValue().toString();


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
    mRef.push().setValue(new MessageModel(mEditText.getText().toString(),URL));
    mRef2.push().setValue(new MessageModel(mEditText.getText().toString(),URL));
    mEditText.setText("");


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
                            Toast.makeText(ChatActivity.this,
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
