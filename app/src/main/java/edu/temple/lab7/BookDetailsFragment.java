package edu.temple.lab7;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BookDetailsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BOOK = "param1";

    private Book book;

    TextView title;
    TextView author;

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    public static BookDetailsFragment newInstance(Book book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BOOK, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = getArguments().getParcelable(ARG_BOOK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_details, container, false);

        title = view.findViewById(R.id.titleDisplay);
        author = view.findViewById(R.id.authorDisplay);

        title.setTextSize(26);
        author.setTextSize(20);

        title.setGravity(Gravity.CENTER);
        author.setGravity(Gravity.CENTER);

        if (book != null) {
            displayBook(book);
        }

        return view;
    }

    // public method to display book
    public void displayBook(Book book) {
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
    }
}