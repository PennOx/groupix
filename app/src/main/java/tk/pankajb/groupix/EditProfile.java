package tk.pankajb.groupix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    final private short EDIT_PROFILE_IMAGE_REQUEST = 1;

    Toolbar EditProfileToolbar;

    TextView UserNameText;
    TextView UserDescText;
    TextView UserEmailText;
    CircleImageView ProfileImg;
    ImageButton EditProfileBtn;

    DataStore AppData = new DataStore();
    ActionHandler handler = new ActionHandler(EditProfile.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        EditProfileToolbar = findViewById(R.id.EditProfile_Toolbar);
        setSupportActionBar(EditProfileToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ProfileImg = findViewById(R.id.EditProfile_UserImage);
        UserNameText = findViewById(R.id.EditProfile_UserName);
        UserDescText = findViewById(R.id.EditProfile_Desc);
        UserEmailText = findViewById(R.id.EditProfile_UserEmail);
        EditProfileBtn = findViewById(R.id.EditProfile_EditProfileButtonImage);

        AppData.getUsersDataRef().child(AppData.getCurrentUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String ProfileThumbImage = dataSnapshot.child("ProfileThumbImage").getValue(String.class);
                String userName = AppData.getCurrentUser().getDisplayName() + " " + dataSnapshot.child("LastName").getValue(String.class);
                UserNameText.setText(userName);
                UserEmailText.setText(AppData.getCurrentUser().getEmail());

                assert ProfileThumbImage != null;
                if (!ProfileThumbImage.equals("default")) {
                    Glide.with(EditProfile.this).load(ProfileThumbImage).into(ProfileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri inputUri = data.getData();
            CropImage.activity(inputUri).setAspectRatio(1, 1).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                handler.uploadUserProfileImage(resultUri);
            }
        }
    }

    public void editProfileImage(View view) {
        Intent GalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(GalleryIntent, EDIT_PROFILE_IMAGE_REQUEST);
    }

    public void logOut(View button) {
        handler.logOut();
        finish();
    }
}