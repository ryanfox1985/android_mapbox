package com.devcows.android_mapbox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MBTilesLayer;
import com.mapbox.mapboxsdk.views.MapView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapView mapView = (MapView) this.findViewById(R.id.mapview);

        Context context = getApplicationContext();

        copyDatabaseFromAssets(context, "spain.mbtiles", false);

        File fileDb = context.getDatabasePath("spain.mbtiles");
        String newPath = fileDb.getPath();
        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(newPath, null, SQLiteDatabase.OPEN_READWRITE);

        MBTilesLayer mbTilesLayer = new MBTilesLayer(myDataBase);
        mapView.setTileSource(mbTilesLayer);

        //aLatitude, double aLongitude
        LatLng latLng = new LatLng(41.117184, 1.210498);
        Marker marker = new Marker(mapView, "Xusma", "Cuidado con la cartera", latLng);
        mapView.addMarker(marker);

        mapView.setUserLocationEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Copy database file from assets folder inside the apk to the system database path.
     * @param context Context
     * @param databaseName Database file name inside assets folder
     * @param overwrite True to rewrite on the database if exists
     * @return True if the database have copied successfully or if the database already exists without overwrite, false otherwise.
     */
    private boolean copyDatabaseFromAssets(Context context, String databaseName , boolean overwrite)  {

        File outputFile = context.getDatabasePath(databaseName);
        if (outputFile.exists() && !overwrite) {
            return true;
        }

        outputFile = context.getDatabasePath(databaseName + ".temp");
        outputFile.getParentFile().mkdirs();

        try {
            InputStream inputStream = context.getAssets().open(databaseName);
            OutputStream outputStream = new FileOutputStream(outputFile);


            // transfer bytes from the input stream into the output stream
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Close the streams
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            outputFile.renameTo(context.getDatabasePath(databaseName));

        } catch (IOException e) {
            if (outputFile.exists()) {
                outputFile.delete();
            }
            return false;
        }

        return true;
    }

}
