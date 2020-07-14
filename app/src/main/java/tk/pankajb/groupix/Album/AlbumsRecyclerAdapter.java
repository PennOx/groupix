package tk.pankajb.groupix.Album;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import tk.pankajb.groupix.DataStore;
import tk.pankajb.groupix.R;

public class AlbumsRecyclerAdapter extends FirebaseRecyclerAdapter<AlbumsDataModel, AlbumsRecyclerAdapter.AlbumsHolder> {

    Context currentContext;
    DataStore AppData = new DataStore();

    public AlbumsRecyclerAdapter(@NonNull FirebaseRecyclerOptions<AlbumsDataModel> options, Context CurrentContext) {
        super(options);
        currentContext = CurrentContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final AlbumsHolder holder, int position, @NonNull final AlbumsDataModel model) {

        holder.AlbumName.setText(model.getName());
        holder.AlbumDesc.setText(model.getDescription());

        if (!model.getCoverimg().equals("default")) {
            Glide.with(currentContext).load(model.getCoverimg()).into(holder.AlbumCover);
        }

        holder.FullView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SendToAlbumOverview = new Intent(currentContext, AlbumOverview.class);
                SendToAlbumOverview.putExtra("AlbumId", getRef(holder.getAdapterPosition()).getKey());
                SendToAlbumOverview.putExtra("AlbumOwnerId", AppData.getCurrentUserId());
                currentContext.startActivity(SendToAlbumOverview);
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
