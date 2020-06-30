package tk.pankajb.groupix;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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
    ActionHandler AppAction = new ActionHandler();

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

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

        getWindow().setBackgroundDrawableResource(R.color.cardview_dark_background);


        WindowManager.LayoutParams Parameters = getWindow().getAttributes();
        Parameters.gravity = Gravity.CENTER;
        Parameters.x = 0;
        Parameters.y = -20;
        getWindow().setAttributes(Parameters);

        CloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (Type.equals("Single")) {
            AlbumName.setVisibility(View.GONE);

            AppData.getImagesDataRef().child("allimages").child(ImageId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    OwnerId = (String) dataSnapshot.getValue();

                    AppData.getVerifiedUserDataRef().child(OwnerId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserName.setText(dataSnapshot.child("name").getValue() + " " + dataSnapshot.child("lastname").getValue());

                            Glide.with(getApplicationContext()).load((String) dataSnapshot.child("profileThumbImage").getValue()).into(UserProfileImage);
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

            AppData.getAlbumsDataRef().child("allimages").child(ImageId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    OwnerId = (String) dataSnapshot.getValue();

                    AppData.getVerifiedUserDataRef().child(OwnerId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserName.setText(dataSnapshot.child("name").getValue() + " " + dataSnapshot.child("lastname").getValue());

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
    protected void onStart() {
        super.onStart();

        if (Type.equals("Single")) {
            AppData.getImagesDataRef().child("allimages").child(ImageId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    OwnerId = (String) dataSnapshot.getValue();

                    DownloadButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AppAction.DownloadSingleImage(ImageId, OwnerId, getApplicationContext());
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
        }
    }
}
