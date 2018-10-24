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
import com.example.bharat.bookbook.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

        private EditText mBookEditText;
        private EditText mPriceEditText;
        private EditText mQuantityEditText;
        private EditText mSupplierNameEditText;
        private EditText mSupplierPhoneNoEditText;

        private Uri mCurrentProductUri;

        private static final int PRODUCT_LOADER = 0;

        private boolean mProductHasChanged = false;

        // OnTouchListener that listens for any user touches on a View, implying that they are modifying
        // the view, and we change the mProductHasChanged boolean to true.
        private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mProductHasChanged = true;
                return false;
            }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_editor);

            Intent intent = getIntent();
            mCurrentProductUri = intent.getData();

            if (mCurrentProductUri == null) {
                setTitle(getString(R.string.add_book));

                // Invalidate the options menu, so the "Delete" menu option can be hidden.
                // (It doesn't make sense to delete a pet that hasn't been created yet.)
                invalidateOptionsMenu();
            } else {
                setTitle(getString(R.string.edit_book));
                getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
            }

            mBookEditText = findViewById(R.id.book_name_edit);
            mPriceEditText = findViewById(R.id.book_price_edit);
            mQuantityEditText = findViewById(R.id.book_quantity_edit);
            mSupplierNameEditText = findViewById(R.id.supplier_name_edit);
            mSupplierPhoneNoEditText = findViewById(R.id.supplier_phone_edit);

            mBookEditText.setOnTouchListener(mTouchListener);
            mPriceEditText.setOnTouchListener(mTouchListener);
            mQuantityEditText.setOnTouchListener(mTouchListener);
            mSupplierNameEditText.setOnTouchListener(mTouchListener);
            mSupplierPhoneNoEditText.setOnTouchListener(mTouchListener);

        }

        //Get user inout from editor and save new book into database.
        public void saveBook() {
            // Read from input fields.
            String bookString = mBookEditText.getText().toString().trim();
            String priceString = mPriceEditText.getText().toString().trim();
            String quantityString = mQuantityEditText.getText().toString().trim();
            String supplierNameString = mSupplierNameEditText.getText().toString().trim();
            String supplierPhoneNoString = mSupplierPhoneNoEditText.getText().toString().trim();

            // Check if this is supposed to be a new book
            // and check if all the fields in the editor are blank.
            if (mCurrentProductUri == null
                    && TextUtils.isEmpty(bookString)
                    && TextUtils.isEmpty(priceString)
                    && TextUtils.isEmpty(quantityString)
                    && TextUtils.isEmpty(supplierNameString)
                    && TextUtils.isEmpty(supplierPhoneNoString)) {
                Toast.makeText(this, getString(R.string.cant_save_empty_book), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Check if this is supposed to be a new book
            // and check if all the fields in the editor are blank.
            if (TextUtils.isEmpty(bookString) || TextUtils.isEmpty(priceString) ||
                    TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supplierNameString) ||
                    TextUtils.isEmpty(supplierPhoneNoString)) {
                Toast.makeText(this, getString(R.string.fields_cant_be_empty), Toast.LENGTH_SHORT).show();
            } else {
                // Create a ContentValues object where column names are the keys,
                // and pet attributes from the editor are the values.
                ContentValues values = new ContentValues();
                values.put(BookEntry.COLUMN_PRODUCT_NAME, bookString);
                values.put(BookEntry.COLUMN_PRODUCT_PRICE, priceString);
                values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
                values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);
                values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneNoString);

                if (mCurrentProductUri == null) {
                    Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
                    if (newUri == null) {
                        Toast.makeText(EditorActivity.this, getString(R.string.editor_insert_product_failed), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditorActivity.this, getString(R.string.editor_insert_product_successful), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentProductUri
                    // and pass in the new ContentValues. Pass in null for the selection and selection args
                    // because mCurrentProductUri will already identify the correct row in the database that
                    // we want to modify.
                    int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(this, getString(R.string.editor_update_product_failed),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Otherwise, the update was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_update_product_successful),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        }

        @Override
        public boolean onPrepareOptionsMenu(Menu menu) {
            super.onPrepareOptionsMenu(menu);
            // If this is a new product, hide the "Delete" menu item.
            if (mCurrentProductUri == null) {
                MenuItem menuItem = menu.findItem(R.id.delete);
                menuItem.setVisible(false);
            }
            return true;
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu options from the res/menu/menu_catalog.xml file.
            // This adds menu items to the app bar.
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

                // Respond to a click on the "Up" arrow button in the app bar
                case android.R.id.home:
                    // If the product hasn't changed, continue with navigating up to parent activity
                    // which is the {@link CatalogActivity}.
                    if (!mProductHasChanged) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        return true;
                    }

                    // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                    // Create a click listener to handle the user confirming that
                    // changes should be discarded.
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked "Discard" button, navigate to parent activity.
                                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                }
                            };

                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;

            }
            return super.onOptionsItemSelected(item);
        }

        private void showUnsavedChangesDialog(
                DialogInterface.OnClickListener discardButtonClickListener) {
            // Create an AlertDialog.Builder and set the message, and click listeners
            // for the positive and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.unsaved_changes_dialog_msg);
            builder.setPositiveButton(R.string.discard, discardButtonClickListener);
            builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Keep editing" button, so dismiss the dialog
                    // and continue editing the pet.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        private void showDeleteConfirmationDialog() {
            // Create an AlertDialog.Builder and set the message, and click listeners
            // for the postivie and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_dialog_msg);
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Delete" button, so delete the pet.
                    deleteBook();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Cancel" button, so dismiss the dialog
                    // and continue editing the pet.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        /**
         * Perform the deletion of the book in the database.
         */
        private void deleteBook() {
            // Only perform the delete if this is an existing product.
            if (mCurrentProductUri != null) {
                // Call the ContentResolver to delete the product at the given content URI.
                // Pass in null for the selection and selection args because the mCurrentPetUri
                // content URI already identifies the pet that we want.
                int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
                // Show a toast message depending on whether or not the delete was successful.
                if (rowsDeleted == 0) {
                    // If no rows were deleted, then there was an error with the delete.
                    Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the delete was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_delete_procduct_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
            // Close the activity
            finish();
        }

        @Override
        public void onBackPressed() {
            // If the pet hasn't changed, continue with handling back button press
            if (!mProductHasChanged) {
                super.onBackPressed();
                return;
            }

            // Otherwise if there are unsaved changes, setup a dialog to warn the user.
            // Create a click listener to handle the user confirming that changes should be discarded.
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, close the current activity.
                            finish();
                        }
                    };

            // Show dialog that there are unsaved changes
            showUnsavedChangesDialog(discardButtonClickListener);
        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // Since the editor shows all book attributes, define a projection that contains
            // all columns from the pet table
            String[] projection = {
                    BookEntry._ID,
                    BookEntry.COLUMN_PRODUCT_NAME,
                    BookEntry.COLUMN_PRODUCT_PRICE,
                    BookEntry.COLUMN_PRODUCT_QUANTITY,
                    BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                    BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
            };
            // This loader will execute the ContentProvider's query method on a background thread
            return new CursorLoader(this,   // Parent activity context
                    mCurrentProductUri,         // Query the content URI for the current product
                    projection,             // Columns to include in the resulting Cursor
                    null,                   // No selection clause
                    null,                   // No selection arguments
                    null);                  // Default sort order
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            // Bail early if the cursor is null or there is less than 1 row in the cursor
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
                String phoneNo = cursor.getString(supplierPhoneNoColumnIndex);

                // Update the views on the screen with the values from the database
                mBookEditText.setText(bookName);
                mPriceEditText.setText(String.valueOf(price));
                mQuantityEditText.setText(String.valueOf(quantity));
                mSupplierNameEditText.setText(supplierName);
                mSupplierPhoneNoEditText.setText(phoneNo);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // If the loader is invalidated, clear out all the data from the input fields.
            mBookEditText.setText("");
            mPriceEditText.setText("");
            mQuantityEditText.setText("");
            mSupplierNameEditText.setText("");
            mSupplierPhoneNoEditText.setText("");
        }
    }
