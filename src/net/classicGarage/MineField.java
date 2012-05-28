package net.classicGarage;

import java.util.Date;

import android.util.Log;

public class MineField {

	private int hGridSize;		// will be set by constructor
	private int vGridSize;		// will be set by constructor
	
	private MineCell[][] mineArray ; 	// array of mine cells

	/**
	 * constructor
	 */
	public MineField(int hSize, int vSize) {
		this.hGridSize = hSize;
		this.vGridSize = vSize;
		
		mineArray = new MineCell[hSize][vSize];
	}

	/**
	 * links a mine cell to a position in the mine field
	 */
	public void addMineCell(int i, int j, MineCell c) {
		mineArray[i][j] = c;	// link the cell to the mineArray
	}
	
	/**
	 * gets a reference to a mine cell 
	 */
	public MineCell getCell(int i, int j) {
		return mineArray [i][j];
	}
	
	/**
	 * place mines in mine field
	 */
	public void placeMines(int nbBombs) {
		// place mines
		int x1, y1;
			
		Date today=new Date();
		////Log.d("MineField", String.format("%td %tm", today, today));
		
		if (String.format("%td %tm", today, today).equals("14 02")) {
			// saint Valentin special
			////Log.d("MineField", "St Valentin");
			x1= (int) (Math.random() * hGridSize);
			if (x1 >= hGridSize-5 ) x1=hGridSize-5;
			y1= (int) (Math.random() * vGridSize);
			if (y1 >= vGridSize-5 ) y1=vGridSize-5;
			
			mineArray[x1][y1+1  ].setHasMine(true);
			mineArray[x1][y1+3  ].setHasMine(true);
			mineArray[x1+1][y1].setHasMine(true);
			mineArray[x1+1][y1+2].setHasMine(true);
			mineArray[x1+1][y1+4].setHasMine(true);
			mineArray[x1+2][y1  ].setHasMine(true);
			mineArray[x1+2][y1+4].setHasMine(true);
			mineArray[x1+3][y1+1].setHasMine(true);
			mineArray[x1+3][y1+3].setHasMine(true);
			mineArray[x1+4][y1+2].setHasMine(true);
		}
		else {
			int i=0;		
			while (i < nbBombs) {
				 x1= (int) (Math.random() * hGridSize);
				 y1= (int) (Math.random() * vGridSize);
				 
				 if (!mineArray[x1][y1].HasMine()) {  // make sure you don't get 2 times the same mine
						 mineArray[x1][y1].setHasMine(true);	// sets the mine flag
						 i++;
				 }				
			}
		}
	}
	

	// calculate number of adjacent mines
	public int calculateAdjacentMines (MineCell cellPict) {
		int x, y;
		int result=0 ;
		
		// calculate adjacent mines
		x = cellPict.getX();
		y = cellPict.getY();
		
		for (int i=Math.max (0, x-1); i<Math.min(hGridSize, x+2); i++) {
			for (int j=Math.max (0, y-1); j<Math.min(vGridSize, y+2); j++) {
				if (mineArray[i][j].HasMine()) result++;
			}				
		}
		return result;
	}
	
	// checks if exists adjacent cell with status 0
	public boolean hasAdjacentStatus0 (MineCell cellPict) {
		int x, y;
		boolean result = false;
		
		// calculate adjacent mines
		x = cellPict.getX();
		y = cellPict.getY();
		
		for (int i=Math.max (0, x-1); i<Math.min(hGridSize, x+2); i++) {
			for (int j=Math.max (0, y-1); j<Math.min(vGridSize, y+2); j++) {
				if (mineArray[i][j].getDisplayStatus() == MineCell.STATUS_0) {
					result=true;
					break;
				}
			}				
		}
		return result;
	}
	
	// will uncover all adjacent cells with count=0
	public void uncoverAdjacent (MineCell cellPict) {
		int x, y;
		MineCell c;
		
		// calculate adjacent bombs
		x = cellPict.getX();
		y = cellPict.getY();
		
		for (int i=Math.max (0, x-1); i<Math.min(hGridSize, x+2); i++) {
			for (int j=Math.max (0, y-1); j<Math.min(vGridSize, y+2); j++) {
				c = mineArray[i][j];
				// we uncover all cells with status 0 or adjacent cells to cells with status 0
				if (c.getDisplayStatus() == MineCell.STATUS_COVERED &&
						calculateAdjacentMines (c) == 0) { 
					c.setdisplayStatus(MineCell.STATUS_0);
					c.paintComponent();
					
					uncoverAdjacent (c); 	//recursive stuff
				}
				if (c.getDisplayStatus() == MineCell.STATUS_COVERED &&
						hasAdjacentStatus0 (c))  { 
					c.setdisplayStatus(calculateAdjacentMines (c));
					c.paintComponent();
					
					uncoverAdjacent (c); 	//recursive stuff
				}
			}
		}
	}
	
	// uncover all bombs in case user has clicked on a bomb
	public void uncoverMines () {
		MineCell c;
		
		for (int i=0; i<hGridSize; i++) {
			for (int j=0; j<vGridSize; j++) {
				c = mineArray[i][j];
				if (c.getDisplayStatus() == MineCell.STATUS_COVERED && c.HasMine()) {
					// show bomb
					c.setdisplayStatus(MineCell.STATUS_BOMB);
					c.paintComponent();					
					
					//TODO add sound
			        //c	.playSoundEffect(soundConstant);
				}
				if (c.getDisplayStatus() == MineCell.STATUS_FLAG && !c.HasMine()) {
					// user was wrong when he set the flag
					c.setdisplayStatus(MineCell.STATUS_FLAG_WRONG);
					c.paintComponent();	
				}
			}
		}
	}
	
	/*
	 * resets mine field for a new game
	 */
	public void resetMineField () {
		MineCell c;
		
		for (int i=0; i<hGridSize; i++) {
			for (int j=0; j<vGridSize; j++) {
				c = mineArray[i][j];				
				c.resetMineCell();
				c.paintComponent();									
			}
		}
	}
	
	// checks if remaining cells to uncover
	public boolean remainingCellsToUncover () {
		MineCell c;
		boolean result = false;
		
		for (int i=0; i<hGridSize; i++) {
			for (int j=0; j<vGridSize; j++) {
				c = mineArray[i][j];
				if (c.getDisplayStatus() == MineCell.STATUS_COVERED) {
					result = true;
					break;
					}
			}
		}
		return result;		
	}

}
