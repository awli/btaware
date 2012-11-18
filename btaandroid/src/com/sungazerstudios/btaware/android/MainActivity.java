package com.sungazerstudios.btaware.android;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static int REQUEST_ENABLE_BT = 42;
	boolean commsOn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void onResume() {
		super.onResume();
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// TODO Device does not support Bluetooth
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		// TODO Figure out where I am.

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(),
						"This application doesn't work without Bluetooth...",
						Toast.LENGTH_LONG).show();
			} else if (resultCode == RESULT_OK) {
				Toast.makeText(getApplicationContext(), "Bluetooth enabled.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void startComms(View v) {
		if (commsOn == false) {
			//TODO Start communications. 
			Intent intent = new Intent(this, CommService.class);
			this.startService(intent);
			Toast.makeText(getApplicationContext(), "Communications started.",
					Toast.LENGTH_SHORT).show();
			commsOn = true;
			TextView commsOnDisplay = (TextView) findViewById(R.id.commsOnDisplay);
			commsOnDisplay.setText("Communications on.");
		}
		else {
			Toast.makeText(getApplicationContext(), "Communications already started!",
					Toast.LENGTH_SHORT).show();
			commsOn = false;
		}
	}

	public void stopComms(View v) {
		Toast.makeText(getApplicationContext(), "Communications stopped.",
				Toast.LENGTH_SHORT).show();
		TextView commsOnDisplay = (TextView) findViewById(R.id.commsOnDisplay);
		commsOnDisplay.setText("Communications off.");
	}
}
