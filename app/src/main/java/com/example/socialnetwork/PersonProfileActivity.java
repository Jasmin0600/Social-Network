package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB, userLevel;
    private CircleImageView userProfileImage;
    private Button SendFriendReqbutton, DeclineFriendRequestbutton;

    private DatabaseReference FriendRequestRef, UsersRef, FriendsRef;
    private FirebaseAuth mAuth;
    private String senderUserID, receiverUserId, CURRENT_STATE, saveCurrentDate, cureent_user_id;
    private DatabaseReference LevelRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth=FirebaseAuth.getInstance();
        senderUserID=mAuth.getCurrentUser().getUid();
        cureent_user_id=mAuth.getCurrentUser().getUid();


        receiverUserId=getIntent().getExtras().get("visit_user_id").toString();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef=FirebaseDatabase.getInstance().getReference().child("friendRequests");
        FriendsRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        LevelRef=FirebaseDatabase.getInstance().getReference().child("Users").child(cureent_user_id);


        InitializeFields();

        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                  //  String myProfileImage=dataSnapshot.child("profileimage").getValue().toString();   //error video 35 bhavya and manav
                    String myUserName=dataSnapshot.child("username").getValue().toString();
                    String myProfileName=dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus=dataSnapshot.child("status").getValue().toString();
                    String myDOB=dataSnapshot.child("dob").getValue().toString();
                    String myCountry=dataSnapshot.child("country").getValue().toString();
                    String myGender=dataSnapshot.child("gender").getValue().toString();
                    String myRelationshipStatus=dataSnapshot.child("relationshipstatus").getValue().toString();
                    String myUserLevel=dataSnapshot.child("level").getValue().toString();

                    //Picasso.with(PersonProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);  //error video 35 bhavya and manav
                 //   Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);

                    userName.setText("@"+myUserName);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDOB.setText("DOB: "+myDOB);
                    userRelation.setText("Relationship Status: "+myRelationshipStatus);
                    userGender.setText("Gender: "+myGender);
                    userCountry.setText("Country: "+myCountry);
                    userLevel.setText("Level: "+myUserLevel);

                    MaintananceofButtons();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DeclineFriendRequestbutton.setVisibility(View.INVISIBLE);
        DeclineFriendRequestbutton.setEnabled(false);

        if(!senderUserID.equals(receiverUserId)){

            SendFriendReqbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendFriendReqbutton.setEnabled(false);

                    if(CURRENT_STATE.equals("not_friends")){
                        SendFriendRequestToaPerson();
                        ValidateLevel();
                    }
                    if(CURRENT_STATE.equals("request_sent")){
                        CancelFriendRequest();
                    }
                    if(CURRENT_STATE.equals("request_received")){
                        AcceptFriendRequest();
                    }
                    if(CURRENT_STATE.equals("friends")){
                        UnFriendAnExistingFriend();
                    }
                }
            });
        }
        else {
            DeclineFriendRequestbutton.setVisibility(View.INVISIBLE);
            SendFriendReqbutton.setVisibility(View.INVISIBLE);
        }
    }

    private void ValidateLevel(){

        HashMap userMap=new HashMap();
        userMap.put("level",2);
        LevelRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

            }
        });
    }

    private void UnFriendAnExistingFriend() {
        FriendsRef.child(senderUserID).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsRef.child(receiverUserId).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendReqbutton.setEnabled(true);
                                                CURRENT_STATE="not_friends";
                                                SendFriendReqbutton.setText("Send Friend Request");

                                                DeclineFriendRequestbutton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestbutton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest() {
        Calendar calForDate= Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate=currentDate.format(calForDate.getTime());

        FriendsRef.child(senderUserID).child(receiverUserId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsRef.child(receiverUserId).child(senderUserID).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                FriendRequestRef.child(senderUserID).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    FriendRequestRef.child(receiverUserId).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        SendFriendReqbutton.setEnabled(true);
                                                                                        CURRENT_STATE="friends";
                                                                                        SendFriendReqbutton.setText("Unfriend this Person");

                                                                                        DeclineFriendRequestbutton.setVisibility(View.INVISIBLE);
                                                                                        DeclineFriendRequestbutton.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelFriendRequest() {
        FriendRequestRef.child(senderUserID).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserId).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendReqbutton.setEnabled(true);
                                                CURRENT_STATE="not_friends";
                                                SendFriendReqbutton.setText("Send Friend Request");

                                                DeclineFriendRequestbutton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestbutton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void MaintananceofButtons() {
        FriendRequestRef.child(senderUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserId)){
                            String request_type=dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();
                            if(request_type.equals("sent")){
                                CURRENT_STATE="request_sent";
                                SendFriendReqbutton.setText("Cancel Friend Request");

                                DeclineFriendRequestbutton.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestbutton.setEnabled(false);
                            }
                            else if (request_type.equals("received")){
                                CURRENT_STATE="request_received";
                                SendFriendReqbutton.setText("Accept Friend Request");

                                DeclineFriendRequestbutton.setVisibility(View.VISIBLE);
                                DeclineFriendRequestbutton.setEnabled(true);

                                DeclineFriendRequestbutton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelFriendRequest();
                                    }
                                });
                            }
                        }
                        else {
                            FriendsRef.child(senderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(receiverUserId)){
                                                CURRENT_STATE="friends";
                                                SendFriendReqbutton.setText("Unfriend this person");

                                                DeclineFriendRequestbutton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestbutton.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendFriendRequestToaPerson() {
            FriendRequestRef.child(senderUserID).child(receiverUserId)
                    .child("request_type").setValue("sent")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                FriendRequestRef.child(receiverUserId).child(senderUserID)
                                        .child("request_type").setValue("received")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    SendFriendReqbutton.setEnabled(true);
                                                    CURRENT_STATE="request_sent";
                                                    SendFriendReqbutton.setText("Cancel friend Request");

                                                    DeclineFriendRequestbutton.setVisibility(View.INVISIBLE);
                                                    DeclineFriendRequestbutton.setEnabled(false);
                                                }
                                            }
                                        });
                            }
                        }
                    });
    }

    private void InitializeFields() {
        userName=(TextView) findViewById(R.id.person_username);
        userProfName=(TextView)findViewById(R.id.person_full_name);
        userStatus=(TextView)findViewById(R.id.person_profile_status);
        userCountry=(TextView)findViewById(R.id.person_country);
        userGender=(TextView)findViewById(R.id.person_gender);
        userRelation=(TextView)findViewById(R.id.person_relationship_status);
        userDOB=(TextView)findViewById(R.id.person_dob);
        userLevel=(TextView)findViewById(R.id.person_level);
        userProfileImage=(CircleImageView)findViewById(R.id.person_profile_pic);

        SendFriendReqbutton=(Button)findViewById(R.id.person_send_friend_request_btn);
        DeclineFriendRequestbutton=(Button)findViewById(R.id.person_decline_friend_req);

        CURRENT_STATE="not_friends";
    }


}
