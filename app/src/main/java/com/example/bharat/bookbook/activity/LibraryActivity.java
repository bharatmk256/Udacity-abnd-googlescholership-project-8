package com.example.bharat.bookbook.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.bharat.bookbook.R;
import com.example.bharat.bookbook.data.BookContract.BookEntry;
import com.example.bharat.bookbook.data.BooksDbHelper;

public class LibraryActivity extends AppCompatActivity {

    private BooksDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        FloatingActionButton floatingActionButton = (FloatingActionButton)
                findViewById(R.id.add_book_floating);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        mDbHelper = new BooksDbHelper(this);
        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        BooksDbHelper mDbHelper = new BooksDbHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] display = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_PRODUCT_SUPPLIER,
                BookEntry.COLUMN_SUPPLIER_PHONE
        };
        Cursor cursor = db.query(
                BookEntry.TABLE_NAME,
                display,
                null,
                null,
                null,
                null,
                null);

        TextView displayView = (TextView) findViewById(R.id.book_list_text);
        try {
            displayView.setText("The books table contains " + cursor.getCount() + "books.\n\n");
            displayView.append(
                    BookEntry._ID + " - " +
                    BookEntry.COLUMN_PRODUCT_NAME + " - " +
                    BookEntry.COLUMN_PRODUCT_PRICE + " - " +
                    BookEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                    BookEntry.COLUMN_PRODUCT_SUPPLIER + " - " +
                    BookEntry.COLUMN_SUPPLIER_PHONE + "\n");

            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
            int productSupplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER);
            int productSupplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                int currentPrice = cursor.getInt(productPriceColumnIndex);
                int currentQuantity = cursor.getInt(productQuantityColumnIndex);
                String currentSupplier = cursor.getString(productSupplierColumnIndex);
                String currentSupplierPhone = cursor.getString(productSupplierPhoneColumnIndex);

                displayView.append(("\n" + currentID + " - " +
                        currentProductName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplier + " - " +
                        currentSupplierPhone));
            }

        } finally {
            cursor.close();
        }
    }
    private void insertBook(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(BookEntry.COLUMN_PRODUCT_NAME, "Head First Java");
        values.put(BookEntry.COLUMN_PRODUCT_PRICE, 100);
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, 20);
        values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER, "Amazon");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "9999999999");

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_library,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.insert_data:
                insertBook();
                displayDatabaseInfo();
                return true;
            case R.id.delete_all:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
