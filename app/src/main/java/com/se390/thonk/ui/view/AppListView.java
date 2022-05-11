package com.se390.thonk.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.se390.thonk.ui.activity.AppChooserActivity;
import com.se390.thonk.MainApplication;
import com.se390.thonk.R;
import com.se390.thonk.ui.util.RecyclerViewMarginDecoration;

import java.util.ArrayList;

public class AppListView extends LinearLayout {
    public ArrayList<String> apps = new ArrayList<>();
    private TextView noAppsSelected;
    public RecyclerAdapter adapter;

    public AppListView(Context context) {
        super(context);
        init();
    }

    public AppListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AppListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        public ArrayList<MainApplication.AppInfoCache> apps = new ArrayList<>();

        public RecyclerAdapter() {
        }

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_app_list_entry, parent, false);
            return new RecyclerAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
            holder.update(apps.get(position));
        }

        @Override
        public int getItemCount() {
            return apps.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView icon;

            public ViewHolder(View view) {
                super(view);
                icon = view.findViewById(R.id.applist_view_entry);
            }

            public void update(MainApplication.AppInfoCache app) {
                icon.setImageDrawable(app.icon);
            }
        }
    }

    private void init() {
        inflate(getContext(), R.layout.view_app_list, this);
        noAppsSelected = findViewById(R.id.applist_view_no_apps);
        adapter = new RecyclerAdapter();

        RecyclerView recyclerView = findViewById(R.id.applist_view_recycler);
        RecyclerViewMarginDecoration marginDecoration = new RecyclerViewMarginDecoration(10, 0);
        recyclerView.addItemDecoration(marginDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        ComponentActivity activity = getActivity();
        if(activity != null) {
//            ActivityResultLauncher<Intent> launcher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//                if(result.getResultCode() == Activity.RESULT_OK) {
//                    Intent response = result.getData();
//                    if(response != null) {
//                        apps = response.getStringArrayListExtra("apps");
//                        if(apps == null) {
//                            apps = new ArrayList<>();
//                        }
//                        updateIcons();
//                    }
//                }
//            });

            ImageButton button = findViewById(R.id.applist_view_edit);
            button.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AppChooserActivity.class);
                intent.putStringArrayListExtra("apps", apps);
//                launcher.launch(intent);
            });
        }
    }

    public void updateIcons() {
        if(apps.size() == 0) {
            noAppsSelected.setVisibility(VISIBLE);
        } else {
            noAppsSelected.setVisibility(GONE);
        }
        adapter.apps.clear();
        for(String app : apps) {
            adapter.apps.add(MainApplication.inst.getAppInfo(app));
        }
        adapter.notifyDataSetChanged();
    }

    private AppCompatActivity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (AppCompatActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
