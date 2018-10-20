package com.example.bharat.bookbook.activity;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.content.Loader;
import android.app.LoaderManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bharat.bookbook.R;
import com.example.bharat.bookbook.data.BookContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;

    private Uri mCurrentBookUri;
    private EditText mBookNameEditText;
    private EditText mBookPriceEditText;
    private EditText mBookQuantityEditText;
    private EditText mBookSupplierEditText;
    private EditText mSupplierPhoneEditText;

    private boolean mBookHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();

        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.editor_new_title));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_edit_title));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        mBookNameEditText = (EditText) findViewById(R.id.book_name_edit);
        mBookPriceEditText = (EditText) findViewById(R.id.book_price_edit);
        mBookQuantityEditText = (EditText) findViewById(R.id.book_quantity_edit);
        mBookSupplierEditText = (EditText) findViewById(R.id.supplier_name_edit);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.supplier_phone_edit);

        mBookNameEditText.setOnTouchListener(mTouchListener);
        mBookPriceEditText.setOnTouchListener(mTouchListener);
        mBookQuantityEditText.setOnTouchListener(mTouchListener);
        mBookSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

    }

    private void saveBook() {
        String bookNameString = mBookNameEditText.getText().toString().trim();
        String bookPriceString = mBookPriceEditText.getText().toString().trim();
        String bookQuantityString = mBookQuantityEditText.getText().toString().trim();
        String bookSupplierString = mBookSupplierEditText.getText().toString().trim();
        String bookSupplierPhone = mSupplierPhoneEditText.getText().toString();

        if (mCurrentBookUri == null
                && TextUtils.isEmpty(bookNameString)
                && TextUtils.isEmpty(bookPriceString)
                && TextUtils.isEmpty(bookQuantityString)
                && TextUtils.isEmpty(bookSupplierString)
                && TextUtils.isEmpty(bookSupplierPhone)) {
            Toast.makeText(this, getString(R.string.file_cannot_saved), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (TextUtils.isEmpty(bookNameString)
                || TextUtils.isEmpty(bookPriceString)
                || TextUtils.isEmpty(bookQuantityString)
                || TextUtils.isEmpty(bookSupplierString)
                || TextUtils.isEmpty(bookSupplierPhone)) {
            Toast.makeText(this, getString(R.string.fields_cannot_empty), Toast.LENGTH_SHORT).show();

        } else {
            ContentValues values = new ContentValues();
            values.put(BookContract.BookEntry.COLUMN_PRODUCT_NAME, bookNameString);
            values.put(BookContract.BookEntry.COLUMN_PRODUCT_PRICE, bookPriceString);
            values.put(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY, bookQuantityString);
            values.put(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER, bookSupplierString);
            values.put(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE, bookSupplierPhone);

            if (mCurrentBookUri == null) {
                Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(EditorActivity.this, getString(R.string.failed_to_save), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditorActivity.this, getString(R.string.success_to_save), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.failed_to_update), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.success_to_update), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveBook();
                return true;

            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListner =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListner);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListner =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListner);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_PRODUCT_NAME,
                BookContract.BookEntry.COLUMN_PRODUCT_PRICE,
                BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY,
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

        // Find the columns of book attributes that we're interested in
        int bookNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER);
        int supplierPhoneNoColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE);

        if (cursor.moveToNext()) {
            // Extract out the value from the Cursor for the given column index
            String bookName = cursor.getString(bookNameColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String phoneNo = cursor.getString(supplierPhoneNoColumnIndex);

            // Update the views on the screen with the values from the database
            mBookNameEditText.setText(bookName);
            mBookPriceEditText.setText(String.valueOf(price));
            mBookQuantityEditText.setText(String.valueOf(quantity));
            mBookSupplierEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(phoneNo);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookNameEditText.setText("");
        mBookPriceEditText.setText("");
        mBookQuantityEditText.setText("");
        mBookSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }
}
