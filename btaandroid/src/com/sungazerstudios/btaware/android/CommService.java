package com.sungazerstudios.btaware.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CommService extends Service {
    public CommService() {
    }
    
    public int onStartCommand (Intent intent, int flags, int startId) {
    	return 1;
    }
    
    public void onCreate() {
    	super.onCreate();
    }
    
    public void onDestroy() {
    	super.onDestroy();
    }
    
    

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
}
