package com.example.lufamily.groceryapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * The Class GroceryListMainMenu.
 */
public class MainActivity extends Activity {

    String FILENAME01 = "pendingGroceryItems.txt";
    String FILENAME02 = "atHandGroceryItems.txt";

    final Context contxt = this;
    private ListView lv1 = null;
    private ListView lv2 = null;

    private Button addButton;
    private Button deleteAllButton;

    private ArrayList<String> pendingGroceryItems;
    private ArrayList<String> atHandGroceryItems;

    /** The long click pending grocery items. */
    private OnItemLongClickListener longClickPendingGroceryItems = new OnItemLongClickListener() {

        public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                       int position, long id) {
            showAlertBoxForPendingItems(v.getContext(),
                    "Please select action.", position);
            return true;
        }
    };

    /** The click pending grocery items. */
    private OnItemClickListener clickPendingGroceryItems = new OnItemClickListener() {

        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long id) {
            atHandGroceryItems.add(pendingGroceryItems.get(position));
            String removedItem = pendingGroceryItems.remove(position);
            refreshPage();
            Toast.makeText(getApplicationContext(),
                    "Item " + removedItem + " is already at hand",
                    Toast.LENGTH_SHORT).show();
        }
    };

    /** The long click at hand grocery items. */
    private OnItemLongClickListener longClickAtHandGroceryItems = new OnItemLongClickListener() {

        public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                       int position, long id) {
            showAlertBoxForAtHandItems(v.getContext(), "Please select action.",
                    position);
            return true;
        }
    };

    /** The click at hand grocery items. */
    private OnItemClickListener clickAtHandGroceryItems = new OnItemClickListener() {

        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long id) {
            Toast.makeText(getApplicationContext(),
                    "You have already this item at hand",
                    Toast.LENGTH_SHORT).show();
        }
    };

    /** The add item listener. */
    private OnClickListener addItemListener = new OnClickListener() {

        public void onClick(View v) {
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(contxt);
            View promptsView = li.inflate(R.layout.addentry, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    contxt);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.addTextDialogUserInput);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    // get user input and set it to result
                                    // edit text
                                    pendingGroceryItems.add(userInput.getText()
                                            .toString());
                                    refreshPage();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    };

    /** The delete all item listener. */
    private OnClickListener deleteAllItemListener = new OnClickListener() {

        public void onClick(View v) {
            atHandGroceryItems.clear();
            refreshPage();
            Toast.makeText(getApplicationContext(),
                    "The list has been cleared", Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * Create the activity when the application is run.
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pendingGroceryItems = new ArrayList<String>();
        atHandGroceryItems = new ArrayList<String>();
        setContentView(R.layout.activity_main);
        loadDataFromFiles();
        this.initializeAll();
        this.refreshPage();
    }

    /**
     * Load data from files.
     */
    @SuppressWarnings("unchecked")
    private void loadDataFromFiles(){
        try {
            FileInputStream input01 = openFileInput(FILENAME01);
            FileInputStream input02 = openFileInput(FILENAME02);
            ObjectInputStream in01 = new ObjectInputStream(input01);
            ObjectInputStream in02 = new ObjectInputStream(input02);
            pendingGroceryItems = (ArrayList<String>) in01.readObject();
            atHandGroceryItems = (ArrayList<String>) in02.readObject();
            in01.close();
            in02.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize all.
     */
    private void initializeAll() {
        lv1 = (ListView) findViewById(R.id.list1);
        lv2 = (ListView) findViewById(R.id.list2);

        /** Set button listeners **/
        addButton = (Button) findViewById(R.id.AddButton);
        deleteAllButton = (Button) findViewById(R.id.deleteAllButton);
        addButton.setOnClickListener(addItemListener);
        deleteAllButton.setOnClickListener(deleteAllItemListener);

        /** List View 1: Pending Grocery Items **/
        lv1.setOnItemLongClickListener(longClickPendingGroceryItems);
        lv1.setOnItemClickListener(clickPendingGroceryItems);

        /** List View 2: At Hand Grocery Items **/
        lv2.setOnItemLongClickListener(longClickAtHandGroceryItems);
        lv2.setOnItemClickListener(clickAtHandGroceryItems);
    }

    /**
     * Refresh page.
     */
    private void refreshPage() {
        lv1.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, pendingGroceryItems));
        lv2.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, atHandGroceryItems));
    }

    /**
     * Show alert box for pending items.
     * @param context the context of the class
     * @param message the message desired to be shown in the alert box
     * @param position the position of the item selected in the list view
     */
    public void showAlertBoxForPendingItems(Context context, String message,
                                            int position) {
        final int pos = position;

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .create();
        alertDialog.setTitle("Grocery Item Option");
        alertDialog.setButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                pendingGroceryItems.remove(pos);
                refreshPage();
            }
        });

        alertDialog.setButton2("Rename", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(contxt);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        contxt);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // get user input and set it to result
                                        // edit text
                                        pendingGroceryItems.remove(pos);
                                        pendingGroceryItems.add(pos, userInput
                                                .getText().toString());
                                        refreshPage();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        alertDialog.setButton3("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    /**
     * Show alert box for at hand items.
     * @param context the context of the class
     * @param message the message desired to be shown in the alert box
     * @param position the position of the item selected in the list view
     */
    public void showAlertBoxForAtHandItems(Context context, String message,
                                           int position) {
        final int pos = position;

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .create();
        alertDialog.setTitle("At Hand Grocery Item Option");
        alertDialog.setButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                atHandGroceryItems.remove(pos);
                refreshPage();
            }
        });

        alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    /**
     * On back pressed (closing the application). Saves the data into the text file
     */
    @Override
    public void onBackPressed() {
        FileOutputStream fos01;
        FileOutputStream fos02;
        try {
            fos01 = openFileOutput(FILENAME01, Context.MODE_PRIVATE);
            fos02 = openFileOutput(FILENAME02, Context.MODE_PRIVATE);
            ObjectOutputStream out01 = new ObjectOutputStream(fos01);
            ObjectOutputStream out02 = new ObjectOutputStream(fos02);
            out01.writeObject(pendingGroceryItems);
            out02.writeObject(atHandGroceryItems);
            out01.close();
            out02.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }
}