package pkg.home.notepad;

import static pkg.home.notepad.NotesFragment.KEY_IMAGE;
import static pkg.home.notepad.NotesFragment.KEY_NOTE;
import static pkg.home.notepad.NotesFragment.KEY_SERIAL_NUMBER;
import static pkg.home.notepad.NotesFragment.KEY_TIME;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class FavouritesFragment extends Fragment {
    RecyclerView rvFavourites;
    FavouritesAdapter favouritesAdapter;
    NotesDataTable table=NotesDataTable.createObject(getContext());
    Favourites note;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_favourites, container, false);
        rvFavourites=view.findViewById(R.id.rvFavourites);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        favouritesAdapter=new FavouritesAdapter(table.getAllFavouriteNotes());
        rvFavourites.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFavourites.setAdapter(favouritesAdapter);
        rvFavourites.setHasFixedSize(true);
        favouritesAdapter.setOnFavouriteClickListener(new FavouritesAdapter.IOnFavouriteClickListener() {
            @Override
            public void onFavouriteItemClick(View view, int position) {
                //open the edit note activity
                note=table.getAllFavouriteNotes().get(position);
                Intent editNoteIntent=new Intent(getContext(), EditSelectedNoteActivity.class);
                editNoteIntent.putExtra(KEY_NOTE,note.getText());
                editNoteIntent.putExtra(KEY_TIME,note.getTime());
                editNoteIntent.putExtra(KEY_SERIAL_NUMBER,note.getSerialNumber());
                editNoteIntent.putExtra(KEY_IMAGE,note.getImagePath());
                startActivity(editNoteIntent);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFavouriteItemLongClick(View view, int position, ImageView ivFavourites) {
                //Remove the note from favourites
                note=table.getAllFavouriteNotes().get(position);
                if (table.deleteFavouriteNote(new Favourites(note.getSerialNumber()))){
                Toast.makeText(getContext(), "Note Removed from favourites ", Toast.LENGTH_SHORT).show();
                favouritesAdapter.removeItem(position);
                favouritesAdapter.notifyDataSetChanged();
                }

            }
        });
    }
}