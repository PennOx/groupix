package tk.pankajb.groupix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import tk.pankajb.groupix.album.EditAlbum;
import tk.pankajb.groupix.handlers.ActionHandler;
import tk.pankajb.groupix.handlers.DataStore;
import tk.pankajb.groupix.image.ImageDataModel;
import tk.pankajb.groupix.image.ImagesRecyclerAdapter;

public class AlbumOverviewActivity extends AppCompatActivity {

    Toolbar OverviewToolbar;
    ImageView AlbumCoverImageView;
    TextView AlbumNameTextView;
    TextView AlbumDescTextView;
    RecyclerView AlbumImagesRecycler;
    FloatingActionButton AlbumAddImgBtn;

    final private int ALBUM_IMAGE_REQUEST = 1;

    FirebaseRecyclerAdapter ImagesAdapter;

    DataStore AppData = new DataStore();
    ActionHandler handler = new ActionHandler(AlbumOverviewActivity.this);

    String AlbumId;
    String AlbumOwnerId;

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
                    if (!dataSnapshot.child("coverImage").getValue(String.class).equals(getString(R.string.DEFAULT_ALBUM_COVER_IMAGE))) {
                        Glide.with(getApplicationContext()).load(dataSnapshot.child("coverImage").getValue(String.class)).into(AlbumCoverImageView);
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
            Intent sendToEditAlbum = new Intent(AlbumOverviewActivity.this, EditAlbum.class);
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

            Uri inputImage = data.getData();
            handler.uploadAlbumImage(AppData.getCurrentUserId(), AlbumId, inputImage);
        }

    }

    public void addImage(View view) {
        Intent addAlbumImgGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(addAlbumImgGalleryIntent, ALBUM_IMAGE_REQUEST);
    }
}
