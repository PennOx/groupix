package tk.pankajb.groupix.Album;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import tk.pankajb.groupix.DataStore;
import tk.pankajb.groupix.Image.ImageDataModel;
import tk.pankajb.groupix.Image.ImagesRecyclerAdapter;
import tk.pankajb.groupix.R;

public class AlbumOverview extends AppCompatActivity {

    Toolbar OverviewToolbar;
    ImageView AlbumCoverImageView;
    TextView AlbumNameTextView;
    TextView AlbumDescTextView;
    RecyclerView AlbumImagesRecycler;
    FloatingActionButton AlbumAddImgBtn;
    ProgressDialog uploadingImageDialog;

    final private int ALBUM_IMAGE_REQUEST = 1;

    FirebaseRecyclerAdapter ImagesAdapter;

    DataStore AppData = new DataStore();

    String AlbumId;
    String AlbumOwnerId;

    Uri InputImage;
    Long NewImageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_overview);

        OverviewToolbar = findViewById(R.id.AlbumOverview_Toolbar);
        AlbumCoverImageView = findViewById(R.id.AlbumOverview_AlbumCover);
        AlbumNameTextView = findViewById(R.id.AlbumOverview_AlbumName);
        AlbumDescTextView = findViewById(R.id.AlbumOverview_AlbumDesc);
        AlbumImagesRecycler = findViewById(R.id.AlbumOverview_ImagesRecycler);
        AlbumAddImgBtn = findViewById(R.id.AlbumOverview_AddImagesBtn);

        setSupportActionBar(OverviewToolbar);
        getSupportActionBar().setTitle("Album");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uploadingImageDialog = new ProgressDialog(AlbumOverview.this);
        uploadingImageDialog.setCanceledOnTouchOutside(false);
        uploadingImageDialog.setTitle("Uploading Image");

        OverviewToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        AlbumImagesRecycler.setHasFixedSize(true);
        AlbumImagesRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        AlbumId = getIntent().getStringExtra("AlbumId");
        AlbumOwnerId = getIntent().getStringExtra("AlbumOwnerId");


        AppData.getAlbumsDataRef().child(AlbumOwnerId).child(AlbumId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    if (AppData.getCurrentUserId().equals(AlbumOwnerId)) {
                        AlbumAddImgBtn.setVisibility(View.VISIBLE);
                    }
                    AlbumNameTextView.setText(dataSnapshot.child("name").getValue(String.class));
                    AlbumDescTextView.setText(dataSnapshot.child("description").getValue(String.class));
                    if (!dataSnapshot.child("coverimg").getValue(String.class).equals("default")) {
                        Glide.with(getApplicationContext()).load(dataSnapshot.child("coverimg").getValue(String.class)).into(AlbumCoverImageView);
                    }

                } catch (NullPointerException e) {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Query AlbumImagesQuery = AppData.getAlbumsDataRef().child(AlbumOwnerId).child(AlbumId).child("images").limitToLast(50);
        FirebaseRecyclerOptions<ImageDataModel> AlbumImagesOptions = new FirebaseRecyclerOptions.Builder<ImageDataModel>().setQuery(AlbumImagesQuery, ImageDataModel.class).build();
        ImagesAdapter = new ImagesRecyclerAdapter(AlbumImagesOptions, getApplicationContext(), AlbumId, AlbumOwnerId);
        AlbumImagesRecycler.setAdapter(ImagesAdapter);
        ImagesAdapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.albumoverview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_editalbumbtn) {
            Intent sendToEditAlbum = new Intent(AlbumOverview.this, EditAlbum.class);
            sendToEditAlbum.putExtra("AlbumId", AlbumId);
            sendToEditAlbum.putExtra("AlbumOwnerId", AlbumOwnerId);
            startActivity(sendToEditAlbum);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ALBUM_IMAGE_REQUEST && resultCode == RESULT_OK) {

            uploadingImageDialog.show();

            InputImage = data.getData();
            NewImageId = System.currentTimeMillis();

            UploadTask OriginalImageUpload = AppData.getAlbumsStorageRef().child(AppData.getCurrentUserId()).child(AlbumId).child("images").child(String.valueOf(NewImageId)).child("image").putFile(InputImage);

            OriginalImageUpload.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    long ProgressDone = 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                    uploadingImageDialog.setMessage("Uploading Image " + ProgressDone + "%");
                }
            });

            OriginalImageUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    AppData.getAlbumsStorageRef().child(AppData.getCurrentUserId()).child(AlbumId).child("images").child(String.valueOf(NewImageId)).child("image").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            AppData.getAlbumsDataRef().child(AppData.getCurrentUserId()).child(AlbumId).child("images").child(String.valueOf(NewImageId)).child("image").setValue(uri.toString());
                            AppData.getAlbumsDataRef().child("AllImages").child(String.valueOf(NewImageId)).setValue(AppData.getCurrentUserId());

                            uploadingImageDialog.dismiss();
                        }
                    });
                }
            });

            OriginalImageUpload.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    uploadingImageDialog.dismiss();
                    Toast.makeText(AlbumOverview.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    Log.e("Image Upload Error", e.toString());
                }
            });
        }

    }

    public void addImage(View view) {
        Intent addAlbumImgGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(addAlbumImgGalleryIntent, ALBUM_IMAGE_REQUEST);
    }
}
