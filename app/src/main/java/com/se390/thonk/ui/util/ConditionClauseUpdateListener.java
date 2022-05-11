package com.se390.thonk.ui.util;

import android.view.View;

public interface ConditionClauseUpdateListener<T> {
	void delete(View view);
	void update(View view, T newContent);
}
