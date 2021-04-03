package edu.temple.lab8;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class BookListFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LIST = "param1";

    private BookList bookList;
    private BookAdapter adapter;

    public BookListFragment() {
        // Required empty public constructor
    }

    public static BookListFragment newInstance(BookList bookList) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LIST, bookList);
        fragment.setArguments(args);
        return fragment;
    }

    public void update(BookList books) {
        System.out.println("*** BookListFragment update called ***");
        bookList = books;
        adapter.notifyDataSetChanged();

        Bundle args = new Bundle();
        args.putParcelable(ARG_LIST, bookList);
        this.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookList = getArguments().getParcelable(ARG_LIST);
        } else {
            bookList = new BookList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ListView listView = (ListView) inflater.inflate(R.layout.fragment_book_list, container, false);

        adapter = new BookAdapter(getActivity(), bookList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((BookListFragmentInterface) getActivity()).itemClicked(position);
            }
        });
        return listView;
    }

    interface BookListFragmentInterface {
        void itemClicked(int position);
    }
}