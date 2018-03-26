package com.mindnotix.mnxchats.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mindnotix.mnxchats.R;

/**
 * Created by Admin on 11/23/2017.
 */

public class ContactFragment extends Fragment {
    SwipeRefreshLayout swipelayout;
    TextView txtEmpty;
    RecyclerView recyclerView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);

        UiInitialization(view);
        return view;
    }

    private void UiInitialization(View view) {

        swipelayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        txtEmpty = (TextView) view.findViewById(R.id.txtEmpty);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    }


}
