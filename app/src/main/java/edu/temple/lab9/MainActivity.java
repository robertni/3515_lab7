package edu.temple.lab9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListFragmentInterface,
        BookSearchActivity.BSListenerInterface, ControlFragment.ControlFragmentInterface {

    BookList books;
    Book book;
    boolean landscape;

    FragmentManager fragmentManager;
    BookDetailsFragment bdFragment;

    RequestQueue requestQueue;

    int connectionStatus;
    AudiobookService.MediaControlBinder controlBinder;
    boolean connected = false;
    int progress;

    private final String TAG_BOOKDETAILS = "book";
    private final String TAG_BOOKLIST = "booklist";
    private final String TAG_CFRAG = "control fragment";

    private final String SAVED_BOOK = "saved book";
    private final String SAVED_BOOKLIST = "saved booklist";

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            connected = true;
            controlBinder = (AudiobookService.MediaControlBinder) service;
            switch (connectionStatus) {
                case 0: // playing
                    controlBinder.play(book.getId());
                    controlBinder.setProgressHandler(progressHandler);
                    break;
                case 1: // paused
                    controlBinder.pause();
                    break;
                case 2: // stopped
                    controlBinder.stop();
                    break;
                case 3: // changed seek bar progress
                    controlBinder.seekTo(progress);
                    break;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
        }
    };

    Handler progressHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            AudiobookService.BookProgress bookProgress = (AudiobookService.BookProgress) msg.obj;
            if (bookProgress != null) {
                progress = bookProgress.getProgress();
                if (fragmentManager.findFragmentByTag(TAG_CFRAG) instanceof ControlFragment) {
                    ((ControlFragment) fragmentManager.findFragmentByTag(TAG_CFRAG)).setProgress(progress, book.getDuration());
                }
            }
            return false;
        }
    });

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, AudiobookService.class);
        startService(serviceIntent);
    }

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
            book = savedInstanceState.getParcelable(SAVED_BOOK);
            books = savedInstanceState.getParcelable(SAVED_BOOKLIST);
        } else {
            books = new BookList();
            getBookList();
        }

        // display BookList in container 1
        if (fragmentManager.findFragmentById(R.id.container_1) instanceof BookDetailsFragment) {
            fragmentManager.popBackStack();
        } else {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container_1, BookListFragment.newInstance(books), TAG_BOOKLIST)
                    .replace(R.id.controls, ControlFragment.newInstance(), TAG_CFRAG)
                    .commit();
        }

        // if book == null is true, it will create a new BookDetailsFragment
        // else it will create a new instance displaying the selected book
        bdFragment = (book == null) ? new BookDetailsFragment() : BookDetailsFragment.newInstance(book);

        if (landscape) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container_2, bdFragment, TAG_BOOKDETAILS)
                    .commit();
        } else {
            if (book != null) {
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container_1, bdFragment, TAG_BOOKDETAILS)
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void getBookList() {
        BookList books = new BookList();
        String url = "https://kamorris.com/lab/cis3515/search.php?term=";
        requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        books.addBook(new Book(
                                Integer.parseInt(jsonObject.getString("id")),
                                jsonObject.getString("title"),
                                jsonObject.getString("author"),
                                jsonObject.getString("cover_url"),
                                Integer.parseInt(jsonObject.getString("duration"))
                        ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setBookList(books);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void setBookList(BookList books) {
        this.books = books;
        showNewBookList();
    }

    private void showNewBookList() {
        if ((fragmentManager.findFragmentByTag(TAG_BOOKDETAILS)) instanceof BookDetailsFragment) {
            fragmentManager.popBackStack();
        }
        if (fragmentManager.findFragmentByTag(TAG_BOOKLIST) != null) {
            ((BookListFragment) fragmentManager.findFragmentByTag(TAG_BOOKLIST)).update(books);
        }
    }

    // BookListFragmentInterface
    @Override
    public void itemClicked(int position) {
        this.book = books.getBook(position);
        progress = 0;
        if (landscape) {
            bdFragment.displayBook(book);
        } else {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container_1, BookDetailsFragment.newInstance(book), TAG_BOOKDETAILS)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // BSListenerInterface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, BookList bookList) {
        books.clear();
        books = bookList;
        showNewBookList();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // cancel clicked - do nothing
    }

    // ControlFragmentInterface
    @Override
    public void playAudio() {
        connectionStatus = 0;
        if (connected) {
            unbindService(connection);
        }

        if (book == null) {
            Toast.makeText(MainActivity.this, "You have not selected a book yet", Toast.LENGTH_SHORT).show();
        } else {
            ((ControlFragment) fragmentManager.findFragmentByTag(TAG_CFRAG)).changeStatus(R.string.playing);
            ((ControlFragment) fragmentManager.findFragmentByTag(TAG_CFRAG)).displayBook(book);
            Intent serviceIntent = new Intent(this, AudiobookService.class);
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void pauseAudio() {
        ControlFragment cFragment = (ControlFragment) fragmentManager.findFragmentByTag(TAG_CFRAG);
        if (cFragment.getStatus() == R.string.paused) {
            cFragment.changeStatus(R.string.playing);
        } else if (cFragment.getStatus() == R.string.playing) {
            cFragment.changeStatus(R.string.paused);
        }

        connectionStatus = 1;
        if (connected) {
            unbindService(connection);
        }
        Intent serviceIntent = new Intent(this, AudiobookService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void stopAudio() {
        ((ControlFragment) fragmentManager.findFragmentByTag(TAG_CFRAG)).changeStatus(R.string.stopped);
        connectionStatus = 2;
        if (connected) {
            unbindService(connection);
        }
        Intent serviceIntent = new Intent(this, AudiobookService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void changeAudioProgress(int progress) {
        connectionStatus = 3;
        if (connected) {
            unbindService(connection);
        }
        this.progress = progress;
        Intent serviceIntent = new Intent(this, AudiobookService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        book = null;
        ((ControlFragment) fragmentManager.findFragmentByTag(TAG_CFRAG)).clearStatus();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_BOOK, book);
        outState.putParcelable(SAVED_BOOKLIST, books);
    }
}