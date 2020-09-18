package tk.pankajb.groupix.handlers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private DatabaseReference allAlbumsDataRef;

    private StorageReference ImagesStorageRef = FirebaseStorage.getInstance().getReference().child("Images");
    private StorageReference UsersStorageRef = FirebaseStorage.getInstance().getReference().child("Users");
    private StorageReference AlbumsStorageRef = FirebaseStorage.getInstance().getReference().child("Albums");


    public DataStore() {

        UsersDataRef = DataStore.FI.getReference().child("Users");
        AlbumsDataRef = DataStore.FI.getReference().child("Albums");
        ImagesDataRef = DataStore.FI.getReference().child("Images");
        allAlbumsDataRef = AlbumsDataRef.child("AllAlbums");
        UsersDataRef.keepSynced(true);
        AlbumsDataRef.keepSynced(true);
        ImagesDataRef.keepSynced(true);
        allAlbumsDataRef.keepSynced(true);
    }

    public DatabaseReference getUsersDataRef() {
        return UsersDataRef;
    }

    public DatabaseReference getAlbumsDataRef() {
        return AlbumsDataRef;
    }

    public DatabaseReference getAllAlbumsDataRef() {
        return allAlbumsDataRef;
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

}
