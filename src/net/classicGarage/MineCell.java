package net.classicGarage;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableRow.LayoutParams;

public class MineCell extends Activity {
	
	public static final int bg_covered_color = 0x8487F8;
	public static final int bg_uncovered_color = 0xC4C5D0;		
	
	private ImageButton picto;		// the graphical representation of the cell	
	private int x, y; 				// cell coordinates
	
	public ImageButton getPicto() {
		return picto;
	}

	public void setPicto(ImageButton picto) {
		this.picto = picto;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	static final int STATUS_0 = 0;		// no adjacent bombs
	static final int STATUS_1 = 1;		// 1 adjacent bomb
	static final int STATUS_2 = 2;
	static final int STATUS_3 = 3;
	static final int STATUS_4 = 4;
	static final int STATUS_5 = 5;
	static final int STATUS_6 = 6;		// should never happen
	static final int STATUS_7 = 7;		// should never happen
	static final int STATUS_COVERED = -1;
	static final int STATUS_FLAG = -2;
	static final int STATUS_BOMB = -3;  	// display a bomb ! means that game is over
	static final int STATUS_FLAG_WRONG = -4; 
	
	//TODO change status value. Put flag and bomb negative value. Add status 5 and 6 in case more than 4 adjacent bombs
	
	private int displayStatus; 			// covered / 1 mine around / 2 mines around .... / flagged for mine
	private boolean hasMine ; 		// true is a mine is on the cell
	private boolean hasMineFlag; 	// true if user has set a mine flag on this cell
	
	
	// accessors
	public int getDisplayStatus() {
		return displayStatus;
	}

	public void setdisplayStatus(int status) {
			this.displayStatus = status;
	}

	public boolean HasMine() {
		return hasMine;
	}

	public void setHasMine(boolean hasMine) {
		this.hasMine = hasMine;
	}

	public boolean HasMineFlag() {
		return hasMineFlag;
	}

	public void setHasMineFlag(boolean hasMineFlag) {
		this.hasMineFlag = hasMineFlag;
	}

	
	//constructor
	public MineCell(int x, int y, ImageButton b) {
		super();		
		this.x = x;
		this.y = y;
		this.displayStatus = STATUS_COVERED;
		this.hasMine = false;
		this.hasMineFlag = false;		
		
		this.picto = b;
		b.setId(10*x+y);

		ViewGroup.LayoutParams ImageButtonParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		b.setLayoutParams(ImageButtonParams);
		b.setBackgroundColor(bg_covered_color);
		b.setPadding(1, 1, 1, 1);
		b.setImageResource(R.drawable.ms_covered);	// picto : covered
	}
	
	// resets attributes of mineCell
	public void resetMineCell(){
		this.displayStatus = STATUS_COVERED;
		this.hasMine = false;
		this.hasMineFlag = false;
		this.picto.setBackgroundColor(bg_covered_color);
		this.picto.setImageResource(R.drawable.ms_covered);	// picto : covered
	}
	
	/*
	 * display cell picto
	 */
	public void paintComponent() {	
		
		switch (this.displayStatus) {
		case STATUS_COVERED:
			this.picto.setBackgroundColor(bg_covered_color);
			this.picto.setImageResource(R.drawable.ms_covered);	
			break;
		case STATUS_0: 
			this.picto.setBackgroundColor(bg_uncovered_color);
			this.picto.setImageResource(R.drawable.ms_uncovered);	
			break;	
		case STATUS_1:
			this.picto.setBackgroundColor(bg_uncovered_color);
			this.picto.setImageResource(R.drawable.ms_1);	
			break;
		case STATUS_2: 
			this.picto.setBackgroundColor(bg_uncovered_color);
			this.picto.setImageResource(R.drawable.ms_2);	
			break;
		case STATUS_3:
			this.picto.setBackgroundColor(bg_uncovered_color);
			this.picto.setImageResource(R.drawable.ms_3);	
			break;
		case STATUS_4:
			this.picto.setBackgroundColor(bg_uncovered_color);
			this.picto.setImageResource(R.drawable.ms_4);	
			break;
		case STATUS_FLAG: 
			this.picto.setBackgroundColor(bg_uncovered_color);
			this.picto.setImageResource(R.drawable.ms_flag);	
			break;
		case STATUS_BOMB:
			this.picto.setBackgroundColor(bg_uncovered_color);
			this.picto.setImageResource(R.drawable.ms_bomb_explode);	
			break;
		case STATUS_FLAG_WRONG:
			this.picto.setBackgroundColor(bg_uncovered_color);
			this.picto.setImageResource(R.drawable.ms_flag_wrong);	
			break;	
		default:
			this.picto.setBackgroundColor(bg_uncovered_color);
			this.picto.setImageResource(R.drawable.ms_what_the_heck);	
			break;	
		}

	}
}



