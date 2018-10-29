package com.example.bharat.store.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.bharat.store.data.ProductContract.ProductEntry;

public class ProductProvider extends ContentProvider {
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    private static final int BOOKS = 100;

    private static final int BOOK_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    private ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Book requires a name");
        }


        Float price = values.getAsFloat(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Book requires valid price.");
        }

        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Books requires valid quantity.");
        }

        String supplierName = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Book requires a supplier name.");
        }

        String supplierPhoneNumber = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        if (supplierPhoneNumber == null) {
            throw new IllegalArgumentException("Book requires a valid supplier phone number");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ProductEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
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
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String bookName = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (bookName == null) {
                throw new IllegalArgumentException("Book requires a name");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Float price = values.getAsFloat(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Books require valid price");
            }
        }

        if(values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Books requires valid quantity.");
            }
        }

        if(values.containsKey(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Book requires a supplier name.");
            }
        }

        if(values.containsKey(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumber = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            if (supplierPhoneNumber == null) {
                throw new IllegalArgumentException("Book requires a valid supplier phone number");
            }
        }

        if(values.size() == 0){
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

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
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
