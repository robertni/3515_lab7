package edu.temple.lab9;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class ControlFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BOOK = "param1";
    private static final String ARG_STATUS = "param2";
    private static final String ARG_PROGRESS = "params3";

    private Book book;
    private int status;
    private int progress;

    Context context;

    TextView isPlaying;
    TextView nowPlaying;
    ImageButton playButton;
    ImageButton pauseButton;
    ImageButton stopButton;
    SeekBar seekBar;

    ControlFragmentInterface controlFragmentInterface;

    public ControlFragment() {
        // Required empty public constructor
    }

    public static ControlFragment newInstance() {
        ControlFragment fragment = new ControlFragment();
        return fragment;
    }

    public static ControlFragment newInstance(Book book, int status, int progress) {
        ControlFragment fragment = new ControlFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BOOK, book);
        args.putInt(ARG_STATUS, status);
        args.putInt(ARG_PROGRESS, progress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = getArguments().getParcelable(ARG_BOOK);
            status = getArguments().getInt(ARG_STATUS);
            progress = getArguments().getInt(ARG_PROGRESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        controlFragmentInterface = (ControlFragmentInterface) context;

        isPlaying = view.findViewById(R.id.isPlaying);
        nowPlaying = view.findViewById(R.id.nowPlaying);
        playButton = view.findViewById(R.id.playButton);
        pauseButton = view.findViewById(R.id.pauseButton);
        stopButton = view.findViewById(R.id.stopButton);
        seekBar = view.findViewById(R.id.seekBar);


        if (book != null && (status == R.string.playing || status == R.string.paused)) {
            setStatus(status);
            displayBook(book);
            if (progress > 0) {
                setProgress(progress, book.getDuration());
            }
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlFragmentInterface.playAudio();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlFragmentInterface.pauseAudio();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlFragmentInterface.stopAudio();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    controlFragmentInterface.changeAudioProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        return view;
    }

    public void setStatus(int status) {
        this.status = status;
        isPlaying.setText(status);
    }

    public void displayBook(Book book) {
        nowPlaying.setText(book.getTitle());
    }

    public void setProgress(int progress, int duration) {
        seekBar.setMax(duration);
        seekBar.setProgress(progress);
    }

    interface ControlFragmentInterface {
        void playAudio();
        void pauseAudio();
        void stopAudio();
        void changeAudioProgress(int progress);
    }
}