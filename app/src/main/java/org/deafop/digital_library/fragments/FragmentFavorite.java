package org.deafop.digital_library.fragments;

import static org.deafop.digital_library.utils.Constant.VIDEO_LIST_COMPACT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.deafop.digital_library.R;
import org.deafop.digital_library.adapters.AdapterFilms;
import org.deafop.digital_library.databases.DatabaseHandlerFavorite;
import org.deafop.digital_library.models.Films;
import org.deafop.digital_library.utils.SharedPref;

import java.util.ArrayList;

public class FragmentFavorite extends Fragment {

    private static ArrayList<Films> data;
    View root_view, parent_view;
    AdapterFilms adapterFilms;
    DatabaseHandlerFavorite databaseHandler;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    private CharSequence charSequence = null;
    SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_favorite, container, false);
        parent_view = getActivity().findViewById(R.id.lyt_content);

        sharedPref = new SharedPref(getActivity());

       // linearLayout = root_view.findViewById(R.id.lyt_no_favorite);
        recyclerView = root_view.findViewById(R.id.recyclerView);
        if (sharedPref.getVideoViewType() == VIDEO_LIST_COMPACT) {
            recyclerView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.spacing_small), 0, 0);
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        adapterFilms = new AdapterFilms(getActivity(), new ArrayList<>());
        recyclerView.setAdapter(adapterFilms);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        /*data = new ArrayList<>();
        for (int i = 0; i < FilmsData.nameArray.length; i++) {
            data.add(new Films(FilmsData.nameArray[i], FilmsData.id_[i], FilmsData.drawableArray[i]));
        }
        AdapterFilms adapterFilms1 = new AdapterFilms(data);
        recyclerView.setAdapter(adapterFilms1);*/
        return root_view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    public void refreshFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(this).attach(this).commit();
    }

}
