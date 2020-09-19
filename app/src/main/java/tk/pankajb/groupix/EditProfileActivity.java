package tk.pankajb.groupix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import tk.pankajb.groupix.handlers.ActionHandler;
import tk.pankajb.groupix.handlers.DataStore;
import tk.pankajb.groupix.models.User;

public class EditProfileActivity extends AppCompatActivity {

    private TextView userNameText;
    private TextView userEmailText;
    private CircleImageView profileImg;

    private DataStore appData = new DataStore();
    private ActionHandler handler = new ActionHandler(EditProfileActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar editProfileToolbar = findViewById(R.id.EditProfile_Toolbar);
        setSupportActionBar(editProfileToolbar);
        getSupportActionBar().setTitle(getString(R.string.EDIT_PROFILE_TITLE));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileImg = findViewById(R.id.EditProfile_UserImage);
        userNameText = findViewById(R.id.EditProfile_UserName);
        userEmailText = findViewById(R.id.EditProfile_UserEmail);

        appData.getUsersDataRef().child(appData.getCurrentUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User currentUser = dataSnapshot.getValue(User.class);

                userNameText.setText(currentUser.getFullName());
                userEmailText.setText(currentUser.geteMail());

                if (!currentUser.getProfileThumbImage().equals(getString(R.string.DEFAULT_USER_PROFILE_THUMB))) {
                    Glide.with(EditProfileActivity.this).load(currentUser.getProfileThumbImage()).into(profileImg);
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

        if (requestCode == getResources().getInteger(R.integer.EDIT_PROFILE) && resultCode == RESULT_OK) {
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
        startActivityForResult(GalleryIntent, getResources().getInteger(R.integer.EDIT_PROFILE));
    }

    public void logOut(View button) {
        handler.logOut();
        finish();
    }
}