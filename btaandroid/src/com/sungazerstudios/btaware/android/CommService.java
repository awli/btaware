package com.sungazerstudios.btaware.android;

import java.util.Set;
import java.util.UUID;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

public class CommService extends IntentService {
	private static String TAG = "CommService";
	public static String START_COMMS_SERVICE = "com.sungazerstudios.btaware.android.scc";
	public static String BEACON_NAME_EXTRA = "beaconnameextra";
	public static String BEACON_MAC_EXTRA = "beaconmacextra";
	public static UUID MY_UUID = UUID
			.fromString("3b04a01b-cc4f-4d7d-9367-9a0e034d047d");

	boolean inRange;

	public CommService() {
		super("BTACommService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent.getCategories().contains(START_COMMS_SERVICE)) {
			Toast.makeText(getApplicationContext(), "Communications started.",
					Toast.LENGTH_SHORT).show();
		}

		String beaconMAC = intent.getStringExtra(CommService.BEACON_MAC_EXTRA);

		BluetoothAdapter mBA = BluetoothAdapter.getDefaultAdapter();
		
		// Create a BroadcastReceiver for ACTION_FOUND
		final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		            // Get the BluetoothDevice object from the Intent
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		            // Add the name and address to an array adapter to show in a ListView
		            //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
		        }
		    }
		};
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
		
		while (mBA.isEnabled()) {
			Set<BluetoothDevice> pairedDevices = mBA.getBondedDevices();
			// If there are paired devices
			boolean targetInRange = false;
			if (pairedDevices.size() > 0) {
				// Loop through paired devices
				for (BluetoothDevice device : pairedDevices) {
					// Determine if the device is the one we want
					if (device.getAddress() == beaconMAC) {
						targetInRange = true;
					}
				}
			}
			customActions(intent, targetInRange);
			
			
			
			
			
		}
		Log.d(TAG, "The Bluetooth Adapter is disabled, stopping service...");
		this.stopSelf();
	}

	/**
	 * Custom actions that I do based on the Intent received the program.
	 * 
	 * @param intent
	 *            Intent that indicates what we want to do. (Usually, the intent
	 *            that started the activity)
	 * @param targetInRange
	 */
	private void customActions(Intent intent, boolean targetInRange) {
		if (targetInRange != inRange) {
			inRange = targetInRange;
			String toastText = "Target in range. ";
			if (!inRange) {
				toastText = "Not in range.";
			}
			Toast.makeText(getApplicationContext(), toastText,
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Communications stopped.", Toast.LENGTH_SHORT)
				.show();
	}
}