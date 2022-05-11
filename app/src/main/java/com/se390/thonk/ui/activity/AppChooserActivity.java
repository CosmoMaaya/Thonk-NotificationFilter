package com.se390.thonk.ui.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.se390.thonk.MainApplication;
import com.se390.thonk.R;
import com.se390.thonk.ui.util.SortedListAdapterHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppChooserActivity extends AppCompatActivity {
    public RecyclerAdapter adapter;

    public static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        public List<MainApplication.AppInfoCache> allApps = new ArrayList<>();
        public HashSet<String> prioritizedApps;
        public HashSet<String> selectedApps;
        public String filter = "";
        public boolean showSystem = false;

        public SortedList<MainApplication.AppInfoCache> sortedList = new SortedList<>(MainApplication.AppInfoCache.class, new SortedListAdapterHelper<>(
                this,
                (a, b) -> {
                    int comp = -Boolean.compare(prioritizedApps.contains(a.pkg), prioritizedApps.contains(b.pkg));
                    if(comp != 0) {
                        return comp;
                    }
                    comp = a.name.compareTo(b.name);
                    if(comp != 0) {
                        return comp;
                    }
                    return a.pkg.compareTo(b.pkg);
                }
        ));
        public final View progressOverlay;

        public RecyclerAdapter(HashSet<String> selectedApps, View progressOverlay) {
            this.prioritizedApps = new HashSet<>(selectedApps);
            this.selectedApps = selectedApps;
            this.progressOverlay = progressOverlay;
        }

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_chooser_entry, parent, false);
            return new RecyclerAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
            holder.update(sortedList.get(position));
        }

        @Override
        public int getItemCount() {
            return sortedList.size();
        }

        public void fetch() {
            new FetchTask(this).execute((Void[]) null);
        }

        public void setFilter(String filter) {
            this.filter = filter.toLowerCase();
            updateSortedList();
        }

        public void setShowSystem(boolean showSystem) {
            this.showSystem = showSystem;
            updateSortedList();
        }

        public void updateSortedList() {
            Set<MainApplication.AppInfoCache> filteredApps = new HashSet<>();
            for(MainApplication.AppInfoCache app : allApps) {
                if(app.name.toLowerCase().contains(filter) || app.pkg.toLowerCase().contains(filter)) {
                    if(showSystem || (app.info.flags & ApplicationInfo.FLAG_SYSTEM) == 0 || prioritizedApps.contains(app.pkg)) {
                        filteredApps.add(app);
                    }
                }
            }

            sortedList.beginBatchedUpdates();
            for(int i = sortedList.size() - 1; i >= 0; --i) {
                if(!filteredApps.contains(sortedList.get(i))) {
                    sortedList.removeItemAt(i);
                }
            }
            sortedList.addAll(filteredApps);
            sortedList.endBatchedUpdates();
        }

        public static class FetchTask extends AsyncTask<Void, Void, List<MainApplication.AppInfoCache>> {
            WeakReference<RecyclerAdapter> weakAdaptper;
            public FetchTask(RecyclerAdapter adapter) {
                this.weakAdaptper = new WeakReference<>(adapter);
            }

            @Override
            protected void onPreExecute() {
                RecyclerAdapter adapter = weakAdaptper.get();
                if(adapter != null) {
                    adapter.progressOverlay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected List<MainApplication.AppInfoCache> doInBackground(Void... voids) {
                return MainApplication.inst.listApps();
            }

            @Override
            protected void onPostExecute(List<MainApplication.AppInfoCache> apps) {
                RecyclerAdapter adapter = weakAdaptper.get();
                if(adapter != null) {
                    adapter.allApps = apps;
                    adapter.updateSortedList();
                    adapter.progressOverlay.setVisibility(View.GONE);
                }
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView name;
            private final TextView pkg;
            private final ImageView icon;
            private final CheckBox checkbox;
            private final View view;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                name = view.findViewById(R.id.appchooser_entry_name);
                pkg = view.findViewById(R.id.appchooser_entry_package);
                icon = view.findViewById(R.id.appchooser_entry_icon);
                checkbox = view.findViewById(R.id.appchooser_entry_checkbox);
            }

            public void update(MainApplication.AppInfoCache info) {
                checkbox.setChecked(selectedApps.contains(info.pkg));
                name.setText(info.name);
                pkg.setText(info.pkg);
                icon.setImageDrawable(info.icon);
                view.setOnClickListener(listener -> {
                    checkbox.toggle();
                    if(checkbox.isChecked()) {
                        selectedApps.add(info.pkg);
                    } else {
                        selectedApps.remove(info.pkg);
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_chooser_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.app_chooser_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.setFilter(newText);
                return true;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.app_chooser_show_system) {
            if (adapter.showSystem) {
                item.setTitle(R.string.app_chooser_show_system);
            } else {
                item.setTitle(R.string.app_chooser_show_system_hide);
            }
            adapter.setShowSystem(!adapter.showSystem);
            return true;
        } else if(itemId == R.id.app_chooser_menu_confirm) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("apps", new ArrayList<>(adapter.selectedApps));
            setResult(RESULT_OK, intent);
            finish();
            return true;
        } else if(itemId == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_app_chooser);

        RecyclerView recyclerView = findViewById(R.id.app_chooser_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        View progressOverlay = findViewById(R.id.app_chooser_progress_overlay);

        Intent intent = getIntent();
        ArrayList<String> apps = intent.getStringArrayListExtra("apps");

        HashSet<String> selected = new HashSet<>();
        if(apps != null) {
            selected.addAll(apps);
        }

        adapter = new RecyclerAdapter(selected, progressOverlay);
        adapter.fetch();

        recyclerView.setAdapter(adapter);
    }
}