package tk.pankajb.groupix;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class Albums extends Fragment {


    FirebaseUser CurrentUser;
    RecyclerView AlbumRecycler;
    AlbumsRecyclerAdapter Adapter;


    public Albums() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_albums, container, false);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        Query AlbumsQuery = FirebaseDatabase.getInstance().getReference().child("albums").child(CurrentUser.getUid()).limitToLast(50);

        FirebaseRecyclerOptions<AlbumsDataModel> options = new FirebaseRecyclerOptions.Builder<AlbumsDataModel>().setQuery(AlbumsQuery, AlbumsDataModel.class).build();

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
