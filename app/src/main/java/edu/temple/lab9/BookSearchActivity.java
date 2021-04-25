package edu.temple.lab9;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookSearchActivity extends DialogFragment {

    Context context;
    BookList books;
    EditText editText;

    BSListenerInterface listener;
    RequestQueue requestQueue;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_book_search, null);
        editText = view.findViewById(R.id.searchEditText);
        requestQueue = Volley.newRequestQueue(context);

        builder.setView(view)
                .setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // save search last search term
                        SharedPreferences preference = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preference.edit();
                        if (editText.getText().toString().isEmpty()) {
                            editor.putBoolean("search", false);
                        } else {
                            editor.putBoolean("search", true);
                            editor.putString("lastSearch", editText.getText().toString());
                        }
                        editor.apply();

                        String url = "https://kamorris.com/lab/cis3515/search.php?term=" + editText.getText().toString();
                        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                books = new BookList();
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
                                listener.onDialogPositiveClick(BookSearchActivity.this, books);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println(error);
                            }
                        });
                        requestQueue.add(jsonArrayRequest);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // cancel clicked
                    }
                });
        return builder.create();
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            listener = (BSListenerInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("BSListenerInterface must be implemented");
        }
    }

    public interface BSListenerInterface {
         void onDialogPositiveClick(DialogFragment dialog, BookList books);
         void onDialogNegativeClick(DialogFragment dialog);
    }
}
