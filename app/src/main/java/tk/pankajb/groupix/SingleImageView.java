package tk.pankajb.groupix;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SingleImageView extends AppCompatActivity {

    CircleImageView UserProfileImage;
    TextView UserName;
    TextView AlbumName;
    ImageButton CloseButton;
    ImageView ImageView;
    ImageButton DownloadButton;
    ImageButton DeleteButton;
    ImageButton ShareButton;

    String ImageId;
    String Type;
    String AlbumId;
    String OwnerId;

    DataStore AppData = new DataStore();
    ActionHandler AppAction = new ActionHandler(SingleImageView.this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_image_view);

        UserProfileImage = findViewById(R.id.SingleImageview_UserProfileImage);
        UserName = findViewById(R.id.SingleImageview_UserName_Text);
        AlbumName = findViewById(R.id.SingleImageview_ImageAlbum_Text);
        CloseButton = findViewById(R.id.SingleImageView_CloseButton);
        ImageView = findViewById(R.id.SingleImageView_Image);
        DownloadButton = findViewById(R.id.SingleImageView_DownlaodButton);
        DeleteButton = findViewById(R.id.SingleImageView_DeleteButton);
        ShareButton = findViewById(R.id.SingleImageView_ShareButton);

        ImageId = getIntent().getStringExtra("ImageId");
        Type = getIntent().getStringExtra("Type");

        CloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (Type.equals("Single")) {
            AlbumName.setVisibility(View.GONE);

            AppData.getImagesDataRef().child("AllImages").child(ImageId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    OwnerId = dataSnapshot.getValue(String.class);

                    AppData.getUsersDataRef().child(OwnerId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String userName = dataSnapshot.child("Name").getValue(String.class) + " " + dataSnapshot.child("LastName").getValue(String.class);
                            UserName.setText(userName);
                            Glide.with(getApplicationContext()).load((String) dataSnapshot.child("ProfileThumbImage").getValue(String.class)).into(UserProfileImage);
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

            AppData.getAlbumsDataRef().child("AllImages").child(ImageId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    OwnerId = (String) dataSnapshot.getValue();

                    AppData.getUsersDataRef().child(OwnerId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String userName = dataSnapshot.child("Name").getValue() + " " + dataSnapshot.child("LastName").getValue();
                            UserName.setText(userName);
                            Glide.with(getApplicationContext()).load((String) dataSnapshot.child("ProfileThumbImage").getValue()).into(UserProfileImage);
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
    protected void onStart() {
        super.onStart();

        if (Type.equals("Single")) {
            AppData.getImagesDataRef().child("AllImages").child(ImageId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    OwnerId = (String) dataSnapshot.getValue();

                    DownloadButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AppAction.DownloadSingleImage(ImageId, OwnerId);
                        }
                    });

                    DeleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AppAction.DeleteSingleImage(ImageId, OwnerId);
                            finish();
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else if (Type.equals("Album")) {
            AppData.getAlbumsDataRef().child("AllAlbums").child(AlbumId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    OwnerId = (String) dataSnapshot.getValue();

                    DownloadButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AppAction.DownloadSingleImage(AlbumId, ImageId, OwnerId);
                        }
                    });

                    DeleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AppAction.DeleteSingleImage(AlbumId, ImageId, OwnerId);
                            finish();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
}
