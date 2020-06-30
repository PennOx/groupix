package tk.pankajb.groupix;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ImagesRecyclerAdapter extends FirebaseRecyclerAdapter<ImageDataModel, ImagesRecyclerAdapter.ImagesHolder> {

    ActionHandler AppAction = new ActionHandler();
    private Context currentContext;
    private Dialog SingleImageViewingDialog;
    private String albumId = "";


    public ImagesRecyclerAdapter(@NonNull FirebaseRecyclerOptions<ImageDataModel> options, Context context, String albumId) {
        super(options);
        currentContext = context;
        this.albumId = albumId;
    }

    public ImagesRecyclerAdapter(@NonNull FirebaseRecyclerOptions<ImageDataModel> options, Context context) {
        super(options);
        currentContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ImagesHolder holder, int position, @NonNull ImageDataModel model) {

        final String Image = model.getImage();
        final String ImageId = getRef(position).getKey();

        Glide.with(currentContext).load(Image).into(holder.SingleImage);

        holder.SingleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (albumId.isEmpty()) {
                    AppAction.DisplaySingleImage(currentContext, ImageId);
                } else {
                    AppAction.DisplaySingleImage(currentContext, albumId, ImageId);
                }

            }
        });

    }

    @NonNull
    @Override
    public ImagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View SingleImageLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_image, parent, false);

        SingleImageViewingDialog = new Dialog(currentContext, android.R.style.Theme_Black_NoTitleBar);
        SingleImageViewingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        SingleImageViewingDialog.setContentView(R.layout.single_image_view);
        SingleImageViewingDialog.setCancelable(false);

        return new ImagesHolder(SingleImageLayout);
    }

    class ImagesHolder extends RecyclerView.ViewHolder {

        ImageView SingleImage;

        public ImagesHolder(View itemView) {
            super(itemView);

            SingleImage = itemView.findViewById(R.id.Single_ImageThumb);
        }
    }
}