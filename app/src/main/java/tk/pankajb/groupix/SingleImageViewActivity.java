package tk.pankajb.groupix;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import tk.pankajb.groupix.handlers.ActionHandler;
import tk.pankajb.groupix.handlers.DataStore;
import tk.pankajb.groupix.models.User;

public class SingleImageViewActivity extends AppCompatActivity {

    private CircleImageView userProfileImage;
    private TextView userName;
    private TextView albumName;
    private ImageView imageView;

    private String imageId;
    private String type;
    private String albumId;
    private String ownerId;

    private DataStore appData = new DataStore();
    private ActionHandler appAction = new ActionHandler(SingleImageViewActivity.this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_image_view);

        Toolbar viewToolbar = findViewById(R.id.SingleImageView_Toolbar);
        userProfileImage = findViewById(R.id.SingleImageview_UserProfileImage);
        userName = findViewById(R.id.SingleImageview_UserName_Text);
        albumName = findViewById(R.id.SingleImageview_ImageAlbum_Text);
        imageView = findViewById(R.id.SingleImageView_Image);

        imageId = getIntent().getStringExtra(getString(R.string.IMAGE_ID_INTENT));
        type = getIntent().getStringExtra(getString(R.string.IMAGE_TYPE_INTENT));
        ownerId = getIntent().getStringExtra(getString(R.string.IMAGE_OWNER_ID_INTENT));

        setSupportActionBar(viewToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        if (type.equals(getString(R.string.SINGLE_IMAGE))) {

            albumName.setVisibility(View.GONE);

            appData.getImagesDataRef().child("AllImages").child(imageId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ownerId = dataSnapshot.getValue(String.class);

                    appData.getUsersDataRef().child(ownerId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User imageOwner = dataSnapshot.getValue(User.class);

                            userName.setText(imageOwner.getFullName());
                            Glide.with(getApplicationContext()).load(imageOwner.getProfileThumbImage()).into(userProfileImage);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    appData.getImagesDataRef().child(ownerId).child(imageId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Glide.with(getApplication()).load((String) dataSnapshot.child("image").getValue()).into(imageView);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else if (type.equals(getString(R.string.ALBUM_IMAGE))) {
            albumName.setVisibility(View.VISIBLE);
            albumId = getIntent().getStringExtra(getString(R.string.IMAGE_ALBUM_ID_INTENT));

            appData.getAlbumsDataRef().child("AllImages").child(imageId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ownerId = dataSnapshot.getValue(String.class);

                    appData.getUsersDataRef().child(ownerId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            User imageOwner = dataSnapshot.getValue(User.class);

                            userName.setText(imageOwner.getFullName());
                            Glide.with(getApplicationContext()).load(imageOwner.getProfileThumbImage()).into(userProfileImage);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    appData.getAlbumsDataRef().child(ownerId).child(albumId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            albumName.setText((String) dataSnapshot.child("name").getValue());
                            Glide.with(getApplicationContext()).load((String) dataSnapshot.child("images").child(imageId).child("image").getValue()).into(imageView);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.singleimage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_close_singleimage) {
            close();
        }
        return true;
    }

    public void close() {
        finish();
    }

    public void deleteImage(View view) {

        if (type.equals(getString(R.string.SINGLE_IMAGE))) {
            appAction.DeleteSingleImage(imageId, ownerId);
            finish();
        } else if (type.equals(getString(R.string.ALBUM_IMAGE))) {
            appAction.DeleteSingleImage(imageId, ownerId, albumId);
            finish();
        }
    }

    public void downloadImage(View view) {

        if (type.equals(getString(R.string.SINGLE_IMAGE))) {
            appAction.DownloadSingleImage(imageId, ownerId);
        } else if (type.equals(getString(R.string.ALBUM_IMAGE))) {
            appAction.DownloadSingleImage(imageId, ownerId, albumId);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == R.integer.STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
}
