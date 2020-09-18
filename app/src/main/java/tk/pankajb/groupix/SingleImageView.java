package tk.pankajb.groupix;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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

public class SingleImageView extends AppCompatActivity {

    private CircleImageView UserProfileImage;
    private TextView UserName;
    private TextView AlbumName;
    private ImageView ImageView;

    private String ImageId;
    private String Type;
    private String AlbumId;
    private String OwnerId;

    private DataStore AppData = new DataStore();
    private ActionHandler AppAction = new ActionHandler(SingleImageView.this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_image_view);

        Toolbar viewToolbar = findViewById(R.id.SingleImageView_Toolbar);
        UserProfileImage = findViewById(R.id.SingleImageview_UserProfileImage);
        UserName = findViewById(R.id.SingleImageview_UserName_Text);
        AlbumName = findViewById(R.id.SingleImageview_ImageAlbum_Text);
        ImageView = findViewById(R.id.SingleImageView_Image);

        ImageId = getIntent().getStringExtra("ImageId");
        Type = getIntent().getStringExtra("Type");
        OwnerId = getIntent().getStringExtra("OwnerId");

        setSupportActionBar(viewToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        if (Type.equals("Single")) {
            AlbumName.setVisibility(View.GONE);

            AppData.getImagesDataRef().child("AllImages").child(ImageId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    OwnerId = dataSnapshot.getValue(String.class);

                    AppData.getUsersDataRef().child(OwnerId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String userName = dataSnapshot.child("name").getValue(String.class) + " " + dataSnapshot.child("lastName").getValue(String.class);
                            UserName.setText(userName);
                            Glide.with(getApplicationContext()).load((String) dataSnapshot.child("profileThumbImage").getValue(String.class)).into(UserProfileImage);
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

        } else if (Type.equals("Album")) {
            AlbumName.setVisibility(View.VISIBLE);
            AlbumId = getIntent().getStringExtra("AlbumId");
            Log.d("Image id", "Image id is - " + ImageId);
            AppData.getAlbumsDataRef().child("AllImages").child(ImageId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    OwnerId = dataSnapshot.getValue(String.class);

                    Log.d("OwnerId", "id is:- " + dataSnapshot.exists());

                    AppData.getUsersDataRef().child(OwnerId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String userName = dataSnapshot.child("name").getValue() + " " + dataSnapshot.child("lastName").getValue();
                            UserName.setText(userName);
                            Glide.with(getApplicationContext()).load((String) dataSnapshot.child("profileThumbImage").getValue()).into(UserProfileImage);
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

        if (Type.equals("Single")) {
            AppAction.DeleteSingleImage(ImageId, OwnerId);
            finish();
        } else if (Type.equals("Album")) {
            AppAction.DeleteSingleImage(ImageId, OwnerId, AlbumId);
            finish();
        }
    }

    public void downloadImage(View view) {

        if (Type.equals("Single")) {
            AppAction.DownloadSingleImage(ImageId, OwnerId);
        } else if (Type.equals("Album")) {
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
