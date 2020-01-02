package com.example.android.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.booklisting.QueryUtils.LOG_TAG;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<BookInfo>> {


    public ProgressBar mProgressBar;
    public static String URL = null;
    private TextView mEmptyStateTextView;
    TextView noInternetTextView;
    EditText mSearchTextView;
    ListView mBooksInfoListView;
    static Parcelable STATE;
    public boolean isConnectedOnLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchTextView = (EditText) findViewById(R.id.search_edit_text_view);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        mBooksInfoListView = (ListView) findViewById(R.id.list);
        noInternetTextView = (TextView) findViewById(R.id.no_internet);
        mProgressBar.setVisibility(View.GONE);
        if (QueryUtils.mJsonResponse != null) {
            Log.d(LOG_TAG, "Swaroop: initLoader Method is called now");
            getLoaderManager().restartLoader(0, null, MainActivity.this);
        }

            Button searchButton = (Button) findViewById(R.id.search_buton);
            //Click Listener for Search button
            searchButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    noInternetTextView.setVisibility(View.GONE);
                                                    mEmptyStateTextView.setVisibility(View.GONE);
                                                    ConnectivityManager cm =
                                                            (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                                                    final boolean isConnected = activeNetwork != null &&
                                                            activeNetwork.isConnectedOrConnecting();
                                                    if (isConnected){
                                                    mProgressBar.setVisibility(View.VISIBLE);
                                                    STATE = null;
                                                    String searchText = mSearchTextView.getText().toString();
                                                    String formattedSearchText = searchText.replace(" ", "+");
                                                    URL = "https://www.googleapis.com/books/v1/volumes?q=";
                                                    URL = URL + formattedSearchText;
                                                    URL = URL + "&maxResults=20";
                                                    Log.d(LOG_TAG, "Swaroop: restartLoader Method is called now");
                                                    getLoaderManager().restartLoader(0, null, MainActivity.this);
                                                    }else{
                                                        mProgressBar.setVisibility(View.GONE);
                                                        mEmptyStateTextView.setVisibility(View.GONE);
                                                        noInternetTextView.setVisibility(View.VISIBLE);
                                                        noInternetTextView.setText(R.string.no_internet);
                                                    }
                                                }
                                            }
            );

    }

    @Override
    protected void onPause() {
        super.onPause();
        STATE = mBooksInfoListView.onSaveInstanceState();
        Log.d(LOG_TAG, "Swaroop: onPause Method is called now");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnectedOnLaunch = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnectedOnLaunch){
            noInternetTextView.setVisibility(View.GONE);
        }
        Log.d(LOG_TAG, "Swaroop: onResume Method is called now");
    }

    /**
     * updates UI with List Object of {@link BookInfo}
     * @param bookInfo
     */
    public void updateUI(List<BookInfo> bookInfo) {
        BookArrayAdapter bookArrayAdapter = new BookArrayAdapter(this, R.color.colorAccent, bookInfo);
        mBooksInfoListView.setAdapter(bookArrayAdapter);
        if (STATE != null) {
            mBooksInfoListView.onRestoreInstanceState(STATE);
        }
    }

    @Override
    public Loader<List<BookInfo>> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "Swaroop: onCreateLoader Method is called now");
        return new BookInfoLoader(MainActivity.this, URL);
    }

    @Override
    public void onLoadFinished(Loader<List<BookInfo>> loader, List<BookInfo> data) {
        Log.d(LOG_TAG, "Swaroop: onLoadFinished Method is called now");
        mProgressBar.setVisibility(View.GONE);

        if (data == null) {
            updateUI(new ArrayList<BookInfo>());
        } else if (data.size() == 0) {
            if (isConnectedOnLaunch){
                noInternetTextView.setVisibility(View.GONE);
                updateUI(new ArrayList<BookInfo>());
                mEmptyStateTextView.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setText(R.string.no_books_found);


            }else{
                updateUI(new ArrayList<BookInfo>());
                mEmptyStateTextView.setVisibility(View.GONE);
                noInternetTextView.setVisibility(View.VISIBLE);
                noInternetTextView.setText(R.string.no_internet);
            }

        } else{
            noInternetTextView.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.GONE);
            updateUI(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<BookInfo>> loader) {
        Log.d(LOG_TAG, "Swaroop: OnLoaderReset Method is called now");
        updateUI(new ArrayList<BookInfo>());
    }
}
