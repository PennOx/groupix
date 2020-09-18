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
import tk.pankajb.groupix.models.Album;

public class AlbumOverviewActivity extends AppCompatActivity {

    Toolbar overviewToolbar;
    ImageView albumCoverImageView;
    TextView albumNameTextView;
    TextView albumDescTextView;
    RecyclerView albumImagesRecycler;
    FloatingActionButton albumAddImgBtn;

    FirebaseRecyclerAdapter ImagesAdapter;

    DataStore AppData = new DataStore();
    ActionHandler handler = new ActionHandler(AlbumOverviewActivity.this);

    String AlbumId;
    String AlbumOwnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_overview);

        overviewToolbar = findViewById(R.id.AlbumOverview_Toolbar);
        albumCoverImageView = findViewById(R.id.AlbumOverview_AlbumCover);
        albumNameTextView = findViewById(R.id.AlbumOverview_AlbumName);
        albumDescTextView = findViewById(R.id.AlbumOverview_AlbumDesc);
        albumImagesRecycler = findViewById(R.id.AlbumOverview_ImagesRecycler);
        albumAddImgBtn = findViewById(R.id.AlbumOverview_AddImagesBtn);

        setSupportActionBar(overviewToolbar);
        getSupportActionBar().setTitle(getString(R.string.ALBUM_OVERVIEW_TITLE));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        overviewToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        albumImagesRecycler.setHasFixedSize(true);
        albumImagesRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        AlbumId = getIntent().getStringExtra(getString(R.string.ALBUM_ID_INTENT));
        AlbumOwnerId = getIntent().getStringExtra(getString(R.string.ALBUM_OWNER_ID_INTENT));


        AppData.getAlbumsDataRef().child(AlbumOwnerId).child(AlbumId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Album currentAlbum = dataSnapshot.getValue(Album.class);

                    if (AppData.getCurrentUserId().equals(AlbumOwnerId)) {
                        albumAddImgBtn.setVisibility(View.VISIBLE);
                    }

                    albumNameTextView.setText(currentAlbum.getName());
                    albumDescTextView.setText(currentAlbum.getDescription());
                    if (!currentAlbum.getCoverImage().equals(getString(R.string.DEFAULT_ALBUM_COVER_IMAGE))) {
                        Glide.with(getApplicationContext()).load(currentAlbum.getCoverImage()).into(albumCoverImageView);
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
        albumImagesRecycler.setAdapter(ImagesAdapter);
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
            sendToEditAlbum.putExtra(getString(R.string.ALBUM_ID_INTENT), AlbumId);
            sendToEditAlbum.putExtra(getString(R.string.ALBUM_OWNER_ID_INTENT), AlbumOwnerId);
            startActivity(sendToEditAlbum);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == getResources().getInteger(R.integer.ALBUM_IMAGE_SELECTION) && resultCode == RESULT_OK) {

            Uri inputImage = data.getData();
            handler.uploadAlbumImage(AppData.getCurrentUserId(), AlbumId, inputImage);
        }

    }

    public void addImage(View view) {
        Intent addAlbumImgGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(addAlbumImgGalleryIntent, getResources().getInteger(R.integer.ALBUM_IMAGE_SELECTION));
    }
}
