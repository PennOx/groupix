package tk.pankajb.groupix.Album;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import tk.pankajb.groupix.DataStore;
import tk.pankajb.groupix.R;

public class CreateAlbum extends AppCompatActivity {

    final private short ADD_COVER_REQUEST = 1;
    final private long ALBUM_ID = System.currentTimeMillis();
    ImageButton addCoverBtn;
    ImageView albumCoverImg;
    EditText albumNameText;
    EditText albumDescText;
    Uri albumCoverUri;
    String albumName;
    String albumDesc;
    String albumCoverLink;
    DataStore AppData = new DataStore();
    private ProgressDialog createAlbumProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_album);

        addCoverBtn = findViewById(R.id.CreateAlbum_AddCoverButton);
        albumCoverImg = findViewById(R.id.CreateAlbum_CoverImg);
        albumNameText = findViewById(R.id.CreateAlbum_AlbumName);
        albumDescText = findViewById(R.id.CreateAlbum_AlbumDescription);

        createAlbumProgressBar = new ProgressDialog(CreateAlbum.this);
        createAlbumProgressBar.setTitle("Creating album");

    }

    public void createAlbum(View view) {

        albumName = albumNameText.getText().toString().trim();
        albumDesc = albumDescText.getText().toString().trim();
        albumCoverLink = getString(R.string.Default_Album_Cover);

        if (albumName.isEmpty()) {
            albumNameText.setError("New Album Name Required");
        } else {
            if (albumDesc.isEmpty())
                albumDesc = getString(R.string.Default_Album_Desc);

            if (albumCoverUri != null) {
                createAlbumProgressBar.show();

                UploadTask AlbumCoverUpload = AppData.getAlbumsStorageRef().child(AppData.getCurrentUserId()).child(String.valueOf(ALBUM_ID))
                        .child("coverimg").putFile(albumCoverUri);

                AlbumCoverUpload.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        long ProgressDone = 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                        createAlbumProgressBar.setMessage("Uploading Image " + ProgressDone + "%");
                    }
                });

                AlbumCoverUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        albumCoverLink = String.valueOf(taskSnapshot.getDownloadUrl());

                        Map NewAlbumMap = new HashMap<>();
                        NewAlbumMap.put(AppData.getCurrentUserId() + "/" + ALBUM_ID + "/name", albumName);
                        NewAlbumMap.put(AppData.getCurrentUserId() + "/" + ALBUM_ID + "/description", albumDesc);
                        NewAlbumMap.put(AppData.getCurrentUserId() + "/" + ALBUM_ID + "/coverimg", albumCoverLink);
                        NewAlbumMap.put("AllAlbums/" + ALBUM_ID, AppData.getCurrentUserId());

                        AppData.getAlbumsDataRef().updateChildren(NewAlbumMap).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                close(null);
                            }
                        });
                    }
                });
            } else {

                Map NewAlbumMap = new HashMap<>();
                NewAlbumMap.put(AppData.getCurrentUserId() + "/" + ALBUM_ID + "/name", albumName);
                NewAlbumMap.put(AppData.getCurrentUserId() + "/" + ALBUM_ID + "/description", albumDesc);
                NewAlbumMap.put(AppData.getCurrentUserId() + "/" + ALBUM_ID + "/coverimg", albumCoverLink);
                NewAlbumMap.put("AllAlbums/" + ALBUM_ID, AppData.getCurrentUserId());

                AppData.getAlbumsDataRef().updateChildren(NewAlbumMap).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        close(null);
                    }
                });
            }
        }

    }

    public void close(View view) {
        finish();
    }

    public void addCover(View view) {
        // TODO Add album cover
        Intent addAlbumCoverGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(addAlbumCoverGalleryIntent, ADD_COVER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_COVER_REQUEST && resultCode == RESULT_OK) {
            albumCoverUri = data.getData();
            addCoverBtn.setVisibility(View.GONE);
            Glide.with(this).load(albumCoverUri).into(albumCoverImg);
        }
    }
}