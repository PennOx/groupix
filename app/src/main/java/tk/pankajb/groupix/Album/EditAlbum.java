package tk.pankajb.groupix.Album;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import tk.pankajb.groupix.DataStore;
import tk.pankajb.groupix.Home.HomeActivity;
import tk.pankajb.groupix.R;

public class EditAlbum extends AppCompatActivity {

    final private short ADD_ALBUM_COVER_REQUEST = 1;
    ImageView albumCoverImg;
    ImageButton addCoverBtn;
    LinearLayout coverBtnLayout;
    EditText albumNameText;
    EditText albumDescText;
    String albumName;
    String albumDesc;
    String albumCoverLink;
    String albumId;
    String albumOwnerId;
    DataStore AppData = new DataStore();
    ProgressDialog editAlbumProgressBar;
    Uri albumCoverUri = null;

    Context appContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_album);

        albumCoverImg = findViewById(R.id.EditAlbum_CoverImg);
        addCoverBtn = findViewById(R.id.EditAlbum_AddCoverButton);
        coverBtnLayout = findViewById(R.id.EditAlbum_CoverButtonLayout);
        albumNameText = findViewById(R.id.EditAlbum_AlbumName);
        albumDescText = findViewById(R.id.EditAlbum_AlbumDescription);

        appContext = getApplicationContext();

        editAlbumProgressBar = new ProgressDialog(EditAlbum.this);
        editAlbumProgressBar.setTitle("Editing album");

        albumId = getIntent().getStringExtra("AlbumId");
        albumOwnerId = getIntent().getStringExtra("AlbumOwnerId");

        AppData.getAlbumsDataRef().child(albumOwnerId).child(albumId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String coverimg = dataSnapshot.child("coverimg").getValue(String.class);
                String desc = dataSnapshot.child("description").getValue(String.class);
                String name = dataSnapshot.child("name").getValue(String.class);

                albumNameText.setText(name);
                albumDescText.setText(desc);

                try {
                    assert coverimg != null;
                    if (!coverimg.equals("default")) {
                        Glide.with(appContext).load(coverimg).into(albumCoverImg);
                        coverBtnLayout.setVisibility(View.VISIBLE);
                        addCoverBtn.setVisibility(View.GONE);
                    } else {
                        Glide.with(appContext).load(R.drawable.home_background).into(albumCoverImg);
                        addCoverBtn.setVisibility(View.VISIBLE);
                        coverBtnLayout.setVisibility(View.GONE);
                    }
                } catch (NullPointerException e) {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_ALBUM_COVER_REQUEST && resultCode == RESULT_OK) {
            albumCoverUri = data.getData();
            Glide.with(this).load(albumCoverUri).into(albumCoverImg);
            coverBtnLayout.setVisibility(View.VISIBLE);
            addCoverBtn.setVisibility(View.GONE);
        }
    }

    public void editAlbum(View view) {

        albumName = albumNameText.getText().toString().trim();
        albumDesc = albumDescText.getText().toString().trim();

        if (albumName.isEmpty()) {
            albumNameText.setError("New Album Name Required");
        } else {
            if (albumDesc.isEmpty())
                albumDesc = getString(R.string.Default_Album_Desc);

            if (albumCoverUri != null) {
                editAlbumProgressBar.show();

                UploadTask AlbumCoverUpload = AppData.getAlbumsStorageRef().child(AppData.getCurrentUserId()).child(String.valueOf(albumId))
                        .child("coverimg").putFile(albumCoverUri);

                AlbumCoverUpload.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        long ProgressDone = 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                        editAlbumProgressBar.setMessage("Uploading Image " + ProgressDone + "%");
                    }
                });

                AlbumCoverUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        albumCoverLink = String.valueOf(taskSnapshot.getDownloadUrl());

                        Map NewAlbumMap = new HashMap<>();
                        NewAlbumMap.put(AppData.getCurrentUserId() + "/" + albumId + "/name", albumName);
                        NewAlbumMap.put(AppData.getCurrentUserId() + "/" + albumId + "/description", albumDesc);
                        NewAlbumMap.put(AppData.getCurrentUserId() + "/" + albumId + "/coverimg", albumCoverLink);

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
                NewAlbumMap.put(AppData.getCurrentUserId() + "/" + albumId + "/name", albumName);
                NewAlbumMap.put(AppData.getCurrentUserId() + "/" + albumId + "/description", albumDesc);

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

    public void deleteCover(View view) {
        AppData.getAlbumsDataRef().child(albumOwnerId).child(albumId).child("coverimg")
                .setValue("default").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AppData.getAlbumsStorageRef().child(albumOwnerId).child(albumId).child("coverimg").delete();
                addCoverBtn.setVisibility(View.VISIBLE);
                coverBtnLayout.setVisibility(View.GONE);
            }
        });
    }

    public void addCover(View view) {
        Intent addAlbumCoverGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(addAlbumCoverGalleryIntent, ADD_ALBUM_COVER_REQUEST);
    }

    public void deleteAlbum(View view) {

        AlertDialog.Builder deleteConfDialog = new AlertDialog.Builder(EditAlbum.this);
        deleteConfDialog.setTitle("Are you sure?");
        deleteConfDialog.setMessage("You want to delete this album.");
        deleteConfDialog.setCancelable(true);
        deleteConfDialog.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AppData.getAllAlbumsDataRef().child(albumId).setValue(null);
                        AppData.getAlbumsDataRef().child(albumOwnerId).child(albumId).setValue(null);

                        sendToHome();

                    }
                });
        deleteConfDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert1 = deleteConfDialog.create();
        alert1.show();
    }

    private void sendToHome() {
        Intent sendingToHomeIntent = new Intent(EditAlbum.this, HomeActivity.class);
        startActivity(sendingToHomeIntent);
        sendingToHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
    }

    public void editCover(View view) {
        // TODO edit Album cover
        Intent editAlbumCoverGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(editAlbumCoverGalleryIntent, ADD_ALBUM_COVER_REQUEST);
    }
}
