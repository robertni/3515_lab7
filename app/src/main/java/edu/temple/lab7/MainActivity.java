package edu.temple.lab7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListFragmentInterface {

    Book book;
    BookList bookList;
    boolean landscape;

    BookListFragment blFragment;
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

        if (savedInstanceState == null) {
            blFragment = new BookListFragment().newInstance(bookList);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_1, blFragment)
                    .addToBackStack(null)
                    .commit();

            if (landscape) {
                bdFragment = new BookDetailsFragment().newInstance(null);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_2, bdFragment)
                        .addToBackStack(null)
                        .commit();
            }
        } else {
            book = savedInstanceState.getParcelable(DESC_BOOK);

            System.out.println("saved book: " + book.getTitle() + " by " + book.getAuthor());

            if (book != null) {
                if (landscape) {
                    if (blFragment == null) {
                        blFragment = BookListFragment.newInstance(bookList);
                    }

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, blFragment)
                            .addToBackStack(null)
                            .commit();

                    if (bdFragment == null) {
                        bdFragment = BookDetailsFragment.newInstance(book);
                    } else {
                        bdFragment.displayBook(book);
                    }

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_2, bdFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    if (bdFragment == null) {
                        bdFragment = BookDetailsFragment.newInstance(book);
                    } else {
                        bdFragment.displayBook(book);
                    }

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, bdFragment)
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
            if (bdFragment == null) {
                bdFragment = BookDetailsFragment.newInstance(book);
            } else {
                bdFragment.displayBook(book);
            }
        } else {
            BookDetailsFragment bookDetailsFragment = new BookDetailsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_1, bookDetailsFragment.newInstance(book))
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DESC_BOOK, book);
    }
}