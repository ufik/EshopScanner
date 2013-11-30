package cz.webcook.eshopscanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import android.os.AsyncTask;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import cz.webcook.eshopscanner.activities.SettingsActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ProductsActivity extends Activity {

	private ArrayList<Product> products;
	
	private final static String PRODUCT_KEY = "productsStorage";
	
	private ArrayAdapter<Product> aa;
	
	private SharedPreferences prefs;
	
	private EshopApiIntegrator eai;
	
	private ProgressDialog waitDlg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_products);
		
		ListView lv = (ListView) findViewById(R.id.listView1);
	    
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		String apiUrl = prefs.getString("url", "");
		String identificator = prefs.getString("url", "");
		String token = prefs.getString("url", "");
		
		eai = new EshopApiIntegrator(apiUrl, identificator, token);
		
		initializeProducts();
		
		Bundle bundle = getIntent().getExtras();
		
		if(bundle != null){
			if(bundle.containsKey("product")){
				Product product = (Product) bundle.getSerializable("product");
				
				if(product != null){
					addProduct(product);
				}
			}
		}
		
        // set adapter
		aa = new ProductAdapter(ProductsActivity.this, products);
		lv.setAdapter(aa);
		
		registerForContextMenu(lv);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> av, View v, int index,
					long arg3) {

				Intent i = new Intent(ProductsActivity.this, ProductFormActivity.class);
				i.putExtra("product", products.get(index));
				startActivity(i);
				
				finish();
				
			}
		});
	}

	private void addProduct(Product p){
		
		Product tmpProduct = null;
		for (Product product : this.products) {
			if(p.getBarcode().equals(product.getBarcode())){
				tmpProduct = product;
			}
		}
		
		if(tmpProduct != null){
			this.products.remove(tmpProduct);
		}
		
		this.products.add(p);	

		saveState();
	}
	
	private void deleteProductByBarcode(String barcode){
		
		Product tmpProduct = null;
		for (Product product : this.products) {
			if(barcode.equals(product.getBarcode())){
				tmpProduct = product;
			}
		}
		
		if(tmpProduct != null){
			this.products.remove(tmpProduct);
			saveState();
		}
	}
	
	private void saveState(){
		try {
			
			Collections.sort(this.products, new Comparator<Product>(){
			    public int compare(Product p1, Product p2) {
			        return p1.getName().compareToIgnoreCase(p2.getName());
			    }
			});
			
			InternalStorage.writeObject(this, PRODUCT_KEY, this.products);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initializeProducts() {
		try {
			this.products = (ArrayList<Product>) InternalStorage.readObject(this, PRODUCT_KEY);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				this.products = new ArrayList<Product>();
				InternalStorage.writeObject(this, PRODUCT_KEY, this.products);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
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
		this.products.remove(Integer.parseInt(itemId.toString()));
		saveState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.products, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                
            	Intent i = new Intent(ProductsActivity.this, SettingsActivity.class);
				startActivity(i);
            	
                return true;
                
            case R.id.action_new_product:
            	
            	Intent i1 = new Intent(ProductsActivity.this, ProductFormActivity.class);
				startActivity(i1);
            	
				return true;
				
            case R.id.action_upload:
            	
            	new ProductUploadTask().execute(this.products);
				
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	public class ProductUploadTask extends AsyncTask<ArrayList<Product>, Integer, ArrayList<JSONObject>> {
		
		@Override
		protected void onPostExecute(ArrayList<JSONObject> result) {
			
			if(waitDlg != null)
				waitDlg.dismiss();
			
			if(result != null){ 
				for (JSONObject jsonObject : result) {
					try {
						if(jsonObject != null){
							String message = jsonObject.getString("message");
							
							Toast.makeText(ProductsActivity.this, message, 3000).show();
						}else{
							Toast.makeText(ProductsActivity.this, "bad config", 3000).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{
				Toast.makeText(ProductsActivity.this, "unknow error", 3000).show();
			}
			
		}

		@Override
		protected void onPreExecute() {
			
			waitDlg = new ProgressDialog(ProductsActivity.this);
			waitDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			waitDlg.setTitle(R.string.progress_upload_title);
			waitDlg.setMessage(getString(R.string.progress_upload_message));
			waitDlg.setIndeterminate(false);
			
			if(waitDlg != null)
				waitDlg.show();
			
		}
		
		protected void onProgressUpdate(Integer... progress) {
	        waitDlg.setProgress(progress[0]);
	     }
		
		@Override
		protected ArrayList<JSONObject> doInBackground(ArrayList<Product>... products) {
			
			ArrayList<Product> iterates = products[0];
			
			ArrayList<JSONObject> res = new ArrayList<JSONObject>();
			int count = iterates.size();
			for (int i = 0; i < iterates.size(); i++) {
				
				res.add(eai.uploadProduct(iterates.get(i)));				
				publishProgress((int) ((i / (float) count) * 100));				

			}
			 		
			return res;
		}
		
	}
}
