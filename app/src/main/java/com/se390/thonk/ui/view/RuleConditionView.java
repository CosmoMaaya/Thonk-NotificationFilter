package com.se390.thonk.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.se390.thonk.R;
import com.se390.thonk.data.RuleCondition;
import com.se390.thonk.ui.util.ConditionClauseUpdateListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Consumer;
import androidx.core.util.Supplier;
import androidx.recyclerview.widget.RecyclerView;

public class RuleConditionView extends ConstraintLayout {
	public List<String> clauses = new ArrayList<>();

	public RuleConditionView(Context context) {
		super(context);
		init();
	}

	public RuleConditionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RuleConditionView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public RuleConditionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	public final int
			FIELD_TITLE = 0,
			FIELD_CONTENT = 1,
			FIELD_APP = 2,
			FIELD_DATE = 3,
			FIELD_TIME = 4;

	public Spinner spinnerField;
	public Spinner spinnerType;
	public RecyclerView layoutClauses;
	public Button buttonDelete;
	public Button buttonAddClause;

	private void init() {
		inflate(getContext(), R.layout.view_rule_condition, this);

		spinnerField = findViewById(R.id.rule_condition_entry_match_field);
		spinnerType = findViewById(R.id.rule_condition_entry_match_type);
		layoutClauses = findViewById(R.id.rule_condition_entry_clauses);
		buttonDelete = findViewById(R.id.rule_condition_entry_delete);
		buttonAddClause = findViewById(R.id.rule_condition_entry_add);

//		ArrayAdapter<CharSequence> appTypeAdapter = generateAdapter.apply(R.array.editrule_condition_type_app);
//		ArrayAdapter<CharSequence> datetimeTypeAdapter = generateAdapter.apply(R.array.editrule_condition_type_datetime);

		List<Supplier<TypeHandler>> handlers = Arrays.asList(
			StringTypeHandler::new,
			StringTypeHandler::new,
			AppTypeHandler::new,
			StringTypeHandler::new,
			StringTypeHandler::new
		);

		spinnerField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				TypeHandler handler = handlers.get(position).get();
				handler.selected();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		data.strings.add("asdf");
		data.strings.add("asdffff");
	}

	public void findIndex(View view, Consumer<Integer> found) {
		int ind = views.indexOf(view);
		if(ind != -1) {
			found.accept(ind);
		}
	}

	public void deleteView(int index) {
//		layoutClauses.removeViewAt(index);
		views.remove(index);
	}

	public void addView(View view) {
//		layoutClauses.addView(view);
		views.add(view);
	}

	public RuleCondition data = new RuleCondition();
	public List<View> views = new ArrayList<>();

	public void setData(RuleCondition condition) {
		this.data = condition;
	}

	public void setOnDeleteListener(OnClickListener listener) {
		buttonDelete.setOnClickListener(listener);
	}

    public static class RecyclerAdapter<T, V extends View> extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        public List<T> entries;
        public TypeHandler handler;

        public RecyclerAdapter(List<T> entries, TypeHandler handler) {
            this.entries = entries;
            this.handler = handler;
        }

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerAdapter.ViewHolder(new RuleConditionView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
            holder.update(entries.get(position));
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public V view;
            public ViewHolder(V view) {
                super(view);
                this.view = view;
            }

            public void update(T entry) {
            	handler.updateview();
            }
        }
    }


	public abstract class TypeHandler {
		public final ArrayAdapter<CharSequence> adapter;

		public TypeHandler(int res) {
			adapter = ArrayAdapter.createFromResource(getContext(), res, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		}

		public abstract void selected();
		public abstract boolean canAddClause();

	}

	public class StringTypeHandler extends TypeHandler {
		public StringTypeHandler() {
			super(R.array.editrule_condition_type_string);
		}

		@Override
		public void selected() {
			layoutClauses.removeAllViews();
			spinnerType.setAdapter(adapter);
			for(String s : data.strings) {
				StringEditView view = new StringEditView(getContext());
				view.setText(s);
				view.setOnUpdateListener(new ConditionClauseUpdateListener<String>() {
					@Override
					public void delete(View view) {
						findIndex(view, (i) -> {
							deleteView(i);
							data.strings.remove((int) i);
						});
					}

					@Override
					public void update(View view, String newContent) {
						findIndex(view, (i) -> data.strings.set(i, newContent));
					}
				});
				addView(view);
			}
		}

		@Override
		public boolean canAddClause() {
			return true;
		}
	}

	public class AppTypeHandler extends TypeHandler {
		public AppTypeHandler() {
			super(R.array.editrule_condition_type_app);
		}

		@Override
		public void selected() {
			layoutClauses.removeAllViews();
			spinnerType.setAdapter(adapter);
			AppListView view = new AppListView(getContext());
			layoutClauses.addView(view);
		}

		@Override
		public boolean canAddClause() {
			return false;
		}
	}
}
