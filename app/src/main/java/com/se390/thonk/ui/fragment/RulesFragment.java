package com.se390.thonk.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.se390.thonk.R;
import com.se390.thonk.data.NotificationEntry;

import java.util.ArrayList;
import java.util.List;

public class RulesFragment extends Fragment {
    public RulesFragment() {
        super(R.layout.fragment_rules);
    }

    public static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        public RecyclerAdapter() {
        }

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rules_entry, parent, false);
            return new RecyclerAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return 100;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View view) {
                super(view);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rules, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rules_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(new RecyclerAdapter());

        return view;
    }


}
