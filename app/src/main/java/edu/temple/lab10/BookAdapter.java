package edu.temple.lab10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BookAdapter extends BaseAdapter {

    Context context;
    BookList bookList;

    public BookAdapter(Context context, BookList bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    public void update(BookList books) {
        bookList = books;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return bookList.getSize();
    }

    @Override
    public Object getItem(int position) {
        return bookList.getBook(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;

        if ((view = (View) convertView) == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false);
        }

        TextView title = view.findViewById(R.id.titleText);
        TextView author = view.findViewById(R.id.authorText);

        title.setTextSize(22);
        author.setTextSize(18);

        title.setPadding(25, 10, 0, 5);
        author.setPadding(25, 0, 0, 10);

        title.setText(((Book) getItem(position)).getTitle());
        author.setText(((Book) getItem(position)).getAuthor());

        return view;
    }
}
