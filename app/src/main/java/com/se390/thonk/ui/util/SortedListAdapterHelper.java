package com.se390.thonk.ui.util;

import java.util.Comparator;
import java.util.Objects;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

public class SortedListAdapterHelper<T> extends SortedList.Callback<T> {

	public final RecyclerView.Adapter<?> adapter;
	private final Comparator<T> comparator;

	public SortedListAdapterHelper(RecyclerView.Adapter<?> adapter, Comparator<T> comparator) {
		this.adapter = adapter;
		this.comparator = comparator;
	}

	@Override
	public int compare(T o1, T o2) {
		return comparator.compare(o1, o2);
	}

	@Override
	public boolean areContentsTheSame(T oldItem, T newItem) {
		return areItemsTheSame(oldItem, newItem);
	}

	@Override
	public boolean areItemsTheSame(T item1, T item2) {
		return Objects.equals(item1, item2);
	}

	@Override
	public void onChanged(int position, int count) {
		adapter.notifyItemRangeChanged(position, count);
	}

	@Override
	public void onInserted(int position, int count) {
		adapter.notifyItemRangeInserted(position, count);
	}

	@Override
	public void onRemoved(int position, int count) {
		adapter.notifyItemRangeRemoved(position, count);
	}

	@Override
	public void onMoved(int fromPosition, int toPosition) {
		adapter.notifyItemMoved(fromPosition, toPosition);
	}
}
