package com.app.buna.boxsimulatorforlol.game.runninggame;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.app.buna.boxsimulatorforlol.R;


public class RunningGameActivity extends Activity implements FinishCallback {
	
	public static final String PREFS_NAME = "DRJPrefsFile";
	
	RunJumpView drjView;
	RunJumpView.DroidRunJumpThread drjThread;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_running_game);
        drjView = (RunJumpView) findViewById(R.id.droidrunjump);
        drjView.setCallback(this);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();

    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
       	SharedPreferences.Editor editor = settings.edit();
       	
       	drjThread = drjView.getThread();
       	       	    	
       	// if player wants to quit then reset the game
    	if (isFinishing()) {
    		drjThread.resetGame();
    	}
    	else {	    	
    		drjThread.pause();
    	}
    	
       	drjThread.saveGame(editor);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();    
    	// restore game
    	drjThread = drjView.getThread();
	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	   	drjThread.restoreGame(settings);  
    }


	@Override
	public void onFinish() {
		finish();
	}
}

interface FinishCallback {
	void onFinish();
}