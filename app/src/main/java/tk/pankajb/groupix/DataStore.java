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

    private String CurrentUserId;
    private String CurrentUserName;
    private String CurrentUserLastName;

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


    public FirebaseUser getCurrentUser() {
        this.CurrentUser = Auth.getCurrentUser();
        return CurrentUser;
    }

    public String getCurrentUserId() {
        if (this.getCurrentUser() != null) {
            this.CurrentUserId = this.CurrentUser.getUid();
        }
        return this.CurrentUserId;
    }

    public String getCurrentUserName() {
        if (this.getCurrentUser() != null) {
            this.CurrentUserName = Auth.getCurrentUser().getDisplayName();
        }
        return this.CurrentUserName;
    }

    public String getCurrentUserLastName() {
        VerifiedUserDataRef.child(Auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                CurrentUserLastName = dataSnapshot.child("lastname").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return CurrentUserLastName;
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
}
