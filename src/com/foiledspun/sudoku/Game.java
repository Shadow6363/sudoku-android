package com.foiledspun.sudoku;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Game extends Activity {
	private static final String TAG = "Sudoku";
	
	public static final String KEY_DIFFICULTY =
		"com.foiledspun.sudoku.difficulty";
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_MEDIUM = 1;
	public static final int DIFFICULTY_HARD = 2;
	
	private int puzzle[] = new int[9 * 9];
	
	private PuzzleView puzzleView;
	
	private final int used[][][] = new int[9][9][];
	
	private final String easyPuzzle =
		"3600000000004230800000004200" +
		"070460003820000014500013020" +
		"001900000007048300000000045";
	private final String mediumPuzzle =
		"3600000000004230800000004200" +
		"070460003820000014500013020" +
		"001900000007048300000000045";
	private final String hardPuzzle =
		"3600000000004230800000004200" +
		"070460003820000014500013020" +
		"001900000007048300000000045";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		
		int diff = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
		puzzle = getPuzzle(diff);
		calculateUsedTiles();
		
		puzzleView = new PuzzleView(this);
		setContentView(puzzleView);
		puzzleView.requestFocus();
	}
	
	protected void showKeypadOrError(int x, int y) {
		int tiles[] = getUsedTiles(x, y);
		if(tiles.length == 9) {
			Toast toast = Toast.makeText(this, R.string.no_moves_label, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		} else {
			Log.d(TAG, "showKeypad: used=" + toPuzzleString(tiles));
			Dialog v = new Keypad(this, tiles, puzzleView);
			v.show();
		}
	}
	
	protected boolean setTileIfValid(int x, int y, int value) {
		int tiles[] = getUsedTiles(x, y);
		if (value != 0) { for (int tile : tiles) { if (tile == value) { return false; } } }
		setTile(x, y, value);
		calculateUsedTiles();
		return true;
	}
	
	protected int[] getUsedTiles(int x, int y) { return used[x][y]; }
	
	private void calculateUsedTiles() {
		for (int x = 0; x < 9; x++) { for (int y = 0; y < 9; y++) { used[x][y] = calculateUsedTiles(x, y); } }
	}
	
	private int[] calculateUsedTiles(int x, int y) {
		int c[] = new int[9];
		for (int i = 0; i < 9; i++) {
			if(i == y) { continue; }
			int t = getTile(x, i);
			if (t != 0) { c[t - 1] = t; }
		}
		for (int i = 0; i < 9; i++) {
			if(i == x) { continue; }
			int t = getTile(i, y);
			if (t != 0) { c[t - 1] = t; }
		}
		int startX = (x / 3) * 3;
		int startY = (y / 3) * 3;
		for (int i = startX; i < startX + 3; i++) {
			for (int j = startY; j < startY + 3; j++) {
				if (i == x && j == y) { continue; }
				int t = getTile(i, j);
				if (t != 0) { c[t - 1] = t; }
			}
		}
		int nused = 0;
		for (int t : c) { if (t != 0) { nused++; } }
		int c1[] = new int[nused];
		nused = 0;
		for (int t : c) { if (t != 0) { c1[nused++] = t; } }
		return c1;
	}
	
	private int[] getPuzzle(int diff) {
		String puz;
		switch (diff) {
		case DIFFICULTY_HARD:
			puz = hardPuzzle; break;
		case DIFFICULTY_MEDIUM:
			puz = mediumPuzzle; break;
		case DIFFICULTY_EASY:
		default:
			puz = easyPuzzle; break;
		} return fromPuzzleString(puz);
	}
	
	private static String toPuzzleString(int[] puz) {
		StringBuilder buf = new StringBuilder();
		for (int element : puz) { buf.append(element); }
		return buf.toString();
	}
	
	protected static int[] fromPuzzleString(String string) {
		int[] puz = new int[string.length()];
		for (int i = 0; i < puz.length; i++) { puz[i] = string.charAt(i) - '0'; }
		return puz;
	}
	
	private int getTile(int x, int y) { return puzzle[y * 9 + x]; }
	private void setTile(int x, int y, int value) {puzzle[y * 9 + x] = value; }
	
	protected String getTileString(int x, int y) {
		int v = getTile(x, y);
		return v == 0 ? "" : String.valueOf(v);
	}
}
