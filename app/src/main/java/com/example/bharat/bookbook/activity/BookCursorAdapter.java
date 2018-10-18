package com.example.bharat.bookbook.activity;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bharat.bookbook.R;
import com.example.bharat.bookbook.data.BookContract;

public class BookCursorAdapter extends CursorAdapter {

    private Context mContext;
    private ProductItemClickListener mListner;

    public BookCursorAdapter(Context context, Cursor c, ProductItemClickListener listener){
        super(context, c,0);
        mContext = context;
        mListner = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.library_list_iteam,parent,false);
    }

    @Override
    public void bindView(final View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY);

        String bookName = cursor.getString(nameColumnIndex);
        int bookPrice = cursor.getInt(priceColumnIndex);
        int bookquantity = cursor.getInt(quantityColumnIndex);

        nameTextView.setText(bookName);
        priceTextView.setText(String.valueOf(bookPrice));
        quantityTextView.setText(String.valueOf(bookquantity));

        Button saleButton = view.findViewById(R.id.order_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = quantityTextView.getText().toString();
                if(Integer.valueOf(quantity) >0){
                    int newQuantity = Integer.valueOf(quantity)-1;
                    View linearLayout = (View) view.getParent();
                    ListView listView = (ListView) linearLayout.getParent();

                    int rows = mListner.onBookOrder(listView.getPositionForView(view),newQuantity);
                    if (rows > 0){
                        Toast.makeText(mContext, R.string.book_sold, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(mContext,R.string.book_have_not_sold, Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(mContext,R.string.out_of_stock, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public interface ProductItemClickListener{
        int onBookOrder(int position, int newQuantity);
    }
}
