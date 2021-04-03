package edu.temple.lab8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListFragmentInterface, BookSearchActivity.BSListenerInterface {

    BookList books;
    Book book;

    boolean landscape;

    FragmentManager fragmentManager;

    BookListFragment blFragment;
    BookDetailsFragment bdFragment;

    RequestQueue requestQueue;

    private static final String SAVED_BOOK = "Book";
    private static final String SAVED_BOOKLIST = "BookList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);

        // check if landscape/tablet
        landscape = findViewById(R.id.container_2) != null;

        fragmentManager = getSupportFragmentManager();

        // check if there is a saved book and booklist
        if (savedInstanceState != null) {
            if (savedInstanceState.getString(SAVED_BOOK) != null) {
                String json = savedInstanceState.getString(SAVED_BOOK);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    book = new Book(
                            jsonObject.getInt("id"),
                            jsonObject.getString("title"),
                            jsonObject.getString("author"),
                            jsonObject.getString("coverURL")
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (savedInstanceState.getString(SAVED_BOOKLIST) != null) {
                String json = savedInstanceState.getString(SAVED_BOOKLIST);
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    books = JsonToBookList(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        blFragment = (books == null) ? BookListFragment.newInstance(getBookList()) : BookListFragment.newInstance(books);

        // display BookList in container 1
        if (fragmentManager.findFragmentById(R.id.container_1) instanceof BookDetailsFragment) {
            fragmentManager.popBackStack();
        } else {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container_1, blFragment)
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new BookSearchActivity();
                dialog.show(fragmentManager, "BookSearchActivity");
            }
        });
    }

    // BookListFragmentInterface
    @Override
    public void itemClicked(int position) {
        this.book = books.getBook(position);
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

    // BSListenerInterface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, BookList bookList) {
        books = bookList;

        if (fragmentManager.findFragmentById(R.id.container_1) instanceof BookDetailsFragment) {
            fragmentManager.popBackStack();
        }

        if (blFragment != null) {
            blFragment.update(books);
        } else {
            blFragment = BookListFragment.newInstance(books);
        }

        fragmentManager
                .beginTransaction()
                .replace(R.id.container_1, blFragment)
                .commit();

        // when a new search occurs, set book to null
        book = null;
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // cancel clicked - do nothing
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        book = null;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        JSONObject book = bookToJson(this.book);
        JSONArray books = bookListToJson(this.books);
        outState.putString(SAVED_BOOK, book.toString());
        outState.putString(SAVED_BOOKLIST, books.toString());
    }

    private BookList getBookList() {
        BookList books = new BookList();
        String url = "https://kamorris.com/lab/cis3515/search.php?term=";
        requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        int id = Integer.parseInt(jsonObject.getString("id"));
                        String title = jsonObject.getString("title");
                        String author = jsonObject.getString("author");
                        String cover_url = jsonObject.getString("cover_url");

                        books.addBook(new Book(id, title, author, cover_url));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setBookList(books);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        requestQueue.add(jsonArrayRequest);

        // return the BookList back to caller
        return books;
    }

    private void setBookList(BookList books) {
        this.books = books;
        if (fragmentManager.findFragmentById(R.id.container_1) instanceof BookDetailsFragment) {
            fragmentManager.popBackStack();
        } else {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container_1, BookListFragment.newInstance(books))
                    .commit();
        }
    }

    private JSONObject bookToJson(Book book) {
        JSONObject jsonObject = new JSONObject();
        if (book != null) {
            try {
                jsonObject.put("id", book.getId());
                jsonObject.put("title", book.getTitle());
                jsonObject.put("author", book.getAuthor());
                jsonObject.put("coverURL", book.getCoverURL());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private JSONArray bookListToJson(BookList books) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < books.getSize(); i++) {
            jsonArray.put(books.getBook(i).getJSONObject());
        }
        return jsonArray;
    }

    private BookList JsonToBookList(JSONArray books) {
        BookList bookList = new BookList();
        for (int i = 0; i < books.length(); i++) {
            JSONObject jsonObject;
            try {
                jsonObject = books.getJSONObject(i);
                Book book = new Book(
                        jsonObject.getInt("id"),
                        jsonObject.getString("title"),
                        jsonObject.getString("author"),
                        jsonObject.getString("coverURL")
                );
                bookList.addBook(book);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return bookList;
    }
}