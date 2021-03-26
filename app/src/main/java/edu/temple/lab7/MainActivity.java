package edu.temple.lab7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListFragmentInterface {

    Book book;
    BookList bookList;
    boolean landscape;

    BookDetailsFragment bdFragment;

    private static final String DESC_BOOK = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check if container_2 is present
        landscape = findViewById(R.id.container_2) != null;

        // initialize bookList
        bookList = new BookList();

        int size = 0;
        if ((Arrays.asList(getResources().getStringArray(R.array.book_titles)).size()) == (Arrays.asList(getResources().getStringArray(R.array.book_authors)).size())) {
            size = Arrays.asList(getResources().getStringArray(R.array.book_titles)).size();
        }

        for (int i = 0; i < size; i++) {
            String title = Arrays.asList(getResources().getStringArray(R.array.book_titles)).get(i);
            String author = Arrays.asList(getResources().getStringArray(R.array.book_authors)).get(i);
            Book book = new Book(title, author);
            bookList.addBook(book);
        }

        bdFragment = new BookDetailsFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_1, BookListFragment.newInstance(bookList))
                .addToBackStack(null)
                .commit();

        // check if landscape or tablet
        if (landscape) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_2, bdFragment)
                    .addToBackStack(null)
                    .commit();
        }

        // check if anything was saved
        if (savedInstanceState != null) {
            book = savedInstanceState.getParcelable(DESC_BOOK);

            if (book != null) {
                if (landscape) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_2, bdFragment.newInstance(book))
                            .addToBackStack(null)
                            .commit();
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, bdFragment.newInstance(book))
                            .addToBackStack(null)
                            .commit();
                }
            }
        }
    }

    @Override
    public void itemClicked(int position) {
        this.book = bookList.getBook(position);

        if (landscape) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_2, bdFragment.newInstance(book))
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_1, bdFragment.newInstance(book))
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DESC_BOOK, book);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.book = null;
    }
}