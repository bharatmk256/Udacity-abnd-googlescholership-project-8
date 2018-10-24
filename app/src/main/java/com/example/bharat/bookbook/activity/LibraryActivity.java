package com.example.bharat.bookbook.activity;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.content.Loader;
import android.net.Uri;
import android.content.CursorLoader;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.bharat.bookbook.R;
import com.example.bharat.bookbook.data.BookContract.BookEntry;

public class LibraryActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, BookCursorAdapter.ProductItemClickListener {

    public static final String LOG_TAG = LibraryActivity.class.getSimpleName();
    private static final int PRODUCT_LOADER = 0;
    public static ListView bookListView;
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        FloatingActionButton fab = findViewById(R.id.add_book_floating);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        bookListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty);
        bookListView.setEmptyView(emptyView);

        mCursorAdapter = new BookCursorAdapter(this, null, this);
        bookListView.setAdapter(mCursorAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LibraryActivity.this, StockActivity.class);
                Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    private void insertBook() {

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, "sprint");
        values.put(BookEntry.COLUMN_PRODUCT_PRICE, 500.7);
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, 10);
        values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Amazon");
        values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, "7878458127");

        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_library, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_data:
                insertBook();
                return true;

            case R.id.delete_all:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.are_you_sure);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllBook();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllBook() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY
        };

        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mCursorAdapter.swapCursor(null);
    }

    @Override
    public int onBookOrder(int position, int newQuantity) {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
        int rowUpdated = getContentResolver().update(Uri.withAppendedPath(BookEntry.CONTENT_URI,
                String.valueOf(bookListView.getItemIdAtPosition(position))), values, null, null);
        return rowUpdated;
    }
}
