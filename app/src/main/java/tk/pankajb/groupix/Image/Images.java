package tk.pankajb.groupix.Image;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import tk.pankajb.groupix.R;

public class Images extends Fragment {

    FirebaseUser CurrentUser;

    RecyclerView ImagesRecycler;

    FirebaseRecyclerAdapter ImagesAdapter;

    public Images() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_images, container, false);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        Query ImagesQuery = FirebaseDatabase.getInstance().getReference().child("images").child(CurrentUser.getUid()).limitToLast(50);

        FirebaseRecyclerOptions<tk.pankajb.groupix.Image.ImageDataModel> ImagesOptions = new FirebaseRecyclerOptions.Builder<tk.pankajb.groupix.Image.ImageDataModel>().setQuery(ImagesQuery, ImageDataModel.class).build();

        ImagesRecycler = view.findViewById(R.id.Images_Recycler);
        ImagesRecycler.setHasFixedSize(true);
        ImagesRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));

        ImagesAdapter = new ImagesRecyclerAdapter(ImagesOptions, getContext());

        ImagesRecycler.setAdapter(ImagesAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ImagesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        ImagesAdapter.stopListening();
    }
}
