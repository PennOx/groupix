package tk.pankajb.groupix.album;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import tk.pankajb.groupix.R;
import tk.pankajb.groupix.handlers.DataStore;
import tk.pankajb.groupix.models.Album;

public class EditAlbum extends AppCompatActivity {

    final private short ADD_ALBUM_COVER_REQUEST = 1;
    Toolbar editAlbumToolbar;
    ImageView albumCoverImg;
    TextView addCoverBtn;
    LinearLayout coverBtnLayout;
    EditText albumNameText;
    EditText albumDescText;
    String albumName;
    String albumDesc;
    String albumId;
    String albumOwnerId;
    DataStore AppData = new DataStore();
    ProgressDialog editAlbumProgressBar;
    Uri albumCoverUri = null;
    byte[] croppedImageBytes = null;

    Context appContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_album);

        editAlbumToolbar = findViewById(R.id.Editalbum_Toolbar);
        albumCoverImg = findViewById(R.id.EditAlbum_CoverImg);
        addCoverBtn = findViewById(R.id.EditAlbum_AddCoverButton);
        coverBtnLayout = findViewById(R.id.EditAlbum_CoverButtonLayout);
        albumNameText = findViewById(R.id.EditAlbum_AlbumName);
        albumDescText = findViewById(R.id.EditAlbum_AlbumDescription);
        appContext = getApplicationContext();

        setSupportActionBar(editAlbumToolbar);
        getSupportActionBar().setTitle("Edit album");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editAlbumToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editAlbumProgressBar = new ProgressDialog(EditAlbum.this);
        editAlbumProgressBar.setTitle("Editing album");

        albumId = getIntent().getStringExtra("AlbumId");
        albumOwnerId = getIntent().getStringExtra("AlbumOwnerId");

        AppData.getAlbumsDataRef().child(albumOwnerId).child(albumId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Album currentAlbum = dataSnapshot.getValue(Album.class);

                albumNameText.setText(currentAlbum.getName());
                albumDescText.setText(currentAlbum.getDescription());

                try {

                    if (!currentAlbum.getCoverImage().equals(getString(R.string.DEFAULT_ALBUM_COVER_IMAGE))) {
                        Glide.with(appContext).load(currentAlbum.getCoverImage()).into(albumCoverImg);
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
            CropImage.activity(albumCoverUri).setAspectRatio(2, 1).start(this);
            coverBtnLayout.setVisibility(View.VISIBLE);
            addCoverBtn.setVisibility(View.GONE);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            Uri croppedImageUri = result.getUri();
            Glide.with(this).load(croppedImageUri).into(albumCoverImg);
            File croppedImageFile = new File(croppedImageUri.getPath());

            try {
                Bitmap compressedImageBitmap = new Compressor(this)
                        .setMaxHeight(200)
                        .setMaxWidth(200)
                        .setQuality(50)
                        .compressToBitmap(croppedImageFile);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageBitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
                croppedImageBytes = baos.toByteArray();


            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            albumCoverUri = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.edit_album_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_album_delete) {
            deleteAlbum();
        }

        if (item.getItemId() == R.id.menu_album_change_save) {
            editAlbum();
        }

        return true;
    }

    public void editAlbum() {

        albumName = albumNameText.getText().toString().trim();
        albumDesc = albumDescText.getText().toString().trim();

        if (albumName.isEmpty()) {
            albumNameText.setError("New Album Name Required");
        } else {
            if (albumDesc.isEmpty())
                albumDesc = getString(R.string.DEFAULT_ALBUM_DESCRIPTION);

            if (albumCoverUri != null) {
                editAlbumProgressBar.show();

                UploadTask AlbumCoverUpload = AppData.getAlbumsStorageRef().child(AppData.getCurrentUserId()).child(String.valueOf(albumId))
                        .child("coverImage").putBytes(croppedImageBytes);

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
                        AppData.getAlbumsStorageRef().child(AppData.getCurrentUserId()).child(String.valueOf(albumId))
                                .child("coverImage").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Map<String, Object> NewAlbumMap = new HashMap<>();
                                NewAlbumMap.put(AppData.getCurrentUserId() + "/" + albumId + "/name", albumName);
                                NewAlbumMap.put(AppData.getCurrentUserId() + "/" + albumId + "/description", albumDesc);
                                NewAlbumMap.put(AppData.getCurrentUserId() + "/" + albumId + "/coverImage", uri.toString());

                                AppData.getAlbumsDataRef().updateChildren(NewAlbumMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        close(null);
                                    }
                                });
                            }
                        });
                    }
                });
            } else {

                Map<String, Object> NewAlbumMap = new HashMap<>();
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
        AppData.getAlbumsDataRef().child(albumOwnerId).child(albumId).child("coverImage")
                .setValue(getString(R.string.DEFAULT_ALBUM_COVER_IMAGE)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AppData.getAlbumsStorageRef().child(albumOwnerId).child(albumId).child("coverImage").delete();
                addCoverBtn.setVisibility(View.VISIBLE);
                coverBtnLayout.setVisibility(View.GONE);
            }
        });
    }

    public void addCover(View view) {
        Intent addAlbumCoverGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(addAlbumCoverGalleryIntent, ADD_ALBUM_COVER_REQUEST);
    }

    public void deleteAlbum() {

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

                        close(null);

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

    public void editCover(View view) {

        Intent editAlbumCoverGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(editAlbumCoverGalleryIntent, ADD_ALBUM_COVER_REQUEST);
    }
}
