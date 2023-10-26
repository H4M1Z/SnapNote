package pkg.home.notepad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class NotesFragment extends Fragment {
    public static final String KEY_NOTE="Note key";
    public static final String KEY_TIME="Time key";
    public static final String KEY_SERIAL_NUMBER="Serial number key";
    public static final String KEY_IS_CHECKED="Is Checked Key";
    public static final String KEY_IMAGE="Image Key";
    RecyclerView rvNotes;
    ListViewModelClassAdapter adapter;
    ListViewModelClass note;
    Favourites favNote;
    NotesDataTable table=NotesDataTable.createObject(getContext());


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
       View view =inflater.inflate(R.layout.fragment_notes_layout, container, false);
        rvNotes=view.findViewById(R.id.rvNotes);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        adapter=new ListViewModelClassAdapter(table.getAllNotes());
        assert mainActivity != null;
        mainActivity.bindListener(new OnDeleteClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDeleteClick(int number) {
                //Set the checkBox visible or invisible
                if (number==2){
                if (!adapter.isVisible()){
                    adapter.setVisibility(true);
                }else {
                    adapter.setVisibility(false);
                }
                }else if (number==1){
                    int notesDeleted=0;
                    for (ListViewModelClass list: table.getAllNotes()) {
                        //Delete the notes that are selected and update the recycler view
                        if (list.isChecked()){
                           if (table.deleteNote(list)){
                               notesDeleted++;
                           }
                            if (!table.getAllFavouriteNotes().isEmpty()){
                                favNote=table.getSelectedNote(list.getSerialNumber());
                                if (table.deleteFavouriteNote(favNote)){

                                }
                            }

                        }

                    }
                    //check the number of notes deleted
                    switch (notesDeleted){
                        //If no notes are deleted then do nothing
                        case 0:
                            break;
                        //If only one note is deleted then show this toast
                        case 1:
                            Toast.makeText(mainActivity, "Note Deleted Successfully", Toast.LENGTH_SHORT).show();
                            break;
                        //If more than one note is deleted then show this toast
                        default:
                            Toast.makeText(mainActivity, "Notes Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                    adapter=new ListViewModelClassAdapter(table.getAllNotes());
                    adapter.setVisibility(false);
                    onResume();
                }
                rvNotes.setAdapter(adapter);
            }
            @Override
            public void onSearchClick(String text) {
                //filter the recycler view
                filter(text);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        adapter = new ListViewModelClassAdapter(table.getAllNotes());
        rvNotes.setAdapter(adapter);
        rvNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotes.setHasFixedSize(true);

           adapter.setItemClickListener(new ListViewModelClassAdapter.IonListItemClickListener() {
               @Override
               public void onItemClick(View view, int position,String text) {
                   note=table.getAllNotes().get(position);
                   //Check if the clicked note is clicked from search result
                   if (note.getText().equals(text)){
                       //open edit note activity
                   if (!adapter.isVisible()){
                       Intent editNoteIntent=new Intent(getContext(), EditSelectedNoteActivity.class);
                       editNoteIntent.putExtra(KEY_NOTE,note.getText());
                       editNoteIntent.putExtra(KEY_TIME,note.getTime());
                       editNoteIntent.putExtra(KEY_SERIAL_NUMBER,note.getSerialNumber());
                       editNoteIntent.putExtra(KEY_IS_CHECKED,note.isChecked());
                       editNoteIntent.putExtra(KEY_IMAGE,note.getImagePath());
                       startActivity(editNoteIntent);
                   }
                   }else {
                       //Open the note clicked from the search result
                       note=table.selectedNote(text);
                       Intent editNoteIntent=new Intent(getContext(), EditSelectedNoteActivity.class);
                       editNoteIntent.putExtra(KEY_NOTE,note.getText());
                       editNoteIntent.putExtra(KEY_TIME,note.getTime());
                       editNoteIntent.putExtra(KEY_SERIAL_NUMBER,note.getSerialNumber());
                       editNoteIntent.putExtra(KEY_IS_CHECKED,note.isChecked());
                       editNoteIntent.putExtra(KEY_IMAGE,note.getImagePath());
                       startActivity(editNoteIntent);
                   }
               }

               @Override
               public void onLongItemClick(View view, int position) {
                   //send the clicked note to Favourite activity
                   boolean isPresent=false;
                   Favourites favNote;
                   note=table.getAllNotes().get(position);
                   for (int i = 0; i < table.latestSerialNumberInFavourites(); i++) {
                       //check if the note is already present than remove if from favourites
                       favNote=table.getAllFavouriteNotes().get(i);
                       if (note.getSerialNumber()==favNote.getSerialNumber()){
                           //Remove note from Favourites
                           table.deleteFavouriteNote(new Favourites(favNote.getSerialNumber()));
                           Toast.makeText(mainActivity, "Note removed from favourites ", Toast.LENGTH_SHORT).show();
                           isPresent=true;
                       }
                       }
                       //If it is not in Favourites add it in Favourites
                       if (!isPresent){
                           String text= note.getText();
                           String time=note.getTime();
                           int serialNumber= note.getSerialNumber();
                           boolean isChecked=note.isChecked();
                           String imagePath=note.getImagePath();
                           if (table.insertNoteInFavourite(new Favourites(serialNumber,text,time,isChecked,imagePath))){
                           Toast.makeText(mainActivity, "Note added to favourites ", Toast.LENGTH_SHORT).show();
                           }
                           note.setFavourite(true);
                           table.updateNote(note);
                     }
               }
           });

    }
    private void filter(String text){
        //Filter each word and check if it matches any note and update the recycler view
        ArrayList<ListViewModelClass> list=new ArrayList<>();
        for (ListViewModelClass view:table.getAllNotes()) {
            if (view.getText().toLowerCase().contains(text.toLowerCase())){
                list.add(view);
            }
        }
        if (list.isEmpty()){
            rvNotes.setAdapter(null);
        }else {
            adapter.filterList(list);
            rvNotes.setAdapter(adapter);
        }
    }

}
