package com.sungazerstudios.btaware.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class CommService extends IntentService {
	private static String TAG = "CommService";
	public static String START_COMMS_SERVICE = "com.sungazerstudios.btaware.android.scc";
	public static String BEACON_NAME_EXTRA = "beaconnameextra";
	public static String BEACON_MAC_EXTRA = "beaconmacextra";
	public static UUID MY_UUID = UUID
			.fromString("3b04a01b-cc4f-4d7d-9367-9a0e034d047d");

	private static int THIRTEEN_SECONDS = 13000;

	boolean inRange;
	ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
	String beaconMAC;
	BluetoothAdapter mBA;
	Intent mIntent;

	public CommService() {
		super("BTACommService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent.getCategories().contains(START_COMMS_SERVICE)) {
			Toast.makeText(getApplicationContext(), "Communications started.",
					Toast.LENGTH_SHORT).show();
		}
		mIntent = intent;
		beaconMAC = intent.getStringExtra(CommService.BEACON_MAC_EXTRA);

		mBA = BluetoothAdapter.getDefaultAdapter();

		final BroadcastReceiver mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// When discovery finds a device
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					// Get the BluetoothDevice object from the Intent
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					devices.add(device);
					if (device.getAddress().equals(beaconMAC)) {
						customActions(mIntent, true);
					}
					Log.d(TAG, "Added " + device.getAddress() + " to devices.");
				} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
						.equals(action)) {
					devices.clear();
					Log.d(TAG, "Cleared all devices.");
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {
					boolean answer = false;
					for (BluetoothDevice device : devices) {
						if (device.getAddress().equals(beaconMAC)) {
							answer = true;
						}
					}
					Log.d(TAG, "Finished finding devices. ");
					customActions(mIntent, answer);
					Toast.makeText(getApplicationContext(),
							"Processed discovery.", Toast.LENGTH_SHORT).show();
				}
			}
		};
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter); // Don't forget to unregister
												// during onDestroy

		while (mBA.isEnabled()) {
			mBA.startDiscovery();
			try {
				Thread.sleep(THIRTEEN_SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
		String toastText = "No change in target. "
				+ Boolean.toString(targetInRange);
		if (targetInRange != inRange) {
			try {
				playSound(this);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			inRange = targetInRange;
			toastText = "Target in range.";
			if (!inRange) {
				toastText = "Not in range.";
			}
		}
		Log.d("Found the thing:", Boolean.toString(targetInRange));
		Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "Communications stopped.", Toast.LENGTH_SHORT)
				.show();
	}

	public void playSound(Context context) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {
		Uri soundUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		MediaPlayer mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setDataSource(context, soundUri);
		final AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		}
	}

}