package tk.pankajb.groupix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

public class AlbumOverview extends AppCompatActivity {

    Toolbar OverviewToolbar;
    ImageView AlbumCoverImageView;
    TextView AlbumNameTextView;
    TextView AlbumDescTextView;
    RecyclerView AlbumImagesRecycler;
    FloatingActionButton AlbumAddImgBtn;

    FirebaseUser CurrentUser;
    DatabaseReference AlbumsDataRef;

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

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        AlbumsDataRef = FirebaseDatabase.getInstance().getReference().child("albums");

        setSupportActionBar(OverviewToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AlbumImagesRecycler.setHasFixedSize(true);
        AlbumImagesRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        AlbumId = getIntent().getStringExtra("AlbumId");

    }

    @Override
    protected void onStart() {
        super.onStart();

//      setAddBtn();

        AppData.getAlbumsDataRef().child("allalbums").child(AlbumId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                AlbumOwnerId = dataSnapshot.getValue().toString();

                AppData.getAlbumsDataRef().child(AlbumOwnerId).child(AlbumId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (AppData.getCurrentUserId().equals(AlbumOwnerId)) {
                            AlbumAddImgBtn.setVisibility(View.VISIBLE);
                        }

                        AlbumNameTextView.setText(dataSnapshot.child("name").getValue().toString());
                        AlbumDescTextView.setText(dataSnapshot.child("description").getValue().toString());

                        if (!dataSnapshot.child("coverimg").getValue().toString().isEmpty()) {
                            Glide.with(getApplicationContext()).load(dataSnapshot.child("coverimg").getValue().toString()).into(AlbumCoverImageView);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Query AlbumImagesQuery = AppData.getAlbumsDataRef().child(AlbumOwnerId).child(AlbumId).child("images").limitToLast(50);

                FirebaseRecyclerOptions<ImageDataModel> AlbumImagesOptions = new FirebaseRecyclerOptions.Builder<ImageDataModel>().setQuery(AlbumImagesQuery, ImageDataModel.class).build();

                ImagesAdapter = new ImagesRecyclerAdapter(AlbumImagesOptions, getApplicationContext());

                AlbumImagesRecycler.setAdapter(ImagesAdapter);

                ImagesAdapter.startListening();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();

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
            EditAlbum();
        }

        return true;
    }

    void setAddBtn() {

        AlbumAddImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent addAlbumImgGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(addAlbumImgGalleryIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 & resultCode == RESULT_OK) {

            InputImage = data.getData();
            NewImageId = System.currentTimeMillis();

            UploadTask OriginalImageUpload = AppData.getAlbumsStorageRef().child(AppData.getCurrentUserId()).child(AlbumId).child("images").child(String.valueOf(NewImageId)).putFile(InputImage);


            OriginalImageUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String OriginalImageUrl = taskSnapshot.getDownloadUrl().toString();

                    AppData.getAlbumsDataRef().child(AppData.getCurrentUserId()).child(AlbumId).child("images").child(String.valueOf(NewImageId)).setValue(OriginalImageUrl);
                    AppData.getAlbumsDataRef().child("allimages").child(String.valueOf(NewImageId)).setValue(AppData.getCurrentUserId());
                }
            });

            OriginalImageUpload.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AlbumOverview.this, "Image upload failed", Toast.LENGTH_SHORT).show();

                    Log.e("Image Upload Error", e.toString());
                }
            });
        }
    }

    private void EditAlbum() {
        Toast.makeText(this, "Edit Album Selected", Toast.LENGTH_SHORT).show();
    }
}
