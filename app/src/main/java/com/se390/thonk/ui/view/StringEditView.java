package com.se390.thonk.ui.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageButton;

import com.se390.thonk.R;
import com.se390.thonk.ui.util.ConditionClauseUpdateListener;

import androidx.constraintlayout.widget.ConstraintLayout;

public class StringEditView extends ConstraintLayout {
    public StringEditView(Context context) {
        super(context);
        init();
    }

    public StringEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StringEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public StringEditView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public EditText textField;
    public ImageButton deleteButton;

    private ConditionClauseUpdateListener<String> listener;

    private void init() {
        inflate(getContext(), R.layout.view_string_edit, this);
        textField = findViewById(R.id.view_string_edit_text);
        deleteButton = findViewById(R.id.view_string_edit_delete);
        textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(listener != null) {
                    listener.update(StringEditView.this, s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        deleteButton.setOnClickListener((v) -> {
            if(listener != null) {
                listener.delete(this);
            }
        });
    }

    public void setOnUpdateListener(ConditionClauseUpdateListener<String> listener) {
        this.listener = listener;
    }

    public void setText(String text) {
        textField.setText(text);
    }
}
