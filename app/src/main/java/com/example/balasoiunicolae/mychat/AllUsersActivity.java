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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private RecyclerView mRecView;
    private DatabaseReference mRef;
    private FirebaseRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mRecView = (RecyclerView)findViewById(R.id.recView);
        mRecView.setHasFixedSize(true);
        mRecView.setLayoutManager(new LinearLayoutManager(this));

        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }


    @Override
    protected void onStart(){
        super.onStart();


        Query query = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseRecyclerOptions<UserModel> options =
                new FirebaseRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class)
                .build();

         adapter = new FirebaseRecyclerAdapter<UserModel,UsersViewHolder>(options){

            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layour,parent,false);

                return new UsersViewHolder(view);
            }

            protected void onBindViewHolder(UsersViewHolder holder, int position, UserModel model){

                UserModel item = getItem(position);
                holder.setName(item.getNickname());
                holder.setStatus(item.getStatus());
                holder.setImage(item.getImage());

                final String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(AllUsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);
                    }
                });
            }

        };

        mRecView.setAdapter(adapter);
        adapter.startListening();

    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public UsersViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){

            TextView nicknameView = (TextView)mView.findViewById(R.id.nameId);
            nicknameView.setText(name);
        }

        public void setStatus(String status){

            TextView statusView = (TextView)mView.findViewById(R.id.statusId);
            statusView.setText(status);
        }

        public void setImage(String url){

            CircleImageView imageView = (CircleImageView)mView.findViewById(R.id.circleImageView);
            Picasso.with(AllUsersActivity.this).load(url).into(imageView);
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
                            Toast.makeText(AllUsersActivity.this,
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
