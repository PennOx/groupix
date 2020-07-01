package tk.pankajb.groupix;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DataStore {

    public FirebaseAuth Auth = FirebaseAuth.getInstance();
    private FirebaseUser CurrentUser;

    private static String tempString;

    private DatabaseReference UserDataRef = FirebaseDatabase.getInstance().getReference().child("users");
    private DatabaseReference AllUserDataRef = FirebaseDatabase.getInstance().getReference().child("users").child("all");
    private DatabaseReference VerifiedUserDataRef = FirebaseDatabase.getInstance().getReference().child("users").child("verified");
    private DatabaseReference UnVerifiedUserDataRef = FirebaseDatabase.getInstance().getReference().child("users").child("unverified");
    private DatabaseReference AlbumsDataRef = FirebaseDatabase.getInstance().getReference().child("albums");
    private DatabaseReference ImagesDataRef = FirebaseDatabase.getInstance().getReference().child("images");

    private StorageReference ImagesStorageRef = FirebaseStorage.getInstance().getReference().child("images");
    private StorageReference UsersStorageRef = FirebaseStorage.getInstance().getReference().child("users");
    private StorageReference AlbumsStorageRef = FirebaseStorage.getInstance().getReference().child("albums");


    public DataStore() {

        UserDataRef.keepSynced(true);
        AlbumsDataRef.keepSynced(true);
        ImagesDataRef.keepSynced(true);
        VerifiedUserDataRef.keepSynced(true);
    }

    public DatabaseReference getUserDataRef() {
        return UserDataRef;
    }

    public DatabaseReference getAllUserDataRef() {
        return AllUserDataRef;
    }

    public DatabaseReference getVerifiedUserDataRef() {
        return VerifiedUserDataRef;
    }

    public DatabaseReference getUnVerifiedUserDataRef() {
        return UnVerifiedUserDataRef;
    }

    public DatabaseReference getAlbumsDataRef() {
        return AlbumsDataRef;
    }

    public DatabaseReference getImagesDataRef() {
        return ImagesDataRef;
    }

    public StorageReference getImagesStorageRef() {
        return ImagesStorageRef;
    }

    public StorageReference getAlbumsStorageRef() {
        return AlbumsStorageRef;
    }

    public StorageReference getUsersStorageRef() {
        return UsersStorageRef;
    }

    public FirebaseUser getCurrentUser() {
        this.CurrentUser = Auth.getCurrentUser();
        return CurrentUser;
    }

    public String getCurrentUserId() {
        if (this.getCurrentUser() != null) {
            this.tempString = this.CurrentUser.getUid();
        }
        return this.tempString;
    }

    public String getCurrentUserName() {
        if (this.getCurrentUser() != null) {
            this.tempString = Auth.getCurrentUser().getDisplayName();
        }
        return this.tempString;
    }

    public String getCurrentUserLastName() {
        VerifiedUserDataRef.child(getCurrentUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                tempString = dataSnapshot.child("lastname").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return tempString;
    }

//    public String getCurrentUserFullName(){
//        return getCurrentUserName()+" "+getCurrentUserLastName();
//    }
//
//    public String getCurrentUserProfileThumb(){
//
//        getVerifiedUserDataRef().child(getCurrentUserId()).child("profileThumbImage").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                tempString = (String) dataSnapshot.getValue();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        return tempString;
//    }

//    public String getUseName(String UId){
//
//        if (UId.isEmpty()){
//            return "";
//        }
//        getVerifiedUserDataRef().child(UId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                tempString = (String) dataSnapshot.getValue();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        return tempString;
//    }
//
//    public String getUserLastName(String UId){
//        if (UId.isEmpty()){
//            return "";
//        }
//        getVerifiedUserDataRef().child(UId).child("lastname").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                tempString = (String) dataSnapshot.getValue();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        return tempString;
//    }

//    public String getUserFullName(String UId){
//        return getUseName(UId)+" "+getUserLastName(UId);
//    }

//    public String getImgOwnerId(String ImageId){
//
//        tempString = "";
//        getImagesDataRef().child("allimages").child(ImageId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                tempString = dataSnapshot.getValue().toString();
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//
//        });
//        while(tempString.isEmpty()){
//
//        }
//        return tempString;
//    }
}
