package tk.pankajb.groupix.Album;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import tk.pankajb.groupix.DataStore;
import tk.pankajb.groupix.R;

public class CreateAlbum extends AppCompatActivity {

    final private short ADD_COVER_REQUEST = 1;
    final private long ALBUM_ID = System.currentTimeMillis();

    Toolbar createAlbumToolbar;
    TextView addCoverBtn;
    ImageView albumCoverImg;
    EditText albumNameText;
    EditText albumDescText;
    Uri albumCoverUri;
    String albumName;
    String albumDesc;
    String albumCoverLink;
    DataStore AppData = new DataStore();
    private ProgressDialog createAlbumProgressBar;

    byte[] croppedImageBytes = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_album);

        createAlbumToolbar = findViewById(R.id.CreateAlbum_Toolbar);
        addCoverBtn = findViewById(R.id.CreateAlbum_AddCoverButton);
        albumCoverImg = findViewById(R.id.CreateAlbum_CoverImg);
        albumNameText = findViewById(R.id.CreateAlbum_AlbumName);
        albumDescText = findViewById(R.id.CreateAlbum_AlbumDescription);

        setSupportActionBar(createAlbumToolbar);
        getSupportActionBar().setTitle("Create album");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        createAlbumToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        createAlbumProgressBar = new ProgressDialog(CreateAlbum.this);
        createAlbumProgressBar.setTitle("Creating album");
        createAlbumProgressBar.setCanceledOnTouchOutside(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_COVER_REQUEST && resultCode == RESULT_OK) {
            albumCoverUri = data.getData();
            CropImage.activity(albumCoverUri).setAspectRatio(2, 1).start(this);
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
        getMenuInflater().inflate(R.menu.create_album_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_create_album) {
            createAlbum();
        }

        return true;
    }

    public void createAlbum() {

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
                        .child("coverimg").putBytes(croppedImageBytes);

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
                        AppData.getAlbumsStorageRef().child(AppData.getCurrentUserId()).child(String.valueOf(ALBUM_ID))
                                .child("coverimg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Map<String, Object> NewAlbumMap = new HashMap<>();
                                NewAlbumMap.put(AppData.getCurrentUserId() + "/" + ALBUM_ID + "/name", albumName);
                                NewAlbumMap.put(AppData.getCurrentUserId() + "/" + ALBUM_ID + "/description", albumDesc);
                                NewAlbumMap.put(AppData.getCurrentUserId() + "/" + ALBUM_ID + "/coverimg", uri.toString());
                                NewAlbumMap.put("AllAlbums/" + ALBUM_ID, AppData.getCurrentUserId());

                                AppData.getAlbumsDataRef().updateChildren(NewAlbumMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });
            } else {

                Map<String, Object> NewAlbumMap = new HashMap<>();
                NewAlbumMap.put(AppData.getCurrentUserId() + "/" + ALBUM_ID + "/name", albumName);
                NewAlbumMap.put(AppData.getCurrentUserId() + "/" + ALBUM_ID + "/description", albumDesc);
                NewAlbumMap.put(AppData.getCurrentUserId() + "/" + ALBUM_ID + "/coverimg", albumCoverLink);
                NewAlbumMap.put("AllAlbums/" + ALBUM_ID, AppData.getCurrentUserId());

                AppData.getAlbumsDataRef().updateChildren(NewAlbumMap).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        finish();
                    }
                });
            }
        }

    }

    public void addCover(View view) {
        Intent addAlbumCoverGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(addAlbumCoverGalleryIntent, ADD_COVER_REQUEST);
    }

}
