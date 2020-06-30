package tk.pankajb.groupix;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;

public class ActionHandler {

    DataStore AppData = new DataStore();

    private File TemporaryFile;

    public ActionHandler() {

    }

    public void DownloadSingleImage(final String ImageId, String OwnerId, final Context context) {

        File f = new File("/sdcard/Groupix/");

        if (!f.exists()) {
            f.mkdir();
        }

        TemporaryFile = new File("/sdcard/Groupix/", ImageId + ".jpg");

        AppData.getImagesStorageRef().child(OwnerId).child(ImageId).child("image").getFile(TemporaryFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.i("ImageDownload", "Image Downloaded " + ImageId + ".jpg to " + "Image downloaded to " + Environment.getExternalStorageDirectory() + "/" + R.string.app_name);

                Toast.makeText(context, "Image downloaded to " + Environment.getExternalStorageDirectory() + "/" + R.string.app_name, Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ImageDownload", "Image not downloaded with error code " + e);

                Toast.makeText(context, "Image not downloaded successfully", Toast.LENGTH_LONG).show();
            }
        });
    }

    void DeleteSingleImage(String ImageId, String OwnerId) {

        AppData.getImagesDataRef().child(OwnerId).child(ImageId).setValue(null);
        AppData.getImagesDataRef().child("allimages").child(ImageId).setValue(null);

        AppData.getImagesStorageRef().child(OwnerId).child(ImageId).child("image").delete();
    }


    void DisplaySingleImage(Context currentContext, String ImageId) {
        Intent DisplayImage = new Intent(currentContext, SingleImageView.class);
        DisplayImage.putExtra("Type", "Single");
        DisplayImage.putExtra("ImageId", ImageId);
        currentContext.startActivity(DisplayImage);
    }

    void DisplaySingleImageFromAlbum(Context currentContext, String AlbumId, String ImageId) {
        Intent DisplayImage = new Intent(currentContext, SingleImageView.class);
        DisplayImage.putExtra("Type", "Album");
        DisplayImage.putExtra("AlbumId", AlbumId);
        DisplayImage.putExtra("ImageId", ImageId);
        currentContext.startActivity(DisplayImage);
    }
}
