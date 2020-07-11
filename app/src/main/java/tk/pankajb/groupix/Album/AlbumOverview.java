package tk.pankajb.groupix.Album;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import tk.pankajb.groupix.DataStore;
import tk.pankajb.groupix.Image.ImageDataModel;
import tk.pankajb.groupix.Image.ImagesRecyclerAdapter;
import tk.pankajb.groupix.R;

public class AlbumOverview extends AppCompatActivity {

    Toolbar OverviewToolbar;
    ImageView AlbumCoverImageView;
    TextView AlbumNameTextView;
    TextView AlbumDescTextView;
    RecyclerView AlbumImagesRecycler;
    FloatingActionButton AlbumAddImgBtn;

    Dialog EditAlbumDialog;
    Button EditAlbumCloseButton;
    Button EditAlbumDeleteButton;
    ImageView EditAlbumCoverImage;
    ImageButton EditAlbumAddCoverButton;
    LinearLayout EditAlbumButtonLayout;
    ImageButton EditAlbumEditCoverButton;
    ImageButton EditAlbumDeleteCoverButton;
    EditText EditAlbumAlbumName;
    EditText EditAlbumAlbumDesc;
    Button EditAlbumEditButton;
    final private int ALBUM_IMAGE_REQUEST = 1;
    final private int ALBUM_COVER_REQUEST = 2;
    boolean coverChanged = false;

    FirebaseRecyclerAdapter ImagesAdapter;

    DataStore AppData = new DataStore();

    String AlbumId;
    String AlbumOwnerId;

    Uri InputImage;
    Long NewImageId;
    Uri CreateAlbumCoverUri;
    ProgressDialog ImageUploadProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_overview);

        OverviewToolbar = findViewById(R.id.AlbumOverview_Toolbar);
        AlbumCoverImageView = findViewById(R.id.AlbumOverview_AlbumCover);
        AlbumNameTextView = findViewById(R.id.AlbumOverview_AlbumName);
        AlbumDescTextView = findViewById(R.id.AlbumOverview_AlbumDesc);
        AlbumImagesRecycler = findViewById(R.id.AlbumOverview_ImagesRecycler);
        AlbumAddImgBtn = findViewById(R.id.AlbumOverview_AddImagesBtn);

        setSupportActionBar(OverviewToolbar);
        getSupportActionBar().setTitle("Album");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AlbumImagesRecycler.setHasFixedSize(true);
        AlbumImagesRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        AlbumId = getIntent().getStringExtra("AlbumId");

        EditAlbumDialog = new Dialog(AlbumOverview.this, android.R.style.Theme_Black_NoTitleBar);
        EditAlbumDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        EditAlbumDialog.setContentView(R.layout.edit_album);
        EditAlbumDialog.setCancelable(true);

        EditAlbumCloseButton = EditAlbumDialog.getWindow().findViewById(R.id.EditAlbum_CloseButton);
        EditAlbumDeleteButton = EditAlbumDialog.getWindow().findViewById(R.id.EditAlbum_DeleButton);
        EditAlbumCoverImage = EditAlbumDialog.getWindow().findViewById(R.id.EditAlbum_CoverImg);
        EditAlbumAddCoverButton = EditAlbumDialog.getWindow().findViewById(R.id.EditAlbum_AddCoverButton);
        EditAlbumButtonLayout = EditAlbumDialog.getWindow().findViewById(R.id.EditAlbum_CoverButtonLayout);
        EditAlbumEditCoverButton = EditAlbumDialog.getWindow().findViewById(R.id.EditAlbum_EditCoverButton);
        EditAlbumDeleteCoverButton = EditAlbumDialog.getWindow().findViewById(R.id.EditAlbum_DeleteCoverButton);
        EditAlbumAlbumName = EditAlbumDialog.getWindow().findViewById(R.id.EditAlbum_AlbumName);
        EditAlbumAlbumDesc = EditAlbumDialog.getWindow().findViewById(R.id.EditAlbum_AlbumDescription);
        EditAlbumEditButton = EditAlbumDialog.getWindow().findViewById(R.id.EditAlbum_EditAlbumButton);

        ImageUploadProgressBar = new ProgressDialog(EditAlbumDialog.getContext());
        ImageUploadProgressBar.setTitle("Uploading Image");

        AppData.getAlbumsDataRef().child("AllAlbums").child(AlbumId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                AlbumOwnerId = (String) dataSnapshot.getValue();
                AppData.getAlbumsDataRef().child(AlbumOwnerId).child(AlbumId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (AppData.getCurrentUserId().equals(AlbumOwnerId)) {
                            AlbumAddImgBtn.setVisibility(View.VISIBLE);
                        }

                        AlbumNameTextView.setText(dataSnapshot.child("name").getValue(String.class));
                        AlbumDescTextView.setText(dataSnapshot.child("description").getValue(String.class));
                        if (!dataSnapshot.child("coverimg").getValue(String.class).equals("default")) {
                            Glide.with(getApplicationContext()).load(dataSnapshot.child("coverimg").getValue(String.class)).into(AlbumCoverImageView);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Query AlbumImagesQuery = AppData.getAlbumsDataRef().child(AlbumOwnerId).child(AlbumId).child("images").limitToLast(50);
                FirebaseRecyclerOptions<ImageDataModel> AlbumImagesOptions = new FirebaseRecyclerOptions.Builder<ImageDataModel>().setQuery(AlbumImagesQuery, ImageDataModel.class).build();
                ImagesAdapter = new ImagesRecyclerAdapter(AlbumImagesOptions, getApplicationContext(), AlbumId);
                AlbumImagesRecycler.setAdapter(ImagesAdapter);
                ImagesAdapter.startListening();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        setAddBtn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.albumoverview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_editalbumbtn) {
            EditAlbum();
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            InputImage = data.getData();
            NewImageId = System.currentTimeMillis();

            UploadTask OriginalImageUpload = AppData.getAlbumsStorageRef().child(AppData.getCurrentUserId()).child(AlbumId).child("images").child(String.valueOf(NewImageId)).child("image").putFile(InputImage);


            OriginalImageUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String OriginalImageUrl = String.valueOf(taskSnapshot.getDownloadUrl());

                    AppData.getAlbumsDataRef().child(AppData.getCurrentUserId()).child(AlbumId).child("images").child(String.valueOf(NewImageId)).child("image").setValue(OriginalImageUrl);
                    AppData.getAlbumsDataRef().child("AllImages").child(String.valueOf(NewImageId)).setValue(AppData.getCurrentUserId());
                }
            });

            OriginalImageUpload.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AlbumOverview.this, "Image upload failed", Toast.LENGTH_SHORT).show();

                    Log.e("Image Upload Error", e.toString());
                }
            });
        }

        if (requestCode == 2 && resultCode == RESULT_OK) {
            CreateAlbumCoverUri = data.getData();
            coverChanged = true;
            Glide.with(this).load(CreateAlbumCoverUri).into(EditAlbumCoverImage);
        }
    }

    private void setAddBtn() {
        AlbumAddImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAlbumImgGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(addAlbumImgGalleryIntent, ALBUM_IMAGE_REQUEST);
            }
        });
    }

    private void UploadCover() {
        Intent addAlbumCoverGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(addAlbumCoverGalleryIntent, ALBUM_COVER_REQUEST);
    }

    private void EditAlbum() {
        AppData.getAlbumsDataRef().child("AllAlbums").child(AlbumId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String OwnerId = dataSnapshot.getValue(String.class);

                assert OwnerId != null;
                AppData.getAlbumsDataRef().child(OwnerId).child(AlbumId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String coverimg = dataSnapshot.child("coverimg").getValue(String.class);
                        String desc = dataSnapshot.child("description").getValue(String.class);
                        String name = dataSnapshot.child("name").getValue(String.class);

                        EditAlbumAlbumName.setText(name);
                        EditAlbumAlbumDesc.setText(desc);
                        assert coverimg != null;
                        if (!coverimg.equals("default")) {
                            Glide.with(AlbumOverview.this).load(coverimg).into(EditAlbumCoverImage);
                            EditAlbumAddCoverButton.setVisibility(View.GONE);
                            EditAlbumButtonLayout.setVisibility(View.VISIBLE);
                        } else {
                            Glide.with(AlbumOverview.this).load(R.drawable.home_background).into(EditAlbumCoverImage);
                            EditAlbumAddCoverButton.setVisibility(View.VISIBLE);
                            EditAlbumButtonLayout.setVisibility(View.GONE);
                        }
                        EditAlbumDialog.show();
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

        EditAlbumCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeEditAlbum();
            }
        });

        EditAlbumDeleteCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData.getAlbumsDataRef().child(AlbumOwnerId).child(AlbumId).child("coverimg")
                        .setValue("default").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AppData.getAlbumsStorageRef().child(AlbumOwnerId).child(AlbumId).child("coverimg").delete();
                        EditAlbumAddCoverButton.setVisibility(View.VISIBLE);
                        EditAlbumButtonLayout.setVisibility(View.GONE);
                    }
                });
            }
        });

        EditAlbumEditCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AlbumOverview.this, "Edit Cover Clicked", Toast.LENGTH_LONG).show();
            }
        });

        EditAlbumAddCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AlbumOverview.this, "Add Cover Clicked", Toast.LENGTH_LONG).show();
                UploadCover();
            }
        });

        EditAlbumEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AlbumOverview.this, "Edit album Clicked", Toast.LENGTH_LONG).show();
                String AlbumName = EditAlbumAlbumName.getText().toString().trim();
                String AlbumDesc = EditAlbumAlbumDesc.getText().toString().trim();

                AppData.getAlbumsDataRef().child(AlbumOwnerId).child(AlbumId).child("name").setValue(AlbumName);
                AppData.getAlbumsDataRef().child(AlbumOwnerId).child(AlbumId).child("description").setValue(AlbumDesc);

                if (coverChanged) {
                    coverChanged = false;
                    UploadTask AlbumCoverUpload = AppData.getAlbumsStorageRef().child(AlbumOwnerId).child(AlbumId).child("coverimg").putFile(CreateAlbumCoverUri);
                    AlbumCoverUpload.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            long ProgressDone = 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                            ImageUploadProgressBar.setMessage("Uploading Image " + ProgressDone + "%");
                        }
                    });

                    AlbumCoverUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String NewAlbumCoverImg = String.valueOf(taskSnapshot.getDownloadUrl());

                            Map NewAlbumMap = new HashMap<>();
                            NewAlbumMap.put(AppData.getCurrentUserId() + "/" + AlbumId + "/coverimg", NewAlbumCoverImg);
                            AppData.getAlbumsDataRef().updateChildren(NewAlbumMap).addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    ImageUploadProgressBar.dismiss();
                                    closeEditAlbum();
                                    ImageUploadProgressBar.dismiss();
                                }
                            });
                        }
                    });


                } else {
                    closeEditAlbum();
                    ImageUploadProgressBar.dismiss();
                }


            }
        });

        EditAlbumDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AlbumOverview.this, "Delete album Clicked", Toast.LENGTH_LONG).show();

                AlertDialog.Builder deleteConfDialog = new AlertDialog.Builder(AlbumOverview.this);
                deleteConfDialog.setTitle("Are you sure?");
                deleteConfDialog.setMessage("You want to delete this album.");
                deleteConfDialog.setCancelable(true);
                deleteConfDialog.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(AlbumOverview.this, "Delete this album", Toast.LENGTH_LONG).show();
                                deleteAlbum();
                            }
                        });
                deleteConfDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alert1 = deleteConfDialog.create();
                alert1.show();
            }
        });
    }

    private void deleteAlbum() {
        // TODO delete current album
        finish();
    }

    private void closeEditAlbum() {
        EditAlbumDialog.dismiss();
    }


}
