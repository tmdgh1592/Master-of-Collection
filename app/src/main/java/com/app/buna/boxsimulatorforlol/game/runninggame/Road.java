package com.app.buna.boxsimulatorforlol.game.runninggame;
import android.content.SharedPreferences;
import android.graphics.Canvas;

public class Road {
	
	Game game;
	float y;
		
	final int MAX_DIVIDERS = 1;
	float [] dividerX;
		
	public Road(Game game) {
		this.game = game;
		y = game.groundY;
		
		dividerX = new float[MAX_DIVIDERS];		
	}
	
	public void reset() {
		float xOffset = 0.0f;		
		for (int i=0; i<MAX_DIVIDERS; i++) {
			dividerX[i] = xOffset;
			xOffset += 80.0f; 			
		}		
	}
	
	public void update() {
		for (int i=0; i<MAX_DIVIDERS; i++) {
			dividerX[i] -= 10.0f;
			if (dividerX[i] < -60.0f) {
				dividerX[i] = game.width + 10.0f;				
			}
		}
	}
	
	public void drawDivider(Canvas canvas) {
		for (int i=0; i<MAX_DIVIDERS; i++) {			
			canvas.drawBitmap(game.dividerImage, dividerX[i], y-25.0f, game.emptyPaint);
		}		
	}

	public void drawRoad(Canvas canvas) {
		canvas.drawBitmap(game.roadImage, 0, y, game.emptyPaint);
	}
	
	public void restore(SharedPreferences savedState) {		
		for (int i=0; i<MAX_DIVIDERS; i++) {
			dividerX[i] = savedState.getFloat("road_div_" + i + "_x", 0);
		}
	}
	
	public void save(SharedPreferences.Editor map) {
		for (int i=0; i<MAX_DIVIDERS; i++) {
			map.putFloat("road_div_" + i + "_x", dividerX[i]);
		}
	}	
}
