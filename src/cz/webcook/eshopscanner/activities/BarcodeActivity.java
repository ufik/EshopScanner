package cz.webcook.eshopscanner.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.webcook.eshopscanner.InternalStorage;
import cz.webcook.eshopscanner.R;
import cz.webcook.eshopscanner.R.id;
import cz.webcook.eshopscanner.R.layout;
import cz.webcook.eshopscanner.R.menu;
import cz.webcook.eshopscanner.R.string;
import cz.webcook.eshopscanner.adapters.BarcodeAdapter;
import cz.webcook.eshopscanner.models.Product;
import cz.webcook.eshopscanner.services.EshopApiIntegrator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BarcodeActivity extends Activity {

	private SharedPreferences prefs;
	
	private EshopApiIntegrator eai;
	
	private ArrayAdapter<Product> aa;
	
	private ProgressDialog waitDlg;
	
	public ArrayList<Product> products;
	
	private final static String PRODUCT_KEY = "productsBarcodeStorage";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_barcode);
		
		initializeProducts();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		String apiUrl = prefs.getString("url", "");
		String identificator = prefs.getString("identificator", "");
		String token = prefs.getString("token", "");
		
		eai = new EshopApiIntegrator(apiUrl, identificator, token);
		
		Bundle bundle = getIntent().getExtras();
		
		if(bundle != null){
			if(bundle.containsKey("product")){
				Product product = (Product) bundle.getSerializable("product");
				
				if(product != null){
					addProduct(product);
				}
			}
		}
		
		ListView lv = (ListView) findViewById(R.id.listView1);
		
		// set adapter
		aa = new BarcodeAdapter(BarcodeActivity.this, products);
		lv.setAdapter(aa);
		
		registerForContextMenu(lv);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> av, View v, int index,
					long arg3) {

				Intent i = new Intent(BarcodeActivity.this, ProductFormActivity.class);
				i.putExtra("product", products.get(index));
				i.putExtra("activity", "barcode");
				startActivity(i);
				
				finish();
				
			}
		});
	}

	private void addProduct(Product p){
		
		Product tmpProduct = null;
		for (Product product : this.products) {
			if(p.getId() == product.getId()){
				tmpProduct = product;
			}
		}
		
		if(tmpProduct != null){
			this.products.remove(tmpProduct);
		}
		
		this.products.add(p);	

		saveState();
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
	
	private void clearProducts(){
		products.clear();
		updateProductAdapter();
		saveState();
	}
	
	public void updateProductAdapter(){
    	
    	aa.notifyDataSetChanged();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.barcode, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_download:
                
            	new ProductDownloadTask().execute(EshopApiIntegrator.ONLY_WITHOUT_BARCODE);
            	
                return true;
            
            case R.id.action_upload:
            	
            	new ProductUploadTask().execute(products);
            
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	public class ProductDownloadTask extends AsyncTask<Integer, Integer, JSONObject> {
		
		protected void onPostExecute(JSONObject result) {
			
			if(waitDlg != null)
				waitDlg.dismiss();
			
			if(result != null){ 
					
				String message;
				try {
					
					message = result.getString("message");
					
					JSONArray response = result.getJSONArray("response");
					
					clearProducts();
					for (int i = 0; i < response.length(); i++) {
						JSONObject product = (JSONObject) response.get(i);
						
						Product p = new Product();
						p.setName(product.getString("title"));
						p.setId(product.getInt("id"));
						p.setBarcode(product.getString("barcode"));
						p.setBarcodeType(product.getString("barcodeType"));
						p.setPrice(Float.valueOf(product.getString("price")));
						
						products.add(p);
						
						Log.d("product", product.getString("title"));
					}
					
					saveState();
					updateProductAdapter();
					
					Toast.makeText(BarcodeActivity.this, message, Toast.LENGTH_LONG).show();
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}else{
				Toast.makeText(BarcodeActivity.this, "unknow error", Toast.LENGTH_LONG).show();
			}
		}

		protected void onPreExecute() {
			
			waitDlg = new ProgressDialog(BarcodeActivity.this);
			waitDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			waitDlg.setTitle(R.string.progress_title);
			waitDlg.setMessage(getString(R.string.progress_download_message));
			waitDlg.setIndeterminate(false);
			
			if(waitDlg != null)
				waitDlg.show();
			
		}
		
		protected void onProgressUpdate(Integer... progress) {
	        waitDlg.setProgress(progress[0]);
	     }
		
		protected JSONObject doInBackground(Integer... config) {
			
			JSONObject res = eai.downloadProducts(config[0]);				
			 		
			return res;
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
							
							Toast.makeText(BarcodeActivity.this, message, 3000).show();
						}else{
							Toast.makeText(BarcodeActivity.this, "bad config", 3000).show();
						}
						
						clearProducts();
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{
				Toast.makeText(BarcodeActivity.this, "unknow error", 3000).show();
			}
			
		}

		protected void onPreExecute() {
			
			waitDlg = new ProgressDialog(BarcodeActivity.this);
			waitDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			waitDlg.setTitle(R.string.progress_title);
			waitDlg.setMessage(getString(R.string.progress_upload_message));
			waitDlg.setIndeterminate(false);
			
			if(waitDlg != null)
				waitDlg.show();
			
		}
		
		protected void onProgressUpdate(Integer... progress) {
	        waitDlg.setProgress(progress[0]);
	     }
		
		protected ArrayList<JSONObject> doInBackground(ArrayList<Product>... products) {
			
			ArrayList<Product> iterates = products[0];
			
			ArrayList<JSONObject> res = new ArrayList<JSONObject>();
			int count = iterates.size();
			for (int i = 0; i < iterates.size(); i++) {
				
				res.add(eai.uploadBarcode(iterates.get(i)));				
				publishProgress((int) ((i / (float) count) * 100));				

			}
			 		
			return res;
		}
	}
}
