package com.se390.thonk.ui.activity;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.se390.thonk.MainApplication;
import com.se390.thonk.R;
import com.se390.thonk.data.RuleCondition;
import com.se390.thonk.ui.view.RuleConditionView;

import java.util.ArrayList;
import java.util.List;

public class EditRuleActivity extends AppCompatActivity {

    public static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        public List<RuleCondition> conditions;

        public RecyclerAdapter(List<RuleCondition> conditions) {
            this.conditions = conditions;
        }

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerAdapter.ViewHolder(new RuleConditionView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
            holder.update(conditions.get(position));
        }

        @Override
        public int getItemCount() {
            return conditions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public RuleConditionView view;
            public ViewHolder(RuleConditionView view) {
                super(view);
                this.view = view;
            }

            public void update(RuleCondition condition) {
                view.setData(condition);
                view.setOnDeleteListener((v) -> {
                    int position = getAdapterPosition();
                    conditions.remove(position);
                    notifyItemRemoved(position);
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rule);
        RecyclerView conditionsRecycler = findViewById(R.id.editrule_conditions);
//        ((ViewGroup) conditionsRecycler).getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        Button addConditionButton = findViewById(R.id.editrule_add_condition);
        Button addActionButton = findViewById(R.id.editrule_add_action);
        RecyclerView actionsRecycler = findViewById(R.id.editrule_actions);
        conditionsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        List<RuleCondition> conditions = new ArrayList<>();
        RecyclerAdapter adapter = new RecyclerAdapter(conditions);
        conditionsRecycler.setAdapter(adapter);
        addConditionButton.setOnClickListener((view) -> {
            conditions.add(new RuleCondition());
            adapter.notifyItemInserted(conditions.size() - 1);
        });
    }
}