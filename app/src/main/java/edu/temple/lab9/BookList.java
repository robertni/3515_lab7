package edu.temple.lab9;

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

    public ArrayList<Book> getBookList() {
        return bookList;
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
