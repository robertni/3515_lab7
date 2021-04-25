package edu.temple.lab9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListFragmentInterface,
        BookSearchActivity.BSListenerInterface, ControlFragment.ControlFragmentInterface {

    BookList books;
    Book book;
    Book playingBook;
    boolean landscape;

    FragmentManager fragmentManager;
    BookDetailsFragment bdFragment;
    ControlFragment cFragment;

    RequestQueue requestQueue;

    AudiobookService.MediaControlBinder controlBinder;
    boolean connected = false;
    Intent serviceIntent;
    int progress = 0;
    int status = 0;

    SharedPreferences preference;
    String filename;
    File file;

    private final String TAG_BOOKDETAILS = "tag_book";
    private final String TAG_BOOKLIST = "tag_booklist";
    private final String TAG_CFRAG = "tag_control_fragment";

    private final String SAVED_BOOK = "saved_book";
    private final String SAVED_BOOK2 = "saved_book2";
    private final String SAVED_BOOKLIST = "saved_booklist";
    private final String SAVED_PROGRESS = "saved_progress";
    private final String SAVED_STATUS = "saved_status";

    Handler progressHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.obj != null && playingBook != null) {
                progress = ((AudiobookService.BookProgress) msg.obj).getProgress();
                cFragment.setProgress(progress, playingBook.getDuration());
            }
            return false;
        }
    });

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            controlBinder = (AudiobookService.MediaControlBinder) service;
            controlBinder.setProgressHandler(progressHandler);
            connected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preference = getPreferences(Context.MODE_PRIVATE);

        serviceIntent = new Intent(this, AudiobookService.class);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);

        Button button = findViewById(R.id.button);

        // check if landscape/tablet
        landscape = findViewById(R.id.container_2) != null;

        fragmentManager = getSupportFragmentManager();

        // check if there is a saved book and booklist
        if (savedInstanceState != null) {
            book = savedInstanceState.getParcelable(SAVED_BOOK);
            playingBook = savedInstanceState.getParcelable(SAVED_BOOK2);
            books = savedInstanceState.getParcelable(SAVED_BOOKLIST);
            progress = savedInstanceState.getInt(SAVED_PROGRESS);
            status = savedInstanceState.getInt(SAVED_STATUS);
            cFragment = ControlFragment.newInstance(playingBook, status, progress);
        } else {
            books = new BookList();
            cFragment = ControlFragment.newInstance();
            getBookList();
        }

        // display BookList in container 1
        if (fragmentManager.findFragmentById(R.id.container_1) instanceof BookDetailsFragment) {
            fragmentManager.popBackStack();
        } else {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container_1, BookListFragment.newInstance(books), TAG_BOOKLIST)
                    .replace(R.id.controls, cFragment, TAG_CFRAG)
                    .commit();
        }

        // if book == null is true, it will create a new BookDetailsFragment
        // else it will create a new instance displaying the selected book
        bdFragment = (book == null) ? new BookDetailsFragment() : BookDetailsFragment.newInstance(book);

        if (landscape) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container_2, bdFragment, TAG_BOOKDETAILS)
                    .replace(R.id.controls, cFragment, TAG_CFRAG)
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
        // playingBook and book are null
        if (playingBook == null && (book == null || book.getId() == 0)) {
            Toast.makeText(MainActivity.this, "You have not selected a book yet", Toast.LENGTH_SHORT).show();
        } else {
            if (connected) {
                if (book != null) {
                    System.out.println("book != null");
                    filename = book.getTitle();
                    file = new File(getFilesDir(), filename);
                    if (book == playingBook) {
                        System.out.println("book == playingBook");
                        switch (status) {
                            case R.string.paused:
                                controlBinder.pause();
                                break;
                            case R.string.stopped:
                                controlBinder.play(file);
                                break;
                        }
                    } else { // book not playingBook
                        System.out.println("book != playingBook");
                        if (file.exists()) {
                            System.out.println("File exists. Playing from existing file");
                            controlBinder.play(file);
                        } else {
                            System.out.println("File does not exist. Stream book");
                            controlBinder.play(book.getId());
                            new Thread(() -> {
                                try {
                                    URL url = new URL("https://kamorris.com/lab/audlib/download.php?id=" + book.getId());
                                    URLConnection conn = url.openConnection();
                                    conn.connect();

                                    System.out.println("Downloading from " + url.toString());

                                    BufferedInputStream input = new BufferedInputStream(url.openStream());
                                    FileOutputStream output = new FileOutputStream(file);

                                    byte[] data = new byte[1024];
                                    int count;
                                    while ((count = input.read(data)) > 0) {
                                        output.write(data, 0, count);
                                    }

                                    output.flush();
                                    output.close();
                                    input.close();

                                    System.out.println("Download finished. Closing IOStream");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    playingBook = book;
                } else { // book is null; use playingBook
                    filename = playingBook.getTitle();
                    file = new File(getFilesDir(), filename);

                    switch(status) {
                        case R.string.paused:
                            controlBinder.pause();
                            break;
                        case R.string.stopped:
                            controlBinder.play(file);
                            break;
                    }
                }
                status = R.string.playing;
                ControlFragment fragment = (ControlFragment) fragmentManager.findFragmentByTag(TAG_CFRAG);
                fragment.setStatus(status);
                fragment.displayBook(playingBook);
            }
        }
        startService(serviceIntent);
    }




    @Override
    public void pauseAudio() {
        if (connected) {
            controlBinder.pause();

            ControlFragment fragment = (ControlFragment) fragmentManager.findFragmentByTag(TAG_CFRAG);
            if (status == R.string.paused) {
                status = R.string.playing;
                fragment.setStatus(status);
            } else if (status == R.string.playing) {
                status = R.string.paused;
                fragment.setStatus(status);
            }
        }
    }

    @Override
    public void stopAudio() {
        if (connected) {
            controlBinder.stop();
            progress = 0;

            if (status == R.string.playing || status == R.string.paused) {
                status = R.string.stopped;
                ((ControlFragment) fragmentManager.findFragmentByTag(TAG_CFRAG)).setStatus(status);
            }
        }
        stopService(serviceIntent);
    }

    @Override
    public void changeAudioProgress(int progress) {
        if (connected) {
            this.progress = progress;
            controlBinder.seekTo(progress);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        book = null;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_BOOK, book);
        outState.putParcelable(SAVED_BOOK2, playingBook);
        outState.putParcelable(SAVED_BOOKLIST, books);
        outState.putInt(SAVED_PROGRESS, progress);
        outState.putInt(SAVED_STATUS, status);
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }
}