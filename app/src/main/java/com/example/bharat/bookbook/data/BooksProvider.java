package com.example.bharat.bookbook.data;

import android.app.AuthenticationRequiredException;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.AdapterView;

public class BooksProvider extends ContentProvider {

    public static final String LOG_TAG = BooksProvider.class.getSimpleName();

    private static final int BOOKS = 100;

    private static final int BOOK_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);

        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    private BooksDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BooksDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {

            case BOOKS:
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown Uri" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBOOK(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBOOK(Uri uri, ContentValues values) {
        String name = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Book Name ??");
        }
        Integer price = values.getAsInteger(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
        if (price == null) {
            if (price <= 0) {
                throw new IllegalArgumentException("Book Price should not be 0 or less");
            }
            throw new IllegalArgumentException("Books Price ??");
        }
        Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Please add Quantity");
        }
        String supplier = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier name should not be empty");
        }
        Integer supplierPhone = values.getAsInteger(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE);
        if (supplierPhone == null) {
            throw new IllegalArgumentException("Supplier phone Number please");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert book try again" + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return update(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return update(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book Name ??");
            }
        }
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Books Price ??");
            }
        }
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Please add Quantity");
            }
        }
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER)) {
            String supplier = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Supplier name should not be empty");
            }
        }
        if (values.containsKey(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE)) {
            Integer supplierPhone = values.getAsInteger(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE);
            if (supplierPhone == null) {
                throw new IllegalArgumentException("Supplier Phone Number Please");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

}
