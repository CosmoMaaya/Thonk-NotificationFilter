package com.se390.thonk.ui.util;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewMarginDecoration extends RecyclerView.ItemDecoration {
	public final int marginX, marginY;
	public RecyclerViewMarginDecoration(int marginX, int marginY) {
		this.marginX = marginX;
		this.marginY = marginY;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		int position = parent.getChildLayoutPosition(view);
		if(position != 0) {
			outRect.left = marginX;
			outRect.top = marginY;
		}
	}
}
