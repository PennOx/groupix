package tk.pankajb.groupix;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class DataStore {

    public FirebaseAuth Auth = FirebaseAuth.getInstance();
    private FirebaseUser CurrentUser;

    private static FirebaseDatabase FI;

    static {
        FI = FirebaseDatabase.getInstance();
        FI.setPersistenceEnabled(true);
    }

    private String tempString;

    private DatabaseReference UsersDataRef;
    private DatabaseReference AlbumsDataRef;
    private DatabaseReference ImagesDataRef;

    private StorageReference ImagesStorageRef = FirebaseStorage.getInstance().getReference().child("Images");
    private StorageReference UsersStorageRef = FirebaseStorage.getInstance().getReference().child("Users");
    private StorageReference AlbumsStorageRef = FirebaseStorage.getInstance().getReference().child("Albums");


    public DataStore() {

        UsersDataRef = DataStore.FI.getReference().child("Users");
        AlbumsDataRef = DataStore.FI.getReference().child("Albums");
        ImagesDataRef = DataStore.FI.getReference().child("Images");
        UsersDataRef.keepSynced(true);
        AlbumsDataRef.keepSynced(true);
        ImagesDataRef.keepSynced(true);
    }

    public DatabaseReference getUsersDataRef() {
        return UsersDataRef;
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
            this.tempString = Objects.requireNonNull(Auth.getCurrentUser()).getDisplayName();
        }
        return this.tempString;
    }

//    public String getCurrentUserLastName() {
//        VerifiedUserDataRef.child(getCurrentUserId()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                tempString = dataSnapshot.child("lastname").getValue(String.class);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//        return tempString;
//    }

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
