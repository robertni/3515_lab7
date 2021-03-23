package edu.temple.lab7;

import java.util.ArrayList;

public class BookList {

    private ArrayList<Book> bookList;

    public BookList() {
        bookList = new ArrayList<Book>();
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
}
