package tk.pankajb.groupix.Home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import tk.pankajb.groupix.Album.CreateAlbum;
import tk.pankajb.groupix.DataStore;
import tk.pankajb.groupix.EditProfile;
import tk.pankajb.groupix.R;


public class HomeActivity extends AppCompatActivity {

    Toolbar mToolBar;
    CircleImageView userProf;
    FloatingActionButton AddBtn;
    TabLayout mTabLayout;
    ViewPager mViewPager;

    ProgressDialog ImageUploadProgressBar;

    Long ImageId;
    Uri InputImage;

    DataStore AppData = new DataStore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolBar = findViewById(R.id.Home_Toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        userProf = findViewById(R.id.Home_Toolbar_Image);
        AddBtn = findViewById(R.id.MainAddButton);

        mViewPager = findViewById(R.id.Home_ViewPager);
        SectionAdapter mSectionAdapter = new SectionAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionAdapter);

        mTabLayout = findViewById(R.id.Home_TabPager);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.image);
        mTabLayout.getTabAt(1).setIcon(R.drawable.album);

        ImageUploadProgressBar = new ProgressDialog(HomeActivity.this);
        ImageUploadProgressBar.setTitle("Uploading Image");


    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckCredentials();

        setAddBtn();


        userProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendToEditProfile();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 & resultCode == RESULT_OK) {
            InputImage = data.getData();

            ImageId = System.currentTimeMillis();

            ImageUploadProgressBar.show();

            UploadTask OriginalImageUpload = AppData.getImagesStorageRef().child(AppData.getCurrentUserId()).child(String.valueOf(ImageId)).child("image").putFile(InputImage);

            OriginalImageUpload.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    long ProgressDone = 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                    ImageUploadProgressBar.setMessage("Uploading Image " + ProgressDone + "%");
                }
            });
            OriginalImageUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String OriginalImageUrl = String.valueOf(taskSnapshot.getDownloadUrl());

                    AppData.getImagesDataRef().child(AppData.getCurrentUserId()).child(String.valueOf(ImageId)).child("image").setValue(OriginalImageUrl);
                    AppData.getImagesDataRef().child("AllImages").child(ImageId.toString()).setValue(AppData.getCurrentUserId());
                    ImageUploadProgressBar.dismiss();
                }
            });

            OriginalImageUpload.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HomeActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();

                    Log.e("Image Upload Error", e.toString());
                }
            });
        }
    }

    private void CheckCredentials() {

        AppData.getUsersDataRef().child(AppData.getCurrentUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String CurrentUserProfileThumb = dataSnapshot.child("ProfileThumbImage").getValue(String.class);
                if (!CurrentUserProfileThumb.equals("default")) {
                    Glide.with(HomeActivity.this).load(CurrentUserProfileThumb).into(userProf);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setAddBtn() {

        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (mTabLayout.getSelectedTabPosition()) {
                    case 0:
                        addSingleImage();
                        break;
                    case 1:
                        addAlbum();
                        break;
                }
            }
        });
    }

    private void addAlbum() {
        Intent addAlbum = new Intent(HomeActivity.this, CreateAlbum.class);
        startActivity(addAlbum);
    }

    private void addSingleImage() {

        Intent addSingleImageGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(addSingleImageGalleryIntent, 1);
    }

    private void SendToEditProfile() {
        Intent EditProfileIntent = new Intent(HomeActivity.this, EditProfile.class);
        startActivity(EditProfileIntent);
    }
}
