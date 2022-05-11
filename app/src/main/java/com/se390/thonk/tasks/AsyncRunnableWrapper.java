package com.se390.thonk.tasks;

import android.os.AsyncTask;

public class AsyncRunnableWrapper extends AsyncTask<Object, Object, Object> {
    private final Runnable runnable;

    public AsyncRunnableWrapper(Runnable runnable) {
        this.runnable = runnable;
    }
    @Override
    protected Object doInBackground(Object... objects) {
        runnable.run();
        return null;
    }

    public void start() {
        execute((Object[]) null);
    }
}
