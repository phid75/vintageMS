package net.classicGarage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class MSActivity extends Activity implements OnClickListener, OnLongClickListener,
		OnSharedPreferenceChangeListener {	// to track preference changes{	

	private static final String TAG="MSActivity"; 
	
	private int H_GRID_SIZE = 9;		// will be overridden by preference
	private int V_GRID_SIZE = 8;		// will be overridden by preference
	private int NB_BOMBS = 10;			// will be overridden by preference
	
	private int nbBombsGuessed = 0;		// number of bombs flagged by user
	
	private boolean gameOn = false;	// true if a game is started
	
	private AlertDialog alertYouWin;
	private AlertDialog alertYouLoose;
    
	private TextView statusText;  		// status text
	private Button gameInfo;			// button to display game rules	
	
	private MineField mineField;
	
	private boolean mineFieldDimensionHasChanged = false;		// true if user has changed preferences that impact on mine field
	
    /** Called when the activity is first created. 
     *  or when it comes from stopped state 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms);  // display screen

        // Get preferences from XML file - register for pref changes
    	SharedPreferences prefs ;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        mineFieldDimensionHasChanged = false;
		H_GRID_SIZE = Integer.parseInt(prefs.getString("H_SIZE", Integer.toString(H_GRID_SIZE)));
        V_GRID_SIZE = Integer.parseInt(prefs.getString("V_SIZE", Integer.toString(V_GRID_SIZE)));
        NB_BOMBS = Integer.parseInt(prefs.getString("NB_BOMBS", Integer.toString(NB_BOMBS)));       
        
        //creates mine field
        TableLayout tl = (TableLayout) findViewById(R.id.mineFieldTableLayout);          
        mineField = new MineField(H_GRID_SIZE, V_GRID_SIZE);	// creates the new minefield
        createMineField(tl);		// creates table layout with mine cells
		mineField.placeMines(NB_BOMBS);
		gameOn = true;				// game is started		
		
		statusText = (TextView) findViewById(R.id.numberOfMines);
		updateStatusText();	// display status message
		
	    gameInfo = (Button) findViewById(R.id.gameInfo);
	    gameInfo.setOnClickListener(this); 	
	     
		showHint();		// game instructions
        
	    // create alert dialog that will be used later when user wins or loose
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
	    builder.setMessage("You win !\nPlay again ?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   newGame();
	            	   dialog.cancel();
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	               }
	           });	   
	    alertYouWin = builder.create();		// popup dialog when user looses
	    
	    builder.setMessage("You loose !\nPlay again ?");
	    alertYouLoose = builder.create();	// popup dialog when user wins 
    }
/*
 * inhibates restart of activity due to change of screen orientation
 * see also in manifest file : needs             android:configChanges="orientation|keyboardHidden"	
 * WE WILL NOT USE IT. INSTEAD WE PUT android:screenOrientation="portrait" in manifest to force orientation
 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
 */
/*    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);    // Checks the orientation of the screen   

    	if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
    		//Log.d(TAG, "orientation changed to landscape");    
    	} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
    		//Log.d(TAG, "orientation changed to portrait");   
    	} 
    	//TODO need to handle vertical scrollview
    }
  */  

	/**
	 * creates table layout and mine field. Populates minefield
	 */
	private void createMineField(TableLayout layout) {		
		MineCell c;
		TableRow row;
		ImageButton b;		

	    TableRow.LayoutParams tableRowParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	       
		// create layout table rows 
		for (int i=0; i<H_GRID_SIZE; i++){
			row = new TableRow (this);	// create a new tableRow layout
	        row.setLayoutParams(tableRowParams);

	        row.setId(i);
	        layout.addView(row);	// add the row to the table layout
	        
			// create layout table columns 
			for (int j=0; j<V_GRID_SIZE; j++){				
				
				b = new ImageButton(this);
				b.setOnClickListener(this);	// add listener
				b.setOnLongClickListener(this); // add listener for setting bomb flg
				row.addView(b);			// add the button to the table row layout	
				
				c = new MineCell(i, j, b);		// creates the cell 
				mineField.addMineCell(i, j, c);	// link the cell to the mineArray
				c.paintComponent();
			}			
		}
	}
	

    /*
     * respond to clik event
     * short click is used to uncover cell. Long click is used to put flag, not relevant to this method
     */
    public void onClick(View v) {
    int nbAdjacentBombs ;
		
	int id = v.getId();
	
	// button gameInfo
	if (id == gameInfo.getId()) {
		showHint();
		return;
	}
	
	// mineField cells
	int i = (int) Math.floor((double) (id/10));	//cell coordinates derived from layout cell id
	int j = (id - i*10);
	
		MineCell cellPict = mineField.getCell(i, j);
		
		// we will perform action only if cell is still covered or has mine flag
		if (	gameOn && 
				(	cellPict.getDisplayStatus() == MineCell.STATUS_COVERED
				|| 	cellPict.getDisplayStatus() == MineCell.STATUS_FLAG )) {

			// step on a bomb !!
			if (cellPict.HasMine()) {
				cellPict.setdisplayStatus(MineCell.STATUS_BOMB);
				cellPict.paintComponent();
				
				gameOn = false;		// GAME OVER !
				
				mineField.uncoverMines ();	// display all bombs
				
				//TODO put some tempo before displaying alert				
				alertYouLoose.show();		// dialog to propose a new game
				return;
			}
			else {
				// calculate number to display
				nbAdjacentBombs = mineField.calculateAdjacentMines (cellPict);
				
				if (cellPict.getDisplayStatus() == MineCell.STATUS_FLAG) {
					nbBombsGuessed--;	// mine flag was removed
					updateStatusText();
				}
				
				cellPict.setdisplayStatus(nbAdjacentBombs);	// sets number to display
				cellPict.paintComponent();
				
				//if no adjacent bombs, uncover as much as possible
				if (nbAdjacentBombs == 0) {
					mineField.uncoverAdjacent (cellPict);
				}
				
				//if no more cells to uncover, game over, user wins
				if (!mineField.remainingCellsToUncover ()){
					gameOn = false;		// GAME OVER 
					
					//TODO put some tempo before displaying alert
					alertYouWin.show();		// dialog to propose a new game
				}
					
			}
		}
		
	} // END on_click()
    
    /*
     * long click on a cell is used to put a flag on a mine
     * 
     */
    public  boolean onLongClick(View v) {
    	int id = v.getId();
    	int i = (int) Math.floor((double) (id/10));
    	int j = (id - i*10);

    	MineCell cellPict = mineField.getCell(i, j);
    	
    	if (gameOn && cellPict.getDisplayStatus() == MineCell.STATUS_COVERED) {
    		if (nbBombsGuessed == NB_BOMBS) {
    			Toast.makeText(this, String.format("you already placed %s mine flags !", nbBombsGuessed)
    					, Toast.LENGTH_LONG).show();
    			//TODO beep error
    		}
    		else {
				// long click sets flag
				cellPict.setdisplayStatus(MineCell.STATUS_FLAG);
				cellPict.paintComponent();
				
				nbBombsGuessed++;
				updateStatusText();
	    		}
			}
    	
    	//if no more cells to uncover, game over, user wins
		if (!mineField.remainingCellsToUncover ()){
			gameOn = false;		// GAME OVER 
			
			//TODO put some tempo before displaying alert
			alertYouWin.show();		// dialog to propose a new game
		}
		
    	return true;
    }
    

	// start menu first time user press on the "menu" touch
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater ();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    //handle menu options clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()) {
    	case R.id.preferences:
    		Intent settingsActivity = new Intent(getBaseContext(),
                    Preferences.class);
    		startActivity(settingsActivity);
    		break;
    	case R.id.newGame:
    		// start a new game
    		//popup of confirmation in case game is started
    		if (gameOn) {
    			// TODO confirmation pour lancer nouvelle partie 
    		}
    		newGame();
    		break;
    	}
    	
    	return true;
    }

	/**
	 * starts a new game
	 */
	private void newGame() {
		if (!mineFieldDimensionHasChanged){
			//Log.d(TAG, "mine field dimension has not changed. Resetting mine field.");  
			mineField.resetMineField();
			mineField.placeMines(NB_BOMBS);
			nbBombsGuessed = 0;
			updateStatusText();
			gameOn=true;
 	   }
 	   else {
 		   // we need to restart a new class instance because mine field size has changed
 		   // best solution because difficult to reset table layout
			//Log.d(TAG, "mine field dimensions have changed. Starting a new intent.");
			Intent MSActivityIntent = new Intent(getBaseContext(), MSActivity.class);
    			startActivity(MSActivityIntent);	// start a new class instance

    			// promotes GC
    			this.finish();	// to speed up activity dispose. Not sure if it's usefull at all    
    			mineField = null;
 	   }
	}
    

	/*
	 * Toast the game rule 
	 */
	private void showHint(){
		String text = "There are "+Integer.toString(NB_BOMBS)+" bombs in the minefield.\nTap to uncover a cell.\nLong tap to flag a mine.";
        
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
	
	private void updateStatusText(){
		statusText.setText(String.format("total # of mines : %d\nmines guessed : %d", NB_BOMBS, nbBombsGuessed));
	}
	
    // Preference Change handler
	public void onSharedPreferenceChanged(SharedPreferences prefs, String arg1) {

		Toast.makeText(this, "Your changes will be implemented in the next game. Use the 'Back' button to go back to the game.", Toast.LENGTH_LONG).show();
		if (Integer.parseInt(prefs.getString("H_SIZE", Integer.toString(H_GRID_SIZE))) != H_GRID_SIZE
			||Integer.parseInt(prefs.getString("V_SIZE", Integer.toString(V_GRID_SIZE))) != V_GRID_SIZE ) {
			mineFieldDimensionHasChanged = true;	// will tell to start a new class instance if user wants to start a new game
		}
		
		//update other preference values in case they were changed
		NB_BOMBS = Integer.parseInt(prefs.getString("NB_BOMBS", Integer.toString(NB_BOMBS))); 
	 
	}

}