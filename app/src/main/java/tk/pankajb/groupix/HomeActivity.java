package tk.pankajb.groupix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import tk.pankajb.groupix.Home.SectionAdapter;
import tk.pankajb.groupix.album.CreateAlbum;
import tk.pankajb.groupix.handlers.ActionHandler;
import tk.pankajb.groupix.handlers.DataStore;

public class HomeActivity extends AppCompatActivity {

    private final short SINGLE_IMAGE_UPLOAD_REQUEST = 1;

    private CircleImageView userProf;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private ProgressDialog ImageUploadProgressBar;

    private DataStore AppData = new DataStore();
    private ActionHandler handler = new ActionHandler(HomeActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar mToolBar = findViewById(R.id.Home_Toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        userProf = findViewById(R.id.Home_Toolbar_Image);

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

        AppData.getUsersDataRef().child(AppData.getCurrentUserId()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String CurrentUserProfileThumb = dataSnapshot.child("profileThumbImage").getValue(String.class);
                if (!CurrentUserProfileThumb.equals(getString(R.string.DEFAULT_USER_PROFILE_THUMB))) {
                    Glide.with(getApplicationContext()).load(CurrentUserProfileThumb).into(userProf);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void addBtnClicked(View view) {

        switch (mTabLayout.getSelectedTabPosition()) {
            case 0:
                addSingleImage();
                break;
            case 1:
                addAlbum();
                break;
        }
    }

    public void editProfile(View view) {
        SendToEditProfile();
    }

    private void addAlbum() {
        Intent addAlbum = new Intent(HomeActivity.this, CreateAlbum.class);
        startActivity(addAlbum);
    }

    private void addSingleImage() {

        Intent addSingleImageGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(addSingleImageGalleryIntent, SINGLE_IMAGE_UPLOAD_REQUEST);
    }

    private void SendToEditProfile() {
        Intent EditProfileIntent = new Intent(HomeActivity.this, EditProfileActivity.class);
        startActivity(EditProfileIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SINGLE_IMAGE_UPLOAD_REQUEST & resultCode == RESULT_OK) {
            Uri inputImage = data.getData();

            handler.uploadSingleImage(AppData.getCurrentUserId(), inputImage);
        }
    }
}
