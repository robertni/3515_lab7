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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ControlFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ID = "param1";
    private static final String ARG_DURATION = "param2";

    private int id;
    private int duration;
    private String current;

    ImageButton playButton;
    ImageButton pauseButton;
    ImageButton stopButton;
    SeekBar seekBar;
    TextView nowPlaying;

    public ControlFragment() {
        // Required empty public constructor
    }

    public static ControlFragment newInstance(int id, int duration) {
        ControlFragment fragment = new ControlFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putInt(ARG_DURATION, duration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ARG_ID);
            duration = getArguments().getInt(ARG_DURATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        // title of current song
        nowPlaying = view.findViewById(R.id.current);

        // buttons
        playButton = view.findViewById(R.id.playButton);
        pauseButton = view.findViewById(R.id.pauseButton);
        stopButton = view.findViewById(R.id.stopButton);

        // seekbar
        seekBar = view.findViewById(R.id.seekBar);

        return view;
    }
}