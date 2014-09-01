package com.example.bitquest;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends ActionBarActivity{
	
	// Declare TextView variables.
	TextView findLat;
	TextView findLong;
	TextView locLat;
	TextView locLong;
	TextView distance;
	TextView totDest;
	TextView foundDest;
	
	//Declare Button variables.
	Button	scanQR;
	Button restart;

	//Set destination latitude and longitude coordinates.
	double fLatNext[] = { 41.744631, 41.744324, 41.744304, 41.744290, 41.745003, 41.745187, 41.744592, 0};
	double fLongNext[] = {-111.801078, -111.801139, -111.800335, -111.799538, -111.799692, -111.800985, -111.801153, 0};
	
	//Initialize latitude and longitude variables.
	double fLat = fLatNext[0];
	double fLong = fLongNext[0];
	
	//Initialize number of destinations variable and count variables.
	int locations = fLatNext.length-1;
	int count = 0;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//go to main layout
		setContentView(R.layout.activity_main);
		
		//initialize textviews and buttons.
		findLat = (TextView)findViewById(R.id.findLat);
		findLong = (TextView)findViewById(R.id.findLong); 
		locLat = (TextView)findViewById(R.id.locLat);
		locLong = (TextView)findViewById(R.id.locLong);
		distance = (TextView)findViewById(R.id.distance);
		totDest = (TextView)findViewById(R.id.totDest);
		foundDest = (TextView)findViewById(R.id.foundDest);
		scanQR = (Button)findViewById(R.id.bScanQR);
		restart = (Button)findViewById(R.id.restart);
	
		//set up scan button functionality
		scanQR.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent qrDroidScan = new Intent("la.droid.qr.scan");
				qrDroidScan.putExtra("la.droid.qr.complete", true);
				startActivityForResult(qrDroidScan, 0);
				
			}
		});
		
		//setup restart button functionality
		restart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				count=0;
				fLat=fLatNext[count];
				fLong=fLongNext[count];
				foundDest.setText(Integer.toString(count));
				
			}
		});

		//show destinations information
		findLat.setText(Double.toString(fLat));
		findLong.setText(Double.toString(fLong));
		totDest.setText(Integer.toString(locations));
		foundDest.setText(Integer.toString(count));
		
		//use location services to find current location
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationListener ll = new mylocationlistener();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);	
		
	}
	
	//update location information and calculate straight line distance to next destination
		class mylocationlistener implements LocationListener{

			@Override
			public void onLocationChanged(Location location) {
				if(location != null)
				{
					double lLong = location.getLongitude();
					double lLat = location.getLatitude();
					
					locLat.setText(Double.toString(lLat));
					locLong.setText(Double.toString(lLong));
					
					Location originLocation = new Location("gps");
					Location destinationLocation = new Location("gps");
					originLocation.setLatitude(lLat);
					originLocation.setLongitude(lLong);
					destinationLocation.setLatitude(fLat);
					destinationLocation.setLongitude(fLong);
					float dist = originLocation.distanceTo(destinationLocation);
					
					distance.setText(Float.toString(dist) + " meters");
					
					//set up "geo fence" and alert player that destination has been found. 
					if(dist<2.75){		
						if(count<locations){
							foundDest.setText(Integer.toString(count+1));
							playAlertTone(getApplicationContext());
							Toast.makeText(MainActivity.this, "You found your destination!", Toast.LENGTH_LONG).show();
							fLat=fLatNext[count+1];
							fLong=fLongNext[count+1];
							count++;
						} 
						//Notify player that quest is finished. Restart to first destination
						if(count>=locations){
								Toast.makeText(MainActivity.this, "You completed your quest!", Toast.LENGTH_LONG).show();
								count=0;
								fLat=fLatNext[count];
								fLong=fLongNext[count];
						}
					}

				}

				
			}
			
			
			//function to play destination found audio alert
			public  void playAlertTone(final Context context){


			    Thread t = new Thread(){
			            public void run(){
			                MediaPlayer player = null;
			                int countBeep = 0;
			                while(countBeep<1){
			                player = MediaPlayer.create(context,R.raw.chime_up);
			                player.start();
			                countBeep+=1;
			                try {

			                                // 100 milisecond is duration gap between two beep
			                    Thread.sleep(player.getDuration()+100);
			                                       player.release();
			                } catch (InterruptedException e) {
			                    // TODO Auto-generated catch block
			                    e.printStackTrace();
			                }


			                }
			            }
			        };

			        t.start();   

			    }


			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
		}
		
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
