package com.example.abhi.bottomsheet;


public class CouponsCardItem {

    private int mTextResource;
    private int mTitleResource;

    public CouponsCardItem(int title, int text) {
        mTitleResource = title;
        mTextResource = text;
    }

    public int getText() {
        return mTextResource;
    }

    public int getTitle() {
        return mTitleResource;
    }
}
