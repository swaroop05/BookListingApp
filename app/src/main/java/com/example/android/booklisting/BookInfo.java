package com.example.android.booklisting;

/**
 * Created by meets on 12/8/2017.
 */

public class BookInfo {
    private String mAuthor;
    private String mTitle;
    private String mUrl;

    /**
     * constructor for {@link BookInfo} class
     */
    public BookInfo(String author, String title, String url) {
        mAuthor = author;
        mTitle = title;
        mUrl = url;
    }

    /**
     * Getter for mAuthor
     */
    public String getmAuthor() {
        return mAuthor;
    }

    /**
     * Getter for mAuthor
     */
    public String getmTitle() {
        return mTitle;
    }

    /**
     * Getter for mUrl
     */
    public String getmUrl() {
        return mUrl;
    }

}
