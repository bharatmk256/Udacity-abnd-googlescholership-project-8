package com.example.bharat.bookbook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bharat.bookbook.data.BookContract.BookEntry;

public class BooksDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = BooksDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "book_store.db";

    private static final int DATABASE_VERSION = 1;

    public BooksDbHelper(Context context){
        super(context,DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("

                + BookEntry._ID + " INTEGER, "
                + BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT NOT NULL, "
                + BookEntry.COLUMN_SUPPLIER_PHONE + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
