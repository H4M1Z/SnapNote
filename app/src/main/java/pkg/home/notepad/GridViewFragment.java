package pkg.home.notepad;

import static pkg.home.notepad.NotesFragment.KEY_IMAGE;
import static pkg.home.notepad.NotesFragment.KEY_IS_CHECKED;
import static pkg.home.notepad.NotesFragment.KEY_NOTE;
import static pkg.home.notepad.NotesFragment.KEY_SERIAL_NUMBER;
import static pkg.home.notepad.NotesFragment.KEY_TIME;

import android.animation.IntArrayEvaluator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class GridViewFragment extends Fragment {
    RecyclerView rvGridLayout;
    GridViewAdapter GridViewAdapter;
    NotesDataTable table=NotesDataTable.createObject(getContext());
    ListViewModelClass note;
    Favourites favNote;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_grid_view, container, false);
        rvGridLayout=view.findViewById(R.id.recyclerViewGridLayout);
        MainActivity mainActivity=(MainActivity) getActivity();
        assert mainActivity != null;
        mainActivity.bindListener(new OnDeleteClickListener() {
               @SuppressLint("NotifyDataSetChanged")
               @Override
               public void onDeleteClick(int number) {
                   //set the check boxes visible or invisible and update the recycler view
                   if (number==2){
                   if (!GridViewAdapter.isVisible()){
                       GridViewAdapter.setVisible(true);
                   }else {
                       GridViewAdapter.setVisible(false);
                   }
                   }else if (number==1){
                       //Delete the notes that are selected and update the recycler view
                       int notesDeleted=0;
                       for (ListViewModelClass list: table.getAllNotes()) {
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
                       GridViewAdapter.notifyDataSetChanged();
                       GridViewAdapter=new GridViewAdapter(table.getAllNotes());
                       GridViewAdapter.setVisible(false);
                       onResume();
                   }
                   rvGridLayout.setAdapter(GridViewAdapter);

               }

            @Override
            public void onSearchClick(String text) {
                   //filter the recycler view
                 filter(text);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity=(MainActivity) getActivity();
        GridViewAdapter=new GridViewAdapter(table.getAllNotes());
        rvGridLayout.setAdapter(GridViewAdapter);
        rvGridLayout.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        rvGridLayout.setHasFixedSize(true);
        GridViewAdapter.setOnGridViewClickListener(new GridViewAdapter.onGridViewClickListener() {
            @Override
            public void onViewClick(View view, int position,String text) {
                note=table.getAllNotes().get(position);
                //Check if the clicked note is clicked from search result
                if (note.getText().equals(text)){
                    //Open the ote clicked form normal view
                if (!GridViewAdapter.isVisible()){
                    //open edit note activity
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
            public void onViewLongClick(View view, int position) {
                  //send the clicked note to the favourite activity
                  boolean isPresent=false;
                  Favourites favNote;
                  note=table.getAllNotes().get(position);
                  //check if the note is already present than remove if from favourites
                  for (int i = 0; i < table.latestSerialNumberInFavourites(); i++) {
                      favNote=table.getAllFavouriteNotes().get(i);
                      if (note.getSerialNumber()==favNote.getSerialNumber()){
                          //Remove note from Favourites
                          table.deleteFavouriteNote(new Favourites(favNote.getSerialNumber()));
                          Toast.makeText(mainActivity, "Note removed from favourites", Toast.LENGTH_SHORT).show();
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
                          Toast.makeText(mainActivity, "Note added to favourites", Toast.LENGTH_SHORT).show();
                          }
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
            rvGridLayout.setAdapter(null);
        }else {
            GridViewAdapter.filterList(list);
            rvGridLayout.setAdapter(GridViewAdapter);
        }
}
}