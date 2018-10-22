package com.example.bharat.bookbook.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bharat.bookbook.R;
import com.example.bharat.bookbook.data.BookContract;

public class StockActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int BOOK_LOADER = 0;
    private Uri mCurrentBookUri;
    private TextView mBookTextView;
    private TextView mPriceTextView;
    private TextView mQuantityTextView;
    private TextView mSupplierNameTextView;
    private TextView mSupplierPhoneTextView;
    private Button mIncrementButton;
    private Button mDecrementButton;
    private int quantityToBeChanged = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        mBookTextView = findViewById(R.id.book_name_text);
        mPriceTextView = findViewById(R.id.book_price_text);
        mSupplierNameTextView = findViewById(R.id.supplier_name_text);
        mSupplierPhoneTextView = findViewById(R.id.supplier_phone_text);
        mQuantityTextView = findViewById(R.id.book_quantity_text);
        mIncrementButton = findViewById(R.id.book_quantity_plus);
        mDecrementButton = findViewById(R.id.book_quantity_minus);

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_book:
                Intent intent = new Intent(StockActivity.this, EditorActivity.class);
                intent.setData(mCurrentBookUri);
                startActivity(intent);
                finish();
                return true;
            case R.id.delete_book:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(StockActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_book_success), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_PRODUCT_NAME,
                BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookContract.BookEntry.COLUMN_PRODUCT_PRICE,
                BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER,
                BookContract.BookEntry.COLUMN_SUPPLIER_PHONE
        };
        return new CursorLoader(
                this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        int bookNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        int bookQuantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY);
        int bookPriceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
        int bookSupplierColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER);
        int supplierPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE);

        if (cursor.moveToNext()) {
            String bookName = cursor.getString(bookNameColumnIndex);
            int bookQuantity = cursor.getInt(bookQuantityColumnIndex);
            Float bookPrice = cursor.getFloat(bookPriceColumnIndex);
            String bookSupplier = cursor.getString(bookSupplierColumnIndex);
            final String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            mBookTextView.setText(bookName);
            mQuantityTextView.setText(bookQuantity);
            mPriceTextView.setText(String.valueOf(bookPrice));
            mSupplierNameTextView.setText(bookSupplier);
            mSupplierPhoneTextView.setText(supplierPhone);

            mIncrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String quantity = mQuantityTextView.getText().toString();
                    int newQuantity = Integer.valueOf(quantity) - quantityToBeChanged;
                    if (newQuantity >= 0) {
                        ContentValues values = new ContentValues();
                        values.put(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                        getContentResolver().update(mCurrentBookUri, values, null, null);
                        mQuantityTextView.setText(String.valueOf(newQuantity));
                    } else {
                        Toast.makeText(StockActivity.this, R.string.books_less_then_0, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookTextView.setText("");
        mQuantityTextView.setText("");
        mPriceTextView.setText("");
        mSupplierNameTextView.setText("");
        mSupplierPhoneTextView.setText("");
    }
}
