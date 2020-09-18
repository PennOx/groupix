package tk.pankajb.groupix.album;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import tk.pankajb.groupix.AlbumOverviewActivity;
import tk.pankajb.groupix.R;
import tk.pankajb.groupix.handlers.DataStore;
import tk.pankajb.groupix.models.Album;

public class AlbumsRecyclerAdapter extends FirebaseRecyclerAdapter<Album, AlbumsRecyclerAdapter.AlbumsHolder> {

    Context currentContext;
    DataStore appData = new DataStore();

    public AlbumsRecyclerAdapter(@NonNull FirebaseRecyclerOptions<Album> options, Context CurrentContext) {
        super(options);
        currentContext = CurrentContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final AlbumsHolder holder, int position, @NonNull final Album model) {

        holder.albumName.setText(model.getName());
        holder.albumDesc.setText(model.getDescription());

        if (!model.getCoverImage().equals(currentContext.getString(R.string.DEFAULT_ALBUM_COVER_IMAGE))) {
            Glide.with(currentContext).load(model.getCoverImage()).into(holder.albumCover);
        }

        holder.fullView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendToAlbumOverview = new Intent(currentContext, AlbumOverviewActivity.class);
                sendToAlbumOverview.putExtra(currentContext.getString(R.string.ALBUM_ID_INTENT), getRef(holder.getAdapterPosition()).getKey());
                sendToAlbumOverview.putExtra(currentContext.getString(R.string.ALBUM_OWNER_ID_INTENT), appData.getCurrentUserId());
                currentContext.startActivity(sendToAlbumOverview);
            }
        });
    }

    @NonNull
    @Override
    public AlbumsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View SingleLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_album, parent, false);
        return new AlbumsHolder(SingleLayout);
    }

    static class AlbumsHolder extends RecyclerView.ViewHolder {

        TextView albumName;
        TextView albumDesc;
        ImageView albumCover;
        View fullView;

        public AlbumsHolder(View itemView) {
            super(itemView);
            fullView = itemView;

            albumName = itemView.findViewById(R.id.SingleAlbum_AlbumName);
            albumDesc = itemView.findViewById(R.id.SingleAlbum_AlbumDesc);
            albumCover = itemView.findViewById(R.id.SingleAlbum_AlbumCoverImg);
        }
    }
}
