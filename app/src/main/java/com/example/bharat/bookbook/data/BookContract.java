package com.example.bharat.bookbook.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {

    private BookContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.bharat.bookbook";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "book";

    public static final class BookEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public final static String TABLE_NAME = "books";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "Product_Name";
        public final static String COLUMN_PRODUCT_PRICE = "Price";
        public final static String COLUMN_PRODUCT_QUANTITY = "Quantity";
        public final static String COLUMN_PRODUCT_SUPPLIER = "Supplier_Name";
        public final static String COLUMN_SUPPLIER_PHONE = "Supplier_Phone_Number";
    }

}
