package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB, profileLevel;
    private CircleImageView userProfileImage;

    private DatabaseReference profileUserRef, FriendsRef, PostRef;
    private FirebaseAuth mAuth;

    private Button MyPosts,MyFriends;

    private String currentUserID;
    private int countFriends=0,countPosts=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        profileUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        FriendsRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        PostRef=FirebaseDatabase.getInstance().getReference().child("Posts");


        userName=(TextView) findViewById(R.id.my_username);
        userProfName=(TextView)findViewById(R.id.my_profile_full_name);
        userStatus=(TextView)findViewById(R.id.my_profile_status);
        userCountry=(TextView)findViewById(R.id.my_country);
        userGender=(TextView)findViewById(R.id.my_gender);
        userRelation=(TextView)findViewById(R.id.my_relationship_status);
        userDOB=(TextView)findViewById(R.id.my_dob);
        profileLevel=(TextView)findViewById(R.id.profile_level);
        userProfileImage=(CircleImageView)findViewById(R.id.my_profile_pic);
        MyFriends=(Button)findViewById(R.id.my_friends_button);
        MyPosts=(Button)findViewById(R.id.my_post_button);


        MyFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToFriendsActivity();
            }
        });

        MyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToMyPostsActivity();
            }
        });

        PostRef.orderByChild("uid")
                .startAt(currentUserID).endAt(currentUserID+"\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            countPosts=(int)dataSnapshot.getChildrenCount();
                            MyPosts.setText(Integer.toString(countPosts)+" Posts");
                        }
                        else {
                            MyPosts.setText("0 Posts");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FriendsRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    countFriends=(int)dataSnapshot.getChildrenCount();
                    MyFriends.setText(Integer.toString(countFriends)+" Friends");
                }
                else {
                    MyFriends.setText("0 Friends");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //String myProfileImage=dataSnapshot.child("profileimage").getValue().toString();   //error video 35 bhavya and manav
                    String myUserName=dataSnapshot.child("username").getValue().toString();
                    String myProfileName=dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus=dataSnapshot.child("status").getValue().toString();
                    String myDOB=dataSnapshot.child("dob").getValue().toString();
                    String myCountry=dataSnapshot.child("country").getValue().toString();
                    String myGender=dataSnapshot.child("gender").getValue().toString();
                    String myRelationshipStatus=dataSnapshot.child("relationshipstatus").getValue().toString();
                    String myProfileLevel=dataSnapshot.child("level").getValue().toString();

                    //Picasso.with(ProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);  //error video 35 bhavya and manav
                    //Picasso.get().load(myProfileImage).into(userProfileImage);

                    userName.setText("@"+myUserName);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDOB.setText("DOB: "+myDOB);
                    userRelation.setText("Relationship Status: "+myRelationshipStatus);
                    userGender.setText("Gender: "+myGender);
                    userCountry.setText("Country: "+myCountry);
                    profileLevel.setText("Level: "+myProfileLevel);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SendUserToFriendsActivity()
    {
        Intent friendIntent = new Intent(ProfileActivity.this, FriendsActivity.class);
        startActivity(friendIntent);
    }

    private void SendUserToMyPostsActivity()
    {
        Intent friendIntent = new Intent(ProfileActivity.this, MyPostsActivity.class);
        startActivity(friendIntent);
    }
}
