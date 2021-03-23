package edu.temple.lab7;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    BookList bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookList = new BookList();

        for (int i = 0; i < Arrays.asList(getResources().getStringArray(R.array.book_titles)).size(); i++) {
            String title = Arrays.asList(getResources().getStringArray(R.array.book_titles)).get(i);
            String author = Arrays.asList(getResources().getStringArray(R.array.book_authors)).get(i);
            Book book = new Book(title, author);
            bookList.addBook(book);
        }
    }
}