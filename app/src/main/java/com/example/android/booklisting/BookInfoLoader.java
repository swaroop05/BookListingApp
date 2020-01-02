package com.example.android.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by meets on 12/8/2017.
 */

public class BookInfoLoader extends AsyncTaskLoader<List<BookInfo>> {

    String mURL;
    public Context mContext;

    @Override
    public List<BookInfo> loadInBackground() {
        // Don't perform the request if there are no URLs.
        if (mURL == null) {
            return null;
        }
        return   QueryUtils.fetchBookInfos(mURL);
    }

    public BookInfoLoader(Context context, String mURL) {
        super(context);
        this.mURL = mURL;
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
