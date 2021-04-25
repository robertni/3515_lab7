package edu.temple.lab10;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class BookList implements Parcelable {

    private ArrayList<Book> bookList;

    public BookList() {
        bookList = new ArrayList<Book>();
    }

    protected BookList(Parcel in) {
        bookList = in.createTypedArrayList(Book.CREATOR);
    }

    public static final Creator<BookList> CREATOR = new Creator<BookList>() {
        @Override
        public BookList createFromParcel(Parcel in) {
            return new BookList(in);
        }

        @Override
        public BookList[] newArray(int size) {
            return new BookList[size];
        }
    };

    public void clear() {
        bookList.clear();
    }

    public void addAll(BookList books) {
        for (int i = 0; i < books.getSize(); i++) {
            bookList.add(books.getBook(i));
        }
    }

    public void addBook(Book book) {
        bookList.add(book);
    }

    public void removeBook(Book book) {
        bookList.remove(book);
    }

    public Book getBook(int position) {
        return bookList.get(position);
    }

    public int getSize() {
        return bookList.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(bookList);
    }
}
