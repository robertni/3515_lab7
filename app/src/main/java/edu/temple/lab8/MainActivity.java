package edu.temple.lab8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListFragmentInterface {

    Book book;
    boolean landscape;

    FragmentManager fragmentManager;

    BookDetailsFragment bdFragment;

    private static final String DESC_BOOK = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check if landscape/tablet
        landscape = findViewById(R.id.container_2) != null;

        fragmentManager = getSupportFragmentManager();

        // check if there is a saved book
        if (savedInstanceState != null) {
            book = savedInstanceState.getParcelable(DESC_BOOK);
        }

        // display BookList in container 1
        if (fragmentManager.findFragmentById(R.id.container_1) instanceof BookDetailsFragment) {
            fragmentManager.popBackStack();
        } else {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container_1, BookListFragment.newInstance(getBookList()))
                    .commit();
        }

        // if book == null is true, it will create a new BookDetailsFragment
        // else it will create a new instance displaying the selected book
        bdFragment = (book == null) ? new BookDetailsFragment() : BookDetailsFragment.newInstance(book);

        if (landscape) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container_2, bdFragment)
                    .commit();
        } else {
            if (book != null) {
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container_1, bdFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    private BookList getBookList() {
        // create a new BookList
        BookList books = new BookList();

        // get book titles and authors
        ArrayList<String> title = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.book_titles)));
        ArrayList<String> author = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.book_authors)));

        // add books into the BookList
        for (int i = 0; i < 10; i++) {
            books.addBook(new Book(title.get(i), author.get(i)));
        }

        // return the BookList back to caller
        return books;
    }

    @Override
    public void itemClicked(int position) {
        book = getBookList().getBook(position);

        if (landscape) {
            bdFragment.displayBook(book);
        } else {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container_1, BookDetailsFragment.newInstance(book))
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
        book = null;
    }
}