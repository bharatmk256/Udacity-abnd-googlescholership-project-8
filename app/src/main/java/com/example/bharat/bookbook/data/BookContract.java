package com.example.bharat.bookbook.data;

import android.provider.BaseColumns;

public final class BookContract {

    private BookContract(){}

    public static final class BookEntry implements BaseColumns{
        public final static String TABLE_NAME = "books";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "Product Name";
        public final static String COLUMN_PRODUCT_PRICE = "Price";
        public final static String COLUMN_PRODUCT_QUANTITY = "Quantity";
        public final static String COLUMN_PRODUCT_SUPPLIER = "Supplier Name";
        public final static String COLUMN_SUPPLIER_PHONE = "Supplier Phone Number";
    }

}
