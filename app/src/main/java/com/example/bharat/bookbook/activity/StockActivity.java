package com.example.bharat.bookbook.activity;

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
import com.example.bharat.bookbook.data.BookContract.BookEntry;

public class StockActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;
    private TextView mBookTextView;
    private TextView mPriceTextView;
    private TextView mSupplierNameTextView;
    private TextView mSupplierPhoneNoTextView;
    private TextView mQuantityTextView;
    private Button mOrderButton;
    private Button mIncrementButton;
    private Button mDecrementButton;
    private int quantityToBeChanged = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        mBookTextView =             findViewById(R.id.book_name_text);
        mPriceTextView =            findViewById(R.id.book_price_text);
        mSupplierNameTextView =     findViewById(R.id.supplier_name_text);
        mSupplierPhoneNoTextView =  findViewById(R.id.supplier_phone_text);
        mQuantityTextView =         findViewById(R.id.book_quantity_text);
        mOrderButton =              findViewById(R.id.order_button);
        mIncrementButton =          findViewById(R.id.book_quantity_plus);
        mDecrementButton =          findViewById(R.id.book_quantity_minus);

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_book:
                Intent intent = new Intent(StockActivity.this, EditorActivity.class);
                intent.setData(mCurrentProductUri);
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

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_procduct_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Find the columns of book attributes that we're interested in
        int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        int supplierPhoneNoColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

        if (cursor.moveToNext()) {
            // Extract out the value from the Cursor for the given column index
            String bookName = cursor.getString(bookNameColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            final String phoneNo = cursor.getString(supplierPhoneNoColumnIndex);

            // Update the views on the screen with the values from the database
            mBookTextView.setText(bookName);
            mPriceTextView.setText("₹ " + String.valueOf(price));
            mQuantityTextView.setText(String.valueOf(quantity));
            mSupplierNameTextView.setText(supplierName);
            mSupplierPhoneNoTextView.setText(phoneNo);

            mIncrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String quantity = mQuantityTextView.getText().toString();
                    int newQuantity = Integer.valueOf(quantity) + quantityToBeChanged;
                    if (newQuantity >= 0) {
                        ContentValues values = new ContentValues();
                        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                        getContentResolver().update(mCurrentProductUri, values, null, null);
                        mQuantityTextView.setText(String.valueOf(newQuantity));
                    }
                }
            });

            mDecrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String quantity = mQuantityTextView.getText().toString();
                    int newQuantity = Integer.valueOf(quantity) - quantityToBeChanged;
                    if (newQuantity >= 0) {
                        ContentValues values = new ContentValues();
                        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                        getContentResolver().update(mCurrentProductUri, values, null, null);
                        mQuantityTextView.setText(String.valueOf(newQuantity));
                    } else {
                        Toast.makeText(StockActivity.this, R.string.book_cant_be_neative, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "tel:" + phoneNo;
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                }
            });

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mBookTextView.setText("");
        mPriceTextView.setText("");
        mQuantityTextView.setText("");
        mSupplierNameTextView.setText("");
        mSupplierPhoneNoTextView.setText("");
    }

}