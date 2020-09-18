package tk.pankajb.groupix.album;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    DataStore AppData = new DataStore();

    public AlbumsRecyclerAdapter(@NonNull FirebaseRecyclerOptions<Album> options, Context CurrentContext) {
        super(options);
        currentContext = CurrentContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final AlbumsHolder holder, int position, @NonNull final Album model) {

        holder.AlbumName.setText(model.getName());
        holder.AlbumDesc.setText(model.getDescription());

        if (!model.getCoverImage().equals(currentContext.getString(R.string.DEFAULT_ALBUM_COVER_IMAGE))) {
            Glide.with(currentContext).load(model.getCoverImage()).into(holder.AlbumCover);
        }

        holder.FullView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SendToAlbumOverview = new Intent(currentContext, AlbumOverviewActivity.class);
                SendToAlbumOverview.putExtra("AlbumId", getRef(holder.getAdapterPosition()).getKey());
                SendToAlbumOverview.putExtra("AlbumOwnerId", AppData.getCurrentUserId());
                currentContext.startActivity(SendToAlbumOverview);

                Toast.makeText(currentContext, "Touched", Toast.LENGTH_LONG).show();
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

        TextView AlbumName;
        TextView AlbumDesc;
        ImageView AlbumCover;
        View FullView;

        public AlbumsHolder(View itemView) {
            super(itemView);
            FullView = itemView;

            AlbumName = itemView.findViewById(R.id.SingleAlbum_AlbumName);
            AlbumDesc = itemView.findViewById(R.id.SingleAlbum_AlbumDesc);
            AlbumCover = itemView.findViewById(R.id.SingleAlbum_AlbumCoverImg);
        }
    }
}
