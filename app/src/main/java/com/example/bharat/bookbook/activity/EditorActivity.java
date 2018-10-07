package com.example.bharat.bookbook.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bharat.bookbook.R;
import com.example.bharat.bookbook.data.BookContract;
import com.example.bharat.bookbook.data.BooksDbHelper;

import static com.example.bharat.bookbook.data.BookContract.BookEntry.COLUMN_PRODUCT_NAME;

public class EditorActivity extends AppCompatActivity {

    private EditText mBookNameEditText;

    private EditText mBookPriceEditText;

    private EditText mBookQuantityEditText;

    private EditText mBookSupplierEditText;

    private EditText mSupplierPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mBookNameEditText = (EditText) findViewById(R.id.book_name_edit);
        mBookPriceEditText = (EditText) findViewById(R.id.book_price_edit);
        mBookQuantityEditText = (EditText) findViewById(R.id.book_quantity_edit);
        mBookSupplierEditText = (EditText) findViewById(R.id.supplier_name_edit);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.supplier_phone_edit);
    }

    private void insertBook(){
        String bookNameString = mBookNameEditText.getText().toString().trim();
        String bookPriceString = mBookPriceEditText.getText().toString().trim();
        String bookQuantityString = mBookQuantityEditText.getText().toString().trim();
        String bookSupplierString = mBookSupplierEditText.getText().toString().trim();
        String bookSupplierPhone = mSupplierPhoneEditText.getText().toString();

        int price = Integer.parseInt(bookPriceString);
        int quantity = Integer.parseInt(bookQuantityString);
        long phone = Long.parseLong(bookSupplierPhone);

        BooksDbHelper mDbHelper = new BooksDbHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();

        values.put(BookContract.BookEntry.COLUMN_PRODUCT_NAME, bookNameString);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER, bookSupplierString);
        values.put(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE, phone);

        long newRowId = db.insert(BookContract.BookEntry.TABLE_NAME,null,values);

        if (newRowId == -1){
            Toast.makeText(this,"Error with Saving Book",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Book Added To Store",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.save:
                insertBook();
                finish();
                return true;

            case R.id.delete:
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
