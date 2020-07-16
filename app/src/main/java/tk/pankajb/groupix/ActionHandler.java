package tk.pankajb.groupix;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class ActionHandler {

    Context context;
    DataStore AppData = new DataStore();

    private File TemporaryFile;

    public ActionHandler(Context context) {
        this.context = context;
    }

    public void DownloadSingleImage(final String ImageId, String OwnerId) {

        File f = new File(Environment.getExternalStorageDirectory() + "/Groupix/");
        if (!f.exists()) {
            f.mkdir();
        }
        TemporaryFile = new File(Environment.getExternalStorageDirectory() + "/Groupix/", ImageId + ".jpg");
        AppData.getImagesStorageRef().child(OwnerId).child(ImageId).child("image").getFile(TemporaryFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.i("ImageDownload", "Image Downloaded " + ImageId + ".jpg to " + "Image downloaded to " + Environment.getExternalStorageDirectory() + "/" + R.string.app_name);
                Toast.makeText(context, "Image downloaded to " + Environment.getExternalStorageDirectory() + "/" + R.string.app_name, Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("ImageDownload", "Image not downloaded with error code " + e);

                Toast.makeText(context, "Image not downloaded successfully", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void DownloadSingleImage(final String AlbumId, final String ImageId, final String OwnerId) {
        File f = new File(Environment.getExternalStorageDirectory() + "/Groupix/");
        if (!f.exists()) {
            f.mkdir();
        }

        AppData.getAlbumsDataRef().child(OwnerId).child(AlbumId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                File f1 = new File(Environment.getExternalStorageDirectory() + "/Groupix/" + (String) dataSnapshot.child("name").getValue() + "/");
                if (!f1.exists()) {
                    f1.mkdir();
                }
                TemporaryFile = new File(Environment.getExternalStorageDirectory() + "/Groupix/" + (String) dataSnapshot.child("name").getValue() + "/", ImageId + ".jpg");

                AppData.getAlbumsStorageRef().child(OwnerId).child(AlbumId).child("images").child(ImageId).child("image").getFile(TemporaryFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.i("ImageDownload", "Image Downloaded " + ImageId + ".jpg to " + "Image downloaded to " + Environment.getExternalStorageDirectory() + "/" + R.string.app_name);
                        Toast.makeText(context, "Image downloaded to " + Environment.getExternalStorageDirectory() + "/" + R.string.app_name, Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("ImageDownload", "Image not downloaded with error code " + e);
                        Toast.makeText(context, "Image not downloaded successfully", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void uploadUserProfileImage(Uri fileUri) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Changing profile image");
        progress.setMessage("Please wait while the image is uploading");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        File thumb_file = new File(fileUri.getPath());

        try {
            Bitmap compressedImageBitmap = new Compressor(context)
                    .setMaxHeight(200)
                    .setMaxWidth(200)
                    .setQuality(50)
                    .compressToBitmap(thumb_file);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImageBitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
            byte[] thumb_image_byte = baos.toByteArray();

            AppData.getUsersStorageRef().child(AppData.getCurrentUserId()).child("Thumb_Profile.jpg").putBytes(thumb_image_byte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    AppData.getUsersStorageRef().child(AppData.getCurrentUserId()).child("Thumb_Profile.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            AppData.getUsersDataRef().child(AppData.getCurrentUserId()).child("ProfileThumbImage").setValue(uri.toString());

                            progress.dismiss();
                        }
                    });
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadSingleImage(final String userId, Uri fileUri) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Uploading image");
        progress.setMessage("Please wait while the image is uploading");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        final long imageId = System.currentTimeMillis();

        UploadTask OriginalImageUpload = AppData.getImagesStorageRef().child(userId).child(String.valueOf(imageId)).child("image").putFile(fileUri);

        OriginalImageUpload.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                long ProgressDone = 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                progress.setMessage("Uploading Image " + ProgressDone + "%");
            }
        });
        OriginalImageUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                AppData.getImagesStorageRef().child(userId).child(String.valueOf(imageId)).child("image").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        AppData.getImagesDataRef().child(userId).child(String.valueOf(imageId)).child("image").setValue(uri.toString());
                        AppData.getImagesDataRef().child("AllImages").child(String.valueOf(imageId)).setValue(userId);
                        progress.dismiss();
                    }
                });


            }
        });

        OriginalImageUpload.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show();

                Log.e("Image Upload Error", e.toString());
            }
        });
    }

    public void uploadAlbumImage(final String userId, final String albumId, Uri fileUri) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Uploading image");
        progress.setMessage("Please wait while the image is uploading");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        final long imageId = System.currentTimeMillis();

        UploadTask OriginalImageUpload = AppData.getAlbumsStorageRef().child(userId).child(albumId).child("images").child(String.valueOf(imageId)).child("image").putFile(fileUri);

        OriginalImageUpload.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                long ProgressDone = 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                progress.setMessage("Uploading Image " + ProgressDone + "%");
            }
        });

        OriginalImageUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                AppData.getAlbumsStorageRef().child(userId).child(albumId).child("images").child(String.valueOf(imageId)).child("image").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        AppData.getAlbumsDataRef().child(AppData.getCurrentUserId()).child(albumId).child("images").child(String.valueOf(imageId)).child("image").setValue(uri.toString());
                        AppData.getAlbumsDataRef().child("AllImages").child(String.valueOf(imageId)).setValue(AppData.getCurrentUserId());

                        progress.dismiss();
                    }
                });
            }
        });

        OriginalImageUpload.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.dismiss();
                Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show();
                Log.e("Image Upload Error", e.toString());
            }
        });
    }

    public void uploadAlbumCover() {

    }

    public void DeleteSingleImage(String ImageId, String OwnerId) {

        AppData.getImagesDataRef().child(OwnerId).child(ImageId).setValue(null);
        AppData.getImagesDataRef().child("AllImages").child(ImageId).setValue(null);

        AppData.getImagesStorageRef().child(OwnerId).child(ImageId).child("image").delete();
    }

    public void DeleteSingleImage(String ImageId, String OwnerId, String AlbumId) {
        AppData.getAlbumsDataRef().child("AllImages").child(ImageId).setValue(null);
        AppData.getAlbumsDataRef().child(OwnerId).child(AlbumId).child("images").child(ImageId).setValue(null);

        AppData.getAlbumsStorageRef().child(OwnerId).child(AlbumId).child("images").child(ImageId).child("image").delete();
    }

    public void DisplaySingleImage(String ImageId, String OwnerId) {
        Intent DisplayImage = new Intent(context, SingleImageView.class);
        DisplayImage.putExtra("Type", "Single");
        DisplayImage.putExtra("ImageId", ImageId);
        DisplayImage.putExtra("OwnerId", OwnerId);
        DisplayImage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(DisplayImage);
    }

    public void DisplaySingleImage(String ImageId, String OwnerId, String AlbumId) {
        Intent DisplayImage = new Intent(context, SingleImageView.class);
        DisplayImage.putExtra("Type", "Album");
        DisplayImage.putExtra("AlbumId", AlbumId);
        DisplayImage.putExtra("ImageId", ImageId);
        DisplayImage.putExtra("OwnerId", OwnerId);
        DisplayImage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(DisplayImage);
    }

    public void logOut() {
        AppData.Auth.signOut();
        Intent sendToStart = new Intent(context, tk.pankajb.groupix.Credentials.StartActivity.class);
        sendToStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(sendToStart);
    }
}
