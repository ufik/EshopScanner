package cz.webcook.eshopscanner.activities;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.Menu;
import cz.webcook.eshopscanner.R;

public class MainActivity extends Activity implements OnClickListener {

	private Button listProductsButton, fillinBarcodesButton, ordersButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listProductsButton = (Button)findViewById(R.id.buttonListProducts);
		fillinBarcodesButton = (Button)findViewById(R.id.buttonFillinBarcodes);
		ordersButton = (Button)findViewById(R.id.buttonListOrders);
		
		listProductsButton.setOnClickListener(this);
		fillinBarcodesButton.setOnClickListener(this);
		ordersButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                
            	Intent i = new Intent(MainActivity.this, SettingsActivity.class);
				startActivity(i);
            	
                return true;
                
            case R.id.action_exit:
            	
            	finish();
            	
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	@Override
	public void onClick(View v) {
		// list all saved products
		if(v.getId() == R.id.buttonListProducts){
			
			Intent i = new Intent(MainActivity.this, ProductsActivity.class);
			startActivity(i);
			
		}else if(v.getId() == R.id.buttonFillinBarcodes){
			
			Intent i = new Intent(MainActivity.this, BarcodeActivity.class);
			startActivity(i);
			
		}else if(v.getId() == R.id.buttonListOrders){
			
			Intent i = new Intent(MainActivity.this, OrderActivity.class);
			startActivity(i);
			
		}
	}

}
