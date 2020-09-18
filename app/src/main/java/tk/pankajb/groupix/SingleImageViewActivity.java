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

    private CircleImageView UserProfileImage;
    private TextView UserName;
    private TextView AlbumName;
    private ImageView ImageView;

    private String ImageId;
    private String Type;
    private String AlbumId;
    private String OwnerId;

    private DataStore AppData = new DataStore();
    private ActionHandler AppAction = new ActionHandler(SingleImageViewActivity.this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_image_view);

        Toolbar viewToolbar = findViewById(R.id.SingleImageView_Toolbar);
        UserProfileImage = findViewById(R.id.SingleImageview_UserProfileImage);
        UserName = findViewById(R.id.SingleImageview_UserName_Text);
        AlbumName = findViewById(R.id.SingleImageview_ImageAlbum_Text);
        ImageView = findViewById(R.id.SingleImageView_Image);

        ImageId = getIntent().getStringExtra(getString(R.string.IMAGE_ID_INTENT));
        Type = getIntent().getStringExtra(getString(R.string.IMAGE_TYPE_INTENT));
        OwnerId = getIntent().getStringExtra(getString(R.string.IMAGE_OWNER_ID_INTENT));

        setSupportActionBar(viewToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        if (Type.equals(getString(R.string.SINGLE_IMAGE))) {

            AlbumName.setVisibility(View.GONE);

            AppData.getImagesDataRef().child("AllImages").child(ImageId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    OwnerId = dataSnapshot.getValue(String.class);

                    AppData.getUsersDataRef().child(OwnerId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User imageOwner = dataSnapshot.getValue(User.class);

                            UserName.setText(imageOwner.getFullName());
                            Glide.with(getApplicationContext()).load(imageOwner.getProfileThumbImage()).into(UserProfileImage);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    AppData.getImagesDataRef().child(OwnerId).child(ImageId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Glide.with(getApplication()).load((String) dataSnapshot.child("image").getValue()).into(ImageView);
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

        } else if (Type.equals(getString(R.string.ALBUM_IMAGE))) {
            AlbumName.setVisibility(View.VISIBLE);
            AlbumId = getIntent().getStringExtra(getString(R.string.IMAGE_ALBUM_ID_INTENT));

            AppData.getAlbumsDataRef().child("AllImages").child(ImageId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    OwnerId = dataSnapshot.getValue(String.class);

                    AppData.getUsersDataRef().child(OwnerId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            User imageOwner = dataSnapshot.getValue(User.class);

                            UserName.setText(imageOwner.getFullName());
                            Glide.with(getApplicationContext()).load(imageOwner.getProfileThumbImage()).into(UserProfileImage);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    AppData.getAlbumsDataRef().child(OwnerId).child(AlbumId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            AlbumName.setText((String) dataSnapshot.child("name").getValue());
                            Glide.with(getApplicationContext()).load((String) dataSnapshot.child("images").child(ImageId).child("image").getValue()).into(ImageView);
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

        if (Type.equals(getString(R.string.SINGLE_IMAGE))) {
            AppAction.DeleteSingleImage(ImageId, OwnerId);
            finish();
        } else if (Type.equals(getString(R.string.ALBUM_IMAGE))) {
            AppAction.DeleteSingleImage(ImageId, OwnerId, AlbumId);
            finish();
        }
    }

    public void downloadImage(View view) {

        if (Type.equals(getString(R.string.SINGLE_IMAGE))) {
            AppAction.DownloadSingleImage(ImageId, OwnerId);
        } else if (Type.equals(getString(R.string.ALBUM_IMAGE))) {
            AppAction.DownloadSingleImage(ImageId, OwnerId, AlbumId);
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
