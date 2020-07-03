package tk.pankajb.groupix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditProfile extends AppCompatActivity {


    Toolbar EditProfileToolbar;

    TextView UserNameText;
    TextView UserDescText;
    TextView UserEmailText;
    CircleImageView ProfileImg;
    ImageButton EditProfileBtn;

    ProgressDialog mUploadingImageProgress;

    DataStore AppData = new DataStore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        EditProfileToolbar = findViewById(R.id.EditProfile_Toolbar);
        setSupportActionBar(EditProfileToolbar);
        EditProfileToolbar.setTitle("Edit Profile");
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ProfileImg = findViewById(R.id.EditProfile_UserImage);
        UserNameText = findViewById(R.id.EditProfile_UserName);
        UserDescText = findViewById(R.id.EditProfile_Desc);
        UserEmailText = findViewById(R.id.EditProfile_UserEmail);
        EditProfileBtn = findViewById(R.id.EditProfile_EditProfileButtonImage);

        AppData.getVerifiedUserDataRef().child(AppData.getCurrentUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String ProfileThumbImage = dataSnapshot.child("profileThumbImage").getValue(String.class);
                String userName = AppData.getCurrentUser().getDisplayName() + " " + dataSnapshot.child("lastname").getValue(String.class);
                UserNameText.setText(userName);
                UserEmailText.setText(AppData.getCurrentUser().getEmail());

                assert ProfileThumbImage != null;
                if (!ProfileThumbImage.equals("default")) {
                    Glide.with(EditProfile.this).load(ProfileThumbImage).into(ProfileImg);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        EditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(GalleryIntent, 1);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri inputUri = data.getData();
            CropImage.activity(inputUri).setAspectRatio(1, 1).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                mUploadingImageProgress = new ProgressDialog(EditProfile.this);
                mUploadingImageProgress.setTitle("Changing profile image");
                mUploadingImageProgress.setMessage("Please wait while the image is uploading");
                mUploadingImageProgress.show();

                File thumb_file = new File(resultUri.getPath());

                try {
                    Bitmap compressedImageBitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_file);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageBitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
                    byte[] thumb_image_byte = baos.toByteArray();

                    UploadTask uploadTask = AppData.getUsersStorageRef().child(AppData.getCurrentUserId()).child("Thumb_Profile.jpg").putBytes(thumb_image_byte);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            final String Thumb_ImageURL = String.valueOf(taskSnapshot.getDownloadUrl());

                            AppData.getVerifiedUserDataRef().child(AppData.getCurrentUserId()).child("profileThumbImage").setValue(Thumb_ImageURL);

                            mUploadingImageProgress.dismiss();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}