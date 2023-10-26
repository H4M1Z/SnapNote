package pkg.home.notepad;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    CoordinatorLayout topBarCoordinatorLayout,deleteItemsCoordinatorLayout;
    MaterialToolbar topMaterialToolBar,deleteItemsMaterialToolBar;
    BottomNavigationView bottomNavigationView,deleteItemsBottomNavigationView;
    NavigationView sideNavigationDrawer;
    FloatingActionButton fabAddNote;
    DrawerLayout drawerLayout;
    NotesFragment notesFragment;
    GridViewFragment gridViewFragment;
    FavouritesFragment favouritesFragment;
    FragmentContainerView fragmentContainerView;
    ListViewModelClassAdapter listViewModelClassAdapter;
    NotesDataTable table=NotesDataTable.createObject(this);
    private OnDeleteClickListener onDeleteClickListener;
    private final String[] permissionList=new String[2];
    private final int DELETE_BUTTON_CLICK=1;
    private final int DELETE_ITEMS_CLICK=2;
    private int serialNumber=0;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topBarCoordinatorLayout=findViewById(R.id.topBarCoordinatorLayout);
        deleteItemsCoordinatorLayout=findViewById(R.id.topBarCoordinatorLayoutForDeleteItems);
        topMaterialToolBar=findViewById(R.id.topMaterialToolBar);
        deleteItemsMaterialToolBar=findViewById(R.id.topMaterialTollBarForDeleteItems);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        deleteItemsBottomNavigationView=findViewById(R.id.bottomNavigationViewForDeletedItems);
        fabAddNote=findViewById(R.id.fabAdd);
        drawerLayout=findViewById(R.id.drawerLayout);
        sideNavigationDrawer=findViewById(R.id.sideNavigationDrawer);
        fragmentContainerView=findViewById(R.id.fragment_container_view);

        //Initialize all the variables
       listViewModelClassAdapter=new ListViewModelClassAdapter(table.getAllNotes());
        serialNumber= table.latestSerialNumber();
        gridViewFragment=new GridViewFragment();
        notesFragment=new NotesFragment();
        favouritesFragment=new FavouritesFragment();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view,gridViewFragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();

        //Add permissions to the permission list
        permissionList[1]=Manifest.permission.READ_EXTERNAL_STORAGE;
        permissionList[0]=Manifest.permission.POST_NOTIFICATIONS;

      ActivityResultLauncher<EditNoteActivity.EditNoteParameters> launchEditNoteActivity=registerForActivityResult(new EditNoteActivity.EditNoteActivityContract(), new ActivityResultCallback<EditNoteActivity.EditNoteOutputParameters>() {
      @Override
      public void onActivityResult(EditNoteActivity.EditNoteOutputParameters result) {
          //Receive the data and sen it to the fragments
          FragmentTransaction fragmentTransaction;
          String listHour ="";
          String note=result.getEtNoteText();
          String imagePath=result.getImagePath();
          if(!note.isEmpty()){
              Calendar c = Calendar.getInstance();
              SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM hh:mm a");
              listHour = dateformat.format(c.getTime());
              serialNumber++;
              if (table.insertNote(new ListViewModelClass(serialNumber,note,listHour,false,imagePath,false))){
                  //display the current fragment
                  if (Objects.equals(topMaterialToolBar.getMenu().getItem(0).getTitle(), "Grid View")){
                      fragmentTransaction=getSupportFragmentManager().beginTransaction();
                      fragmentTransaction.replace(R.id.fragment_container_view,notesFragment);
                      fragmentTransaction.setReorderingAllowed(true);
                      fragmentTransaction.commit();
                  }else {
                       fragmentTransaction=getSupportFragmentManager().beginTransaction();
                       fragmentTransaction.replace(R.id.fragment_container_view,gridViewFragment);
                       fragmentTransaction.setReorderingAllowed(true);
                       fragmentTransaction.commit();
                  }
              }

          }
      }
     });

     ActivityResultLauncher<String[]> permissionsLauncher=registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
            }
        });

         topMaterialToolBar.setNavigationOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 //open the drawer
                 drawerLayout.open();
             }
         });

         topMaterialToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
             @Override
             public boolean onMenuItemClick(MenuItem item) {
                 //on Search Click we will disappear topBarCoordinatorLayout and set the search bar layout Visible.
                 if (item.getItemId()==R.id.tvSearch){
                     //Cast it into Search view and perform the functions
                     SearchView searchView=(SearchView) item.getActionView();
                     assert searchView != null;
                     searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                         @Override
                         public boolean onQueryTextSubmit(String query) {
                             return false;
                         }

                         @Override
                         public boolean onQueryTextChange(String newText) {
                             //Send the text to the fragment
                             onDeleteClickListener.onSearchClick(newText);
                             return false;
                         }
                     });
                     return true;
                 }else if (item.getItemId()==R.id.tvNotesView) {
                     //initially the fragment will be of the list view and when grid view is clicked we will change the text to listView and Change the fragment and vice versa.
                     if (Objects.equals(item.getTitle(), "Grid View")){
                         //replace the list fragment with the grid fragment.
                         item.setTitle(R.string.itemListView);
                         getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view,gridViewFragment).commit();
                     }else {
                         //replace the grid fragment with the list fragment.
                         item.setTitle(R.string.itemGridView);
                         getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view,notesFragment).commit();
                     }
                     return true;
                 } else if (item.getItemId()==R.id.tvDeleteItems) {
                     //set checkbox and bottom navigation view and top coordinator layout to visible of the current fragment displayed
                     if (Objects.equals(topMaterialToolBar.getMenu().getItem(0).getTitle(), "List View")){
                         deleteItemsCoordinatorLayout.setVisibility(View.VISIBLE);
                         deleteItemsBottomNavigationView.setVisibility(View.VISIBLE);
                         fabAddNote.setVisibility(View.INVISIBLE);
                         if(onDeleteClickListener != null){
                             onDeleteClickListener.onDeleteClick(DELETE_ITEMS_CLICK);
                         }
                     }else {
                         fabAddNote.setVisibility(View.INVISIBLE);
                         deleteItemsCoordinatorLayout.setVisibility(View.VISIBLE);
                         deleteItemsBottomNavigationView.setVisibility(View.VISIBLE);
                         if(onDeleteClickListener != null){
                             onDeleteClickListener.onDeleteClick(DELETE_ITEMS_CLICK);
                         }
                     }
                     return true;
                 }else
                 return false;
             }
         });

         deleteItemsMaterialToolBar.setNavigationOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 //Set the Views and delete the items selected
                 fabAddNote.setVisibility(View.VISIBLE);
                 deleteItemsCoordinatorLayout.setVisibility(View.INVISIBLE);
                 deleteItemsBottomNavigationView.setVisibility(View.INVISIBLE);
                 if (onDeleteClickListener!=null){
                     onDeleteClickListener.onDeleteClick(DELETE_ITEMS_CLICK);
                 }
             }
          });

            sideNavigationDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId()==R.id.tvSupportUs){
                        //Show the dialog
                        AlertDialog alertDialog=new AlertDialog.Builder(MainActivity.this)
                                .setTitle(getResources().getString(R.string.SupportUsAlertDialogTitle))
                                .setMessage(getResources().getString(R.string.SupportUsAlertDialogMessage))
                                .setPositiveButton(getResources().getString(R.string.SupportUsAlertDialogPositiveButtonText), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(MainActivity.this, getResources().getString(R.string.thankYou), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.SupportUsAlertDialogNegativeButtonText), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Dialog feedBackDialog=new Dialog(MainActivity.this);
                                        feedBackDialog.setContentView(R.layout.custom_dialog_feedback);
                                        TextView tvOk=feedBackDialog.findViewById(R.id.tvOk);
                                        tvOk.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Toast.makeText(MainActivity.this, "We will consider your feedback", Toast.LENGTH_SHORT).show();
                                                feedBackDialog.dismiss();
                                            }
                                        });
                                        feedBackDialog.show();
                                    }
                                })
                                .create();
                                alertDialog.show();
                                drawerLayout.close();
                                return  true;
                    } else if (item.getItemId()==R.id.tvAbout) {
                        //Show the dialog
                        AlertDialog alertDialog=new AlertDialog.Builder(MainActivity.this)
                                .setTitle(getResources().getString(R.string.AboutUsAlertDialogTitle))
                                .setMessage(getResources().getString(R.string.AboutUsAlertDialogMessage))
                                .setPositiveButton(getResources().getString(R.string.shareNow), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Open Sharing Intent
                                        Intent sendIntent=new Intent();
                                        sendIntent.setAction(Intent.ACTION_SEND);
                                        sendIntent.putExtra(Intent.EXTRA_TITLE, "Developer's FaceBook Profile");
                                        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.facebook.com/hamiz.usmani");
                                        sendIntent.setType("text/plain");
                                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                                        startActivity(shareIntent);
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.SupportUsAlertDialogNegativeButtonText), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                     //Do Nothing
                                    }
                                })
                                .create();
                        alertDialog.show();
                        drawerLayout.close();
                        return  true;

                    } else if (item.getItemId()==R.id.tvExit) {
                        finish();
                        return true;
                    }else if (item.getItemId()==R.id.tvShareThisApp){
                        //Open Sharing Intent.
                        Intent sendIntent=new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TITLE, "Developer's FaceBook Profile");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.facebook.com/hamiz.usmani");
                        sendIntent.setType("text/plain");
                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);
                        drawerLayout.close();
                        return true;
                    } else if (item.getItemId()==R.id.tvAllNotes) {
                        //Open the fragment with all notes
                        if (topMaterialToolBar.getMenu().getItem(0).getTitle().equals("Grid View")){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view,notesFragment).commit();
                        }else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view,gridViewFragment).commit();
                        }
                        topMaterialToolBar.setTitle(getResources().getString(R.string.itemAllNotes));
                        topMaterialToolBar.getMenu().getItem(2).setVisible(true);
                        topMaterialToolBar.getMenu().getItem(0).setVisible(true);
                        topMaterialToolBar.getMenu().getItem(1).setVisible(true);
                        fabAddNote.setVisibility(View.VISIBLE);
                        drawerLayout.close();
                        return true;
                    } else if (item.getItemId()==R.id.tvMyFavourites) {
                        //Open the favourite notes fragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view,favouritesFragment).commit();
                        topMaterialToolBar.setTitle(getResources().getString(R.string.itemMyFavourites));
                        topMaterialToolBar.getMenu().getItem(2).setVisible(false);
                        topMaterialToolBar.getMenu().getItem(0).setVisible(false);
                        topMaterialToolBar.getMenu().getItem(1).setVisible(false);
                        fabAddNote.setVisibility(View.INVISIBLE);
                        drawerLayout.close();
                    }
                    return false;
                }
            });


         bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
             @SuppressLint("ResourceType")
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 if (item.getItemId()==R.id.tvNotes){
                     //Open the Notes Fragment
                     if (topMaterialToolBar.getMenu().getItem(0).getTitle().equals("Grid View")){
                     getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view,notesFragment).commit();
                     }else {
                         getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view,gridViewFragment).commit();
                     }
                     topMaterialToolBar.setTitle(getResources().getString(R.string.topBarTitle));
                     topMaterialToolBar.getMenu().getItem(0).setVisible(true);
                     topMaterialToolBar.getMenu().getItem(1).setVisible(true);
                     topMaterialToolBar.getMenu().getItem(2).setVisible(true);
                     fabAddNote.setVisibility(View.VISIBLE);
                     return true;
                 }else{
                 return false;
                 }
             }
         });

         deleteItemsBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 //delete the selected items  and bind the main activity and current fragment
                 if (item.getItemId()==R.id.tvDelete){
                     if (onDeleteClickListener!=null){
                         onDeleteClickListener.onDeleteClick(DELETE_BUTTON_CLICK);
                     }
                     fabAddNote.setVisibility(View.VISIBLE);
                     deleteItemsCoordinatorLayout.setVisibility(View.INVISIBLE);
                     deleteItemsBottomNavigationView.setVisibility(View.INVISIBLE);

                 }
                 return false;
             }
         });

        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send current time to be showed in the note activity and we will send this in string
                String hour;
                String minute;
                TimePicker getCurrentTime=new TimePicker(MainActivity.this);
                getCurrentTime.setIs24HourView(false);
                hour=Integer.toString(getCurrentTime.getHour());
                minute=Integer.toString(getCurrentTime.getMinute());
                launchEditNoteActivity.launch(new EditNoteActivity.EditNoteParameters(hour,minute));
           }
        });

      //Check For Permissions.
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
          permissionsLauncher.launch(permissionList);
      }

      if (ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED){
          permissionsLauncher.launch(permissionList);
      }

    }
    public void bindListener(OnDeleteClickListener onDeleteClickListener){
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

interface OnDeleteClickListener{
    void onDeleteClick(int number);
    void onSearchClick(String text);
}