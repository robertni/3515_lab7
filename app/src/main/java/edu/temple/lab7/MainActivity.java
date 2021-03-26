package edu.temple.lab7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListFragmentInterface {

    BookList bookList;
    boolean moreSpace;
    BookDetailsFragment bdFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moreSpace = findViewById(R.id.container_2) != null;

        bookList = new BookList();

        for (int i = 0; i < Arrays.asList(getResources().getStringArray(R.array.book_titles)).size(); i++) {
            String title = Arrays.asList(getResources().getStringArray(R.array.book_titles)).get(i);
            String author = Arrays.asList(getResources().getStringArray(R.array.book_authors)).get(i);
            Book book = new Book(title, author);
            bookList.addBook(book);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_1, BookListFragment.newInstance(bookList))
                .addToBackStack(null)
                .commit();


        if (moreSpace) {
            bdFragment = new BookDetailsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_2, bdFragment)
                    .commit();
        }
    }


    @Override
    public void itemClicked(int position) {
        if (!moreSpace) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_1, BookDetailsFragment.newInstance(bookList.getBook(position)))
                    .addToBackStack(null)
                    .commit();
        } else {
            bdFragment.displayBook(bookList.getBook(position));
        }
    }
}