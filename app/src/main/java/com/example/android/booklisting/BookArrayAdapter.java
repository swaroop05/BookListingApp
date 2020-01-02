package com.example.android.booklisting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by meets on 12/8/2017.
 */

public class BookArrayAdapter extends ArrayAdapter<BookInfo> {

    private Context mContext;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }
        BookInfo currentInstance = getItem(position);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.author_text_view);
        authorTextView.setText(currentInstance.getmAuthor());
        TextView titleTextView = (TextView) convertView.findViewById(R.id.title_text_view);
        titleTextView.setText(currentInstance.getmTitle());
        ListView listView = (ListView) parent.findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openWebBrowser(getItem(i).getmUrl());
            }
        });
        return convertView;
    }

    /**
     * Constructor for {@link BookArrayAdapter} Class
     * @param context
     * @param resource
     * @param bookInfo
     */
    public BookArrayAdapter(@NonNull Context context, int resource, List<BookInfo> bookInfo) {
        super(context, 0, bookInfo);
        mContext = context;
    }

    /**
     * Opens browser Intent
     * @param url
     */
    public void openWebBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
        }
    }
}
