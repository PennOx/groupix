package tk.pankajb.groupix;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity {

    Toolbar mToolBar;

    CircleImageView UserProfileImg;
    TextView UserNameText;
    FloatingActionButton AddBtn;

    TabLayout mTabLayout;
    ViewPager mViewPager;

    ProgressDialog ImageUploadProgressBar;

    Dialog createAlbumDialog;

    String NewAlbumName;
    String NewAlbumDesc;
    String NewAlbumCoverImg;
    boolean NewAlbumHasCover;
    EditText NewAlbumNameLayout;
    EditText NewAlbumDescLayout;
    ImageView NewAlbumCoverImageView;
    ImageButton NewAlbumAddCoverButton;
    Button NewAlbumCloseDialog;
    Button NewAlbumCreateButton;
    Long NewAlbumId;
    Long ImageId;

    Uri InputImage;
    Uri CreateAlbumCoverUri;

    DataStore AppData = new DataStore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolBar = findViewById(R.id.Home_Toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(R.string.app_name);

        UserProfileImg = findViewById(R.id.Home_ProfileImage);
        UserNameText = findViewById(R.id.Home_UserName);
        AddBtn = findViewById(R.id.MainAddButton);

        mViewPager = findViewById(R.id.Home_ViewPager);
        SectionAdapter mSectionAdapter = new SectionAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionAdapter);

        mTabLayout = findViewById(R.id.Home_TabPager);
        mTabLayout.setupWithViewPager(mViewPager);

        ImageUploadProgressBar = new ProgressDialog(HomeActivity.this);
        ImageUploadProgressBar.setTitle("Uploading Image");

        createAlbumDialog = new Dialog(HomeActivity.this, android.R.style.Theme_Black_NoTitleBar);
        createAlbumDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        createAlbumDialog.setContentView(R.layout.create_album);
        createAlbumDialog.setCancelable(true);

        NewAlbumNameLayout = createAlbumDialog.getWindow().findViewById(R.id.CreateAlbum_AlbumName);
        NewAlbumDescLayout = createAlbumDialog.getWindow().findViewById(R.id.CreateAlbum_AlbumDescription);
        NewAlbumCoverImageView = createAlbumDialog.getWindow().findViewById(R.id.CreateAlbum_CoverImg);
        NewAlbumAddCoverButton = createAlbumDialog.getWindow().findViewById(R.id.CreateAlbum_AddCoverButton);
        NewAlbumCloseDialog = createAlbumDialog.getWindow().findViewById(R.id.CreateAlbum_CloseButton);
        NewAlbumCreateButton = createAlbumDialog.getWindow().findViewById(R.id.CreateAlbum_CreateAlbumButton);

    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckCredentials();

        setAddBtn();

        setCreateAlbumDialog();
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
                        NewAlbumHasCover = false;
                        Glide.with(HomeActivity.this).load(R.drawable.home_background).into(NewAlbumCoverImageView);
                        NewAlbumAddCoverButton.setVisibility(View.VISIBLE);
                        createAlbumDialog.show();
                        break;
                }
            }
        });
    }

    private void setCreateAlbumDialog() {

        NewAlbumAddCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAlbumCoverGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(addAlbumCoverGalleryIntent, 2);
            }
        });

        NewAlbumCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewAlbumNameLayout.setText("");
                NewAlbumDescLayout.setText("");
                createAlbumDialog.cancel();
            }
        });

        NewAlbumCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewAlbumName = NewAlbumNameLayout.getText().toString();
                NewAlbumDesc = NewAlbumDescLayout.getText().toString();
                NewAlbumCoverImg = "default";

                if (NewAlbumName.isEmpty()) {
                    NewAlbumNameLayout.setError("New Album Name Required");
                } else if (NewAlbumDesc.isEmpty()) {
                    NewAlbumDesc = "No Description";
                }

                if (!NewAlbumName.isEmpty()) {

                    NewAlbumId = System.currentTimeMillis();

                    if (NewAlbumHasCover) {
                        UploadTask AlbumCoverUpload = AppData.getAlbumsStorageRef().child(AppData.getCurrentUserId()).child(String.valueOf(NewAlbumId)).child("coverimg").putFile(CreateAlbumCoverUri);
                        AlbumCoverUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                NewAlbumCoverImg = taskSnapshot.getDownloadUrl().toString();
                            }
                        });
                    }

                    Map NewAlbumMap = new HashMap<>();
                    NewAlbumMap.put(AppData.getCurrentUserId() + "/" + NewAlbumId + "/name", NewAlbumName);
                    NewAlbumMap.put(AppData.getCurrentUserId() + "/" + NewAlbumId + "/description", NewAlbumDesc);
                    NewAlbumMap.put(AppData.getCurrentUserId() + "/" + NewAlbumId + "/coverimg", NewAlbumCoverImg);
                    NewAlbumMap.put("allalbums/" + NewAlbumId, AppData.getCurrentUserId());

                    AppData.getAlbumsDataRef().updateChildren(NewAlbumMap).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            NewAlbumNameLayout.setText("");
                            NewAlbumDescLayout.setText("");
                            createAlbumDialog.cancel();
                        }
                    });
                }
            }
        });
    }

    private void addSingleImage() {

        Intent addSingleImageGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(addSingleImageGalleryIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 & resultCode == RESULT_OK) {
            CreateAlbumCoverUri = data.getData();
            NewAlbumHasCover = true;
            NewAlbumAddCoverButton.setVisibility(View.GONE);
            Glide.with(this).load(CreateAlbumCoverUri).into(NewAlbumCoverImageView);
        }

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
                    String OriginalImageUrl = taskSnapshot.getDownloadUrl().toString();

                    AppData.getImagesDataRef().child(AppData.getCurrentUserId()).child(String.valueOf(ImageId)).child("image").setValue(OriginalImageUrl);
                    AppData.getImagesDataRef().child("allimages").child(ImageId.toString()).setValue(AppData.getCurrentUserId());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.home_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_profile_button) {
            SendToEditProfile();
        } else if (item.getItemId() == R.id.menu_log_out_button) {
            LogOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private void CheckCredentials() {

        if (AppData.Auth.getCurrentUser() == null) {
            Toast.makeText(this, "Not Signed in", Toast.LENGTH_SHORT).show();
            Intent SendToLogIn = new Intent(HomeActivity.this, StartActivity.class);
            startActivity(SendToLogIn);
            finish();
        }
        if (AppData.Auth.getCurrentUser() != null) {

            AppData.getVerifiedUserDataRef().child(AppData.getCurrentUserId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String CurrentUserProfileThumb = dataSnapshot.child("profileThumbImage").getValue(String.class);

                    if (!CurrentUserProfileThumb.equals("default")) {
                        Glide.with(HomeActivity.this).load(CurrentUserProfileThumb).into(UserProfileImg);
                    }

                    String UserName = AppData.getCurrentUser().getDisplayName() + " " + dataSnapshot.child("lastname").getValue(String.class);

                    UserNameText.setText(UserName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void LogOut() {
        Intent LogoutIntend = new Intent(HomeActivity.this, StartActivity.class);
        LogoutIntend.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        AppData.Auth.signOut();
        startActivity(LogoutIntend);
        finish();
    }

    private void SendToEditProfile() {
        Intent EditProfileIntent = new Intent(HomeActivity.this, EditProfile.class);
        startActivity(EditProfileIntent);
    }
}
