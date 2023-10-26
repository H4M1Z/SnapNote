package pkg.home.notepad;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NotesDataTable extends SQLiteOpenHelper {
    public static final String DB_NAME="Notes Table";
    public static final int DB_VERSION=10;
    private static NotesDataTable object;

    public static NotesDataTable createObject(Context context) {
        if (object==null){
            object=new NotesDataTable(context);
        }
        return object;
    }

    private NotesDataTable(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ListViewModelClass.CREATE_TABLE);
        sqLiteDatabase.execSQL(Favourites.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
if (oldVersion!=newVersion){
sqLiteDatabase.execSQL(ListViewModelClass.DROP_TABLE);
sqLiteDatabase.execSQL(ListViewModelClass.CREATE_TABLE);
sqLiteDatabase.execSQL(Favourites.DROP_TABLE);
sqLiteDatabase.execSQL(Favourites.CREATE_TABLE);
}
    }
    public boolean insertNote(ListViewModelClass note){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues newNote=new ContentValues();
        newNote.put(ListViewModelClass.COL_SERIAL_NUMBER,note.getSerialNumber());
        newNote.put(ListViewModelClass.COL_TEXT,note.getText());
        newNote.put(ListViewModelClass.COL_TIME,note.getTime());
        newNote.put(ListViewModelClass.COL_IS_CHECKED,Boolean.toString(note.isChecked()));
        newNote.put(ListViewModelClass.COL_IMAGE,note.getImagePath());
        newNote.put(ListViewModelClass.COL_IS_FAVOURITE,Boolean.toString(note.isFavourite()));
        long rowId;
        try {
            rowId=db.insertOrThrow(ListViewModelClass.TABLE_NAME,null,newNote);
        }catch (Exception exception){
            return false;
        }
        return (rowId>=1);
    }
    public boolean insertNoteInFavourite(Favourites note){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues newNote=new ContentValues();
        newNote.put(Favourites.COL_SERIAL_NUMBER,note.getSerialNumber());
        newNote.put(Favourites.COL_TEXT,note.getText());
        newNote.put(Favourites.COL_TIME,note.getTime());
        newNote.put(Favourites.COL_IS_CHECKED,Boolean.toString(note.isChecked()));
        newNote.put(Favourites.COL_IMAGE,note.getImagePath());
        long rowId;
        try {
            rowId=db.insertOrThrow(Favourites.TABLE_NAME,null,newNote);
        }catch (Exception exception){
            return false;
        }
        return (rowId>=1);
    }
    public boolean updateNote(ListViewModelClass note){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues newNote=new ContentValues();
        newNote.put(ListViewModelClass.COL_SERIAL_NUMBER,note.getSerialNumber());
        newNote.put(ListViewModelClass.COL_TEXT,note.getText());
        newNote.put(ListViewModelClass.COL_TIME,note.getTime());
        newNote.put(ListViewModelClass.COL_IS_CHECKED,Boolean.toString(note.isChecked()));
        newNote.put(ListViewModelClass.COL_IMAGE,note.getImagePath());
        newNote.put(ListViewModelClass.COL_IS_FAVOURITE,Boolean.toString(note.isFavourite()));
        long rowId;
        try {
            rowId=db.update(ListViewModelClass.TABLE_NAME,newNote,ListViewModelClass.COL_SERIAL_NUMBER+" = ?",new String[]{Integer.toString(note.getSerialNumber())});
        }catch (Exception exception){
            return false;
        }
        return (rowId>=1);
    }
    public boolean updateNoteInFavourite(Favourites note){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues newNote=new ContentValues();
        newNote.put(Favourites.COL_SERIAL_NUMBER,note.getSerialNumber());
        newNote.put(Favourites.COL_TEXT,note.getText());
        newNote.put(Favourites.COL_TIME,note.getTime());
        newNote.put(Favourites.COL_IS_CHECKED,Boolean.toString(note.isChecked()));
        newNote.put(Favourites.COL_IMAGE,note.getImagePath());
        long rowId;
        try {
            rowId=db.update(Favourites.TABLE_NAME,newNote,Favourites.COL_SERIAL_NUMBER+" = ?",new String[]{Integer.toString(note.getSerialNumber())});
        }catch (Exception exception){
            return false;
        }
        return (rowId>=1);
    }
    public boolean deleteNote(ListViewModelClass note){
        SQLiteDatabase db=getWritableDatabase();
        long rowId;
        try {
            rowId=db.delete(ListViewModelClass.TABLE_NAME,ListViewModelClass.COL_SERIAL_NUMBER+" = ?",new String[]{Integer.toString(note.getSerialNumber())});
        }catch (Exception exception){
            return false;
        }
        return (rowId>=1);
    }
    public boolean deleteFavouriteNote(Favourites note){
        SQLiteDatabase db=getWritableDatabase();
        long rowId;
        try {
            rowId=db.delete(Favourites.TABLE_NAME,Favourites.COL_SERIAL_NUMBER+" = ?",new String[]{Integer.toString(note.getSerialNumber())});
        }catch (Exception exception){
            return false;
        }
        return (rowId>=1);
    }
    @SuppressLint("Range")
    public List<ListViewModelClass> getAllNotes(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(ListViewModelClass.SELECT_ALL_NOTES,null);
        ArrayList<ListViewModelClass> notesList=new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()){
            do {
                ListViewModelClass note=new ListViewModelClass();
                note.setSerialNumber(cursor.getInt(cursor.getColumnIndex(ListViewModelClass.COL_SERIAL_NUMBER)));
                note.setText(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_TEXT)));
                note.setTime(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_TIME)));
                note.setChecked(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_IS_CHECKED))));
                note.setImagePath(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_IMAGE)));
                note.setFavourite(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_IS_FAVOURITE))));
                notesList.add(note);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return notesList;
    }
    @SuppressLint("Range")
    public List<Favourites> getAllFavouriteNotes(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(Favourites.SELECT_ALL_FAVOURITES_NOTES,null);
        ArrayList<Favourites> notesList=new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()){
            do {
                Favourites note=new Favourites();
                note.setSerialNumber(cursor.getInt(cursor.getColumnIndex(Favourites.COL_SERIAL_NUMBER)));
                note.setText(cursor.getString(cursor.getColumnIndex(Favourites.COL_TEXT)));
                note.setTime(cursor.getString(cursor.getColumnIndex(Favourites.COL_TIME)));
                note.setChecked(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(Favourites.COL_IS_CHECKED))));
                note.setImagePath(cursor.getString(cursor.getColumnIndex(Favourites.COL_IMAGE)));
                notesList.add(note);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return notesList;
    }
    @SuppressLint("Range")
    public ListViewModelClass selectedNote(String noteText){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor= db.rawQuery(ListViewModelClass.SELECT_A_NOTE,new String[]{noteText});
        ListViewModelClass selectedNote=new ListViewModelClass();
        if (cursor.moveToFirst()){
                selectedNote.setSerialNumber(cursor.getInt(cursor.getColumnIndex(ListViewModelClass.COL_SERIAL_NUMBER)));
                selectedNote.setText(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_TEXT)));
                selectedNote.setTime(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_TIME)));
                selectedNote.setChecked(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_IS_CHECKED))));
                selectedNote.setImagePath(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_IMAGE)));
                selectedNote.setFavourite(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_IS_FAVOURITE))));
            cursor.close();
        }
        return selectedNote;
    }
    @SuppressLint("Range")
    public Favourites getSelectedNote(int serialNumber){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor= db.rawQuery(Favourites.SELECT__A_NOTE_FORM_FAVOURITE,new String[]{Integer.toString(serialNumber)});
        Favourites favourites=new Favourites();
        if (cursor.moveToFirst()){
            favourites.setSerialNumber(cursor.getInt(cursor.getColumnIndex(Favourites.COL_SERIAL_NUMBER)));
            favourites.setText(cursor.getString(cursor.getColumnIndex(Favourites.COL_TEXT)));
            favourites.setTime(cursor.getString(cursor.getColumnIndex(Favourites.COL_TIME)));
            favourites.setChecked(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(Favourites.COL_IS_CHECKED))));
            favourites.setImagePath(cursor.getString(cursor.getColumnIndex(Favourites.COL_IMAGE)));
        }
        cursor.close();
        return favourites;
    }
    @SuppressLint("Range")
    public int latestSerialNumber(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor= db.rawQuery(ListViewModelClass.SELECT_ALL_NOTES,null);
        ListViewModelClass note=new ListViewModelClass();
        if (cursor.moveToFirst()){
           do {
               note.setSerialNumber(cursor.getInt(cursor.getColumnIndex(ListViewModelClass.COL_SERIAL_NUMBER)));
               note.setText(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_TEXT)));
               note.setTime(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_TIME)));
               note.setChecked(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_IS_CHECKED))));
               note.setImagePath(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_IMAGE)));
               note.setFavourite(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(ListViewModelClass.COL_IS_FAVOURITE))));
           }while (cursor.moveToNext());
            cursor.close();
        }
        return note.getSerialNumber();
    }
    @SuppressLint("Range")
    public int latestSerialNumberOfFavourites(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor= db.rawQuery(Favourites.SELECT_ALL_FAVOURITES_NOTES,null);
        Favourites favourites=new Favourites();
        if (cursor.moveToFirst()){
            do {
                favourites.setSerialNumber(cursor.getInt(cursor.getColumnIndex(Favourites.COL_SERIAL_NUMBER)));
                favourites.setText(cursor.getString(cursor.getColumnIndex(Favourites.COL_TEXT)));
                favourites.setTime(cursor.getString(cursor.getColumnIndex(Favourites.COL_TIME)));
                favourites.setChecked(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(Favourites.COL_IS_CHECKED))));
                favourites.setImagePath(cursor.getString(cursor.getColumnIndex(Favourites.COL_IMAGE)));
            }while (cursor.moveToNext());
            cursor.close();
        }
        return favourites.getSerialNumber();
    }
    public int latestSerialNumberInNotes(){
int count=0;
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor= db.rawQuery(ListViewModelClass.SELECT_ALL_NOTES,null);
        if (cursor.moveToFirst()){
            do {
               count++;
            }while (cursor.moveToNext());
            cursor.close();
        }
        return count;
    }
    public int latestSerialNumberInFavourites(){
        int count=0;
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor= db.rawQuery(Favourites.SELECT_ALL_FAVOURITES_NOTES,null);
        if (cursor.moveToFirst()){
            do {
                count++;
            }while (cursor.moveToNext());
            cursor.close();
        }
        return count;
    }
}
