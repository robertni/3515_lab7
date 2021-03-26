package edu.temple.lab7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListFragmentInterface {

    Book book;
    BookList bookList;
    boolean moreSpace;

    BookDetailsFragment bdFragment;

    private static final String DESC_BOOK = "";

    private static int position;

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

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_1, BookListFragment.newInstance(bookList))
                    .addToBackStack(null)
                    .commit();

            if (moreSpace) {
                bdFragment = new BookDetailsFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_2, bdFragment)
                        .addToBackStack(null)
                        .commit();
            }
        } else {
            book = savedInstanceState.getParcelable(DESC_BOOK);

            if (book != null) {
                if (!moreSpace) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, BookDetailsFragment.newInstance(book))
                            .addToBackStack(null)
                            .commit();
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, BookListFragment.newInstance(bookList))
                            .addToBackStack(null)
                            .commit();

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_2, BookDetailsFragment.newInstance(book))
                            .addToBackStack(null)
                            .commit();
                }
            }

        }
    }


    @Override
    public void itemClicked(int position) {
        this.position = position;
        if (!moreSpace) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_1, BookDetailsFragment.newInstance(bookList.getBook(position)))
                    .addToBackStack(null)
                    .commit();
        } else {
            if (bdFragment != null) {
                bdFragment.displayBook(bookList.getBook(position));
            } else {
                bdFragment = new BookDetailsFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_2, bdFragment.newInstance(bookList.getBook(position)))
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DESC_BOOK, bookList.getBook(position));
    }
}