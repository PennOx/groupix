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
import tk.pankajb.groupix.models.User;

public class HomeActivity extends AppCompatActivity {

    private CircleImageView userProf;
    private TabLayout mTabLayout;

    private DataStore appData = new DataStore();
    private ActionHandler handler = new ActionHandler(HomeActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar mToolBar = findViewById(R.id.Home_Toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(R.string.HOME_TITLE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        userProf = findViewById(R.id.Home_Toolbar_Image);

        ViewPager mViewPager = findViewById(R.id.Home_ViewPager);
        SectionAdapter mSectionAdapter = new SectionAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionAdapter);

        mTabLayout = findViewById(R.id.Home_TabPager);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.image);
        mTabLayout.getTabAt(1).setIcon(R.drawable.album);

        ProgressDialog imageUploadProgressBar = new ProgressDialog(HomeActivity.this);
        imageUploadProgressBar.setTitle(getString(R.string.UPLOAD_IMAGE_TITLE));

    }

    @Override
    protected void onStart() {
        super.onStart();

        appData.getUsersDataRef().child(appData.getCurrentUserId()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User currentUser = dataSnapshot.getValue(User.class);

                if (!currentUser.getProfileThumbImage().equals(getString(R.string.DEFAULT_USER_PROFILE_THUMB))) {
                    Glide.with(getApplicationContext()).load(currentUser.getProfileThumbImage()).into(userProf);
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
        startActivityForResult(addSingleImageGalleryIntent, getResources().getInteger(R.integer.ADD_IMAGE));
    }

    private void SendToEditProfile() {
        Intent EditProfileIntent = new Intent(HomeActivity.this, EditProfileActivity.class);
        startActivity(EditProfileIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == getResources().getInteger(R.integer.ADD_IMAGE) & resultCode == RESULT_OK) {
            Uri inputImage = data.getData();

            handler.uploadSingleImage(appData.getCurrentUserId(), inputImage);
        }
    }
}
