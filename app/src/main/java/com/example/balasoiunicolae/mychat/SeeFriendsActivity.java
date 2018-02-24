package com.example.balasoiunicolae.mychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class SeeFriendsActivity extends AppCompatActivity {

    private RecyclerView mRecView;
    private DatabaseReference mRef;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseUser current_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_friends);

        mRecView = (RecyclerView)findViewById(R.id.Recycler);
        mRecView.setHasFixedSize(true);
        mRecView.setLayoutManager(new LinearLayoutManager(this));
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        current_user = FirebaseAuth.getInstance().getCurrentUser();

    }


    @Override
    protected void onStart(){
        super.onStart();


        Query query = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_user.getUid());

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query,Friends.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Friends,FriendsViewHolder>(options){

            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layour,parent,false);

                return new FriendsViewHolder(view);
            }

            protected void onBindViewHolder(final FriendsViewHolder holder, int position, Friends model){

                Friends item = getItem(position);

                holder.setDate(item.getDate());


                final String user_id = getRef(position).getKey();

               mRef.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       String userName = dataSnapshot.child("nickname").getValue().toString();
                       String userImage = dataSnapshot.child("image").getValue().toString();

                        holder.setImage(userImage);
                        holder.setName(userName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(SeeFriendsActivity.this,ChatActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);
                    }
                });


            }

        };

        mRecView.setAdapter(adapter);
        adapter.startListening();

    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public FriendsViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){

            TextView nicknameView = (TextView)mView.findViewById(R.id.nameId);
            nicknameView.setText(name);
        }

        public void setDate(String date){

            TextView statusView = (TextView)mView.findViewById(R.id.statusId);
            statusView.setText(date);
        }

        public void setImage(String url){

            CircleImageView imageView = (CircleImageView)mView.findViewById(R.id.circleImageView);
            Picasso.with(SeeFriendsActivity.this).load(url).into(imageView);
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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
                            Toast.makeText(SeeFriendsActivity.this,
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
