package tk.pankajb.groupix.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import tk.pankajb.groupix.R;
import tk.pankajb.groupix.handlers.DataStore;
import tk.pankajb.groupix.image.ImageDataModel;
import tk.pankajb.groupix.image.ImagesRecyclerAdapter;

public class ImagesFragment extends Fragment {

    private FirebaseRecyclerAdapter ImagesAdapter;

    private DataStore AppData = new DataStore();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_images, container, false);

        Query ImagesQuery = AppData.getImagesDataRef().child(AppData.getCurrentUserId()).limitToLast(50);

        FirebaseRecyclerOptions<tk.pankajb.groupix.image.ImageDataModel> ImagesOptions = new FirebaseRecyclerOptions.Builder<tk.pankajb.groupix.image.ImageDataModel>().setQuery(ImagesQuery, ImageDataModel.class).build();

        RecyclerView imagesRecycler = view.findViewById(R.id.Images_Recycler);
        imagesRecycler.setHasFixedSize(true);
        imagesRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));

        ImagesAdapter = new ImagesRecyclerAdapter(ImagesOptions, getContext(), AppData.getCurrentUserId());

        imagesRecycler.setAdapter(ImagesAdapter);

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
