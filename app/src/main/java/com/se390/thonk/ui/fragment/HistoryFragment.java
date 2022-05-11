package com.se390.thonk.ui.fragment;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.se390.thonk.MainApplication;
import com.se390.thonk.R;
import com.se390.thonk.data.NotificationEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryFragment extends Fragment {

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        List<NotificationEntry> notifsList;

        public RecyclerAdapter(LiveData<List<NotificationEntry>> notifsLiveList) {
            this.notifsList = new ArrayList<>();
            notifsLiveList.observeForever((notifsList) -> {
                this.notifsList = notifsList;
                notifyDataSetChanged();
            });
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_entry, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            NotificationEntry entry = notifsList.get(position);
            holder.update(entry);
        }

        @Override
        public int getItemCount() {
            return notifsList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView heading;
            private TextView title;
            private TextView text;
            private TextView bigtext;
            private ImageView imageSmall;
            private ImageView imageLeft;
            private ImageView imageRight;
            private ImageView imageBigBottom;
            private ConstraintLayout imageLeftWrapper;
            private ConstraintLayout imageRightWrapper;
            public ViewHolder(View view) {
                super(view);
                heading = view.findViewById(R.id.notifentry_heading);
                title = view.findViewById(R.id.notifentry_title);
                text = view.findViewById(R.id.notifentry_text);
                bigtext = view.findViewById(R.id.notifentry_bigtext);
                imageSmall = view.findViewById(R.id.notifentry_image_small);
                imageLeft = view.findViewById(R.id.notifentry_image_left);
                imageRight = view.findViewById(R.id.notifentry_image_right);
                imageBigBottom = view.findViewById(R.id.notifentry_image_bigbottom);
                imageLeftWrapper = view.findViewById(R.id.notifentry_image_left_wrapper);
                imageRightWrapper = view.findViewById(R.id.notifentry_image_right_wrapper);
            }

            public void update(NotificationEntry entry) {
                String headerText = MainApplication.inst.getAppName(entry.app);
                if(entry.subtext != null) {
                    headerText += " \u2022 " + entry.subtext;
                }
                headerText += " \u2022 " + new Date(entry.timestamp).toLocaleString();
                heading.setText(headerText);
                try {
                    Drawable icon;
                    if(entry.smallIcon != null) {
                        icon = new BitmapDrawable(BitmapFactory.decodeByteArray(entry.smallIcon, 0, entry.smallIcon.length));
                        int color = entry.color;
                        if(color == 0) {
                            color = 0xFF707070;
                        }
                        icon.setTint(color);
                    } else {
                        icon = heading.getContext().getPackageManager().getApplicationIcon(entry.app);
                    }
                    imageSmall.setImageDrawable(icon);
                } catch (PackageManager.NameNotFoundException e) {
                    heading.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    e.printStackTrace();
                }
                title.setText(entry.title);
                text.setText(entry.text);

                ImageView activeImage;
                ConstraintLayout activeWrapper;
                imageLeftWrapper.setVisibility(View.GONE);
                imageRightWrapper.setVisibility(View.GONE);
                activeImage = imageRight;
                activeWrapper = imageRightWrapper;

                bigtext.setVisibility(View.GONE);

                if(entry.template != null) {
                    if(entry.template.contains("MessagingStyle")) {
                        activeImage = imageLeft;
                        activeWrapper = imageLeftWrapper;
                    } else if(entry.template.contains("BigTextStyle")) {
                        if(entry.bigtext != null) {
                            int findIndex = entry.bigtext.indexOf(entry.text);
                            if(findIndex == 0) {
                                String txt = entry.bigtext.substring(entry.text.length()).trim();
                                if(txt.length() != 0) {
                                    bigtext.setVisibility(View.VISIBLE);
                                    bigtext.setText(txt);
                                }
                            }
                        }
                    }
                }

                if(entry.largeIcon != null) {
                    activeWrapper.setVisibility(View.VISIBLE);
                    activeImage.setImageBitmap(BitmapFactory.decodeByteArray(entry.largeIcon, 0, entry.largeIcon.length));
                }

                if(entry.picture != null) {
                    imageBigBottom.setVisibility(View.VISIBLE);
                    imageBigBottom.setImageBitmap(BitmapFactory.decodeByteArray(entry.picture, 0, entry.picture.length));
                } else {
                    imageBigBottom.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.history_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(new RecyclerAdapter(MainApplication.db.historyDao().getAll()));

        return view;
    }
}