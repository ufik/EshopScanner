package cz.webcook.eshopscanner.activities;

import java.util.ArrayList;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import cz.webcook.eshopscanner.InternalStorage;
import cz.webcook.eshopscanner.R;
import cz.webcook.eshopscanner.R.id;
import cz.webcook.eshopscanner.R.layout;
import cz.webcook.eshopscanner.R.menu;
import cz.webcook.eshopscanner.adapters.ProductAdapter;
import cz.webcook.eshopscanner.models.Order;
import cz.webcook.eshopscanner.models.Product;
import cz.webcook.eshopscanner.services.ProductsService;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class OrderFormActivity extends Activity {

	private Order order = null;
	
	private ArrayAdapter<Product> aa;
	
	private SharedPreferences prefs;
	
	private ProductsService ps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_form);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		ps = new ProductsService(getApplicationContext());
		
		this.order = new Order();
		
		// default values
		Bundle bundle = getIntent().getExtras();
		
		if(savedInstanceState != null){
			if(savedInstanceState.containsKey("order")){
				Order order = (Order) savedInstanceState.getSerializable("order");
				this.order = order;
			}
		}
		
		if(bundle != null && this.order.getId() == 0){
			
			if(bundle.containsKey("order")){
				Order order = (Order) bundle.getSerializable("order");
				this.order = order;
				
			}
		}
		
		setTitle(this.order.getName());
		
		// list products
		ListView lv = (ListView) findViewById(R.id.listView1);
		
		// set adapter
		aa = new ProductAdapter(OrderFormActivity.this, this.order.getProducts(), prefs);
		lv.setAdapter(aa);
		
		registerForContextMenu(lv);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> av, View v, int index,
					long arg3) {
	
			}
		});
	}

	protected void onResume(){
		super.onResume();
		
	}
	
	protected void onRestoreInstanceState(Bundle bundle){
		
	}
	
	protected void onSaveInstanceState(Bundle state) {
	    super.onSaveInstanceState(state);
	    
	    state.putSerializable("order", this.order);
	}
	
	public void updateProductAdapter(){
    	
    	aa.notifyDataSetChanged();
    }
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
    	menu.add(Menu.NONE, 0, Menu.NONE, R.string.context_delete);
    }

	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case 0:

	        	deleteProductByItemId(info.id);
	        	updateProductAdapter();
	        	
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	private void deleteProductByItemId(Long itemId) {
		this.order.getProducts().remove(Integer.parseInt(itemId.toString()));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order_form, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_scan_product:
                
            	IntentIntegrator scanIntegrator = new IntentIntegrator(this);
    			scanIntegrator.initiateScan();
            	
                break;
            case R.id.action_save_order:
            	
            	Intent i = new Intent(OrderFormActivity.this, OrderActivity.class);
            	i.putExtra("order", this.order);
				startActivity(i);
				
				finish();
            	
            	break;
            case R.id.action_settings:
                
            	Intent i1 = new Intent(OrderFormActivity.this, SettingsActivity.class);
            	startActivity(i1);
            	
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        
		return false;
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve scan result
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		
		if (scanningResult != null) {
			//we have a result
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			
			Product newProduct = ps.getProductByBarcode(scanContent);
			
			if(newProduct == null){
				newProduct = new Product();
				newProduct.setBarcode(scanContent);
				newProduct.setBarcodeType(scanFormat);
			}
			
			this.order.addProduct(newProduct);
			
		}else{
		    Toast toast = Toast.makeText(getApplicationContext(),
		            "No scan data received!", Toast.LENGTH_SHORT);
		      
		    toast.show();
		}
	}
}
