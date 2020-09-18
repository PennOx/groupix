package tk.pankajb.groupix.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import tk.pankajb.groupix.R;
import tk.pankajb.groupix.album.AlbumsRecyclerAdapter;
import tk.pankajb.groupix.handlers.DataStore;
import tk.pankajb.groupix.models.Album;


public class AlbumsFragment extends Fragment {

    RecyclerView AlbumRecycler;
    AlbumsRecyclerAdapter Adapter;

    DataStore AppData = new DataStore();

    public AlbumsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_albums, container, false);

        Query AlbumsQuery = AppData.getAlbumsDataRef().child(AppData.getCurrentUserId()).limitToLast(50);

        FirebaseRecyclerOptions<Album> options = new FirebaseRecyclerOptions.Builder<Album>().setQuery(AlbumsQuery, Album.class).build();

        AlbumRecycler = view.findViewById(R.id.Albums_Recycler);
        AlbumRecycler.setHasFixedSize(true);
        AlbumRecycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        AlbumRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Adapter = new AlbumsRecyclerAdapter(options, getContext());

        AlbumRecycler.setAdapter(Adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        Adapter.stopListening();
    }
}
