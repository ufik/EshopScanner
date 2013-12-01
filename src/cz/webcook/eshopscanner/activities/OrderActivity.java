package cz.webcook.eshopscanner.activities;

import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import cz.webcook.eshopscanner.InternalStorage;
import cz.webcook.eshopscanner.R;
import cz.webcook.eshopscanner.activities.BarcodeActivity.ProductUploadTask;
import cz.webcook.eshopscanner.adapters.OrderAdapter;
import cz.webcook.eshopscanner.models.Order;
import cz.webcook.eshopscanner.services.EshopApiIntegrator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class OrderActivity extends AbstractActivity{

	private ArrayList<Order> orders;
	
	private final static String ORDER_KEY = "ordersStorage";
	
	private ArrayAdapter<Order> aa;
	
	private SharedPreferences prefs;
	
	private EshopApiIntegrator eai;
	
	private ProgressDialog waitDlg;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		
		ListView lv = (ListView) findViewById(R.id.listView1);
	    
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		String apiUrl = prefs.getString("url", "");
		String identificator = prefs.getString("identificator", "");
		String token = prefs.getString("token", "");
		
		eai = new EshopApiIntegrator(apiUrl, identificator, token);
		
		initializeOrders();
		
		Bundle bundle = getIntent().getExtras();
		
		if(bundle != null){
			if(bundle.containsKey("order")){
				Order order = (Order) bundle.getSerializable("order");
				
				if(order != null){
					addOrder(order);
					saveState();
				}
			}
		}
		
        // set adapter
		aa = new OrderAdapter(OrderActivity.this, orders);
		lv.setAdapter(aa);
		
		registerForContextMenu(lv);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> av, View v, int index,
					long arg3) {

				Intent i = new Intent(OrderActivity.this, OrderFormActivity.class);
				i.putExtra("order", orders.get(index));
				startActivity(i);
				
				finish();
				
			}
		});

	}

	private void clearOrders(){
		orders.clear();
		updateOrderAdapter();
		saveState();
	}
	
	public void updateOrderAdapter(){
    	
    	aa.notifyDataSetChanged();
    }
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
    	menu.add(Menu.NONE, 0, Menu.NONE, R.string.context_delete);
    }
	
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case 0:

	        	deleteOrderByItemId(info.id);
	        	updateOrderAdapter();
	        	
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	private void deleteOrderByItemId(Long itemId) {
		this.orders.remove(Integer.parseInt(itemId.toString()));
		saveState();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_new_order:
                
            	Intent i = new Intent(OrderActivity.this, OrderFormActivity.class);
				startActivity(i);
				
				finish();
            	
                break;
            case R.id.action_upload_orders:
            	
            	if(isNetworkAvailable()){
            		new OrderUploadTask().execute(this.orders);
            	}else{
            		Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            	}
            	
            	break;
            case R.id.action_clear_orders:
            	
            	clearOrders();
				
            	break;
            case R.id.action_settings:
                
            	Intent i1 = new Intent(OrderActivity.this, SettingsActivity.class);
            	startActivity(i1);
            	
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        
		return false;
    }
	
	private void initializeOrders() {
		try {
			this.orders = (ArrayList<Order>) InternalStorage.readObject(this, ORDER_KEY);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				this.orders = new ArrayList<Order>();
				InternalStorage.writeObject(this, ORDER_KEY, this.orders);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}
	
	private void saveState(){
		try {
			
			InternalStorage.writeObject(this, ORDER_KEY, this.orders);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addOrder(Order p){
		
		if(p.getId() == 0){
			if(this.orders.size() > 0){
				Order lastOrder = this.orders.get(this.orders.size() - 1);
				int id = lastOrder.getId();
				id++;
				p.setId(id);
			}else{
				p.setId(1);
			}
		}
		
		Order tmpOrder = null;
		for (Order order : this.orders) {
			if(p.getId() == order.getId()){
				tmpOrder = order;
			}
		}
		
		if(tmpOrder != null){
			this.orders.remove(tmpOrder);
		}
		
		p.setName(getString(R.string.order_default_name) + " " + p.getId());
		this.orders.add(p);	

		saveState();
	}

	public class OrderUploadTask extends AsyncTask<ArrayList<Order>, Integer, ArrayList<JSONObject>> {
		
		@Override
		protected void onPostExecute(ArrayList<JSONObject> result) {
			
			if(waitDlg != null)
				waitDlg.dismiss();
			
			if(result != null){ 
				for (JSONObject jsonObject : result) {
					try {
						if(jsonObject != null){
							String message = jsonObject.getString("message");
							
							Toast.makeText(OrderActivity.this, message, Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(OrderActivity.this, "bad config", Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				clearOrders();
				
			}else{
				Toast.makeText(OrderActivity.this, "unknow error", Toast.LENGTH_LONG).show();
			}
			
		}

		@Override
		protected void onPreExecute() {
			
			waitDlg = new ProgressDialog(OrderActivity.this);
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
		
		@Override
		protected ArrayList<JSONObject> doInBackground(ArrayList<Order>... orders) {
			
			ArrayList<Order> iterates = orders[0];
			
			ArrayList<JSONObject> res = new ArrayList<JSONObject>();
			int count = iterates.size();
			for (int i = 0; i < iterates.size(); i++) {
				
				res.add(eai.uploadOrder(iterates.get(i)));				
				publishProgress((int) ((i / (float) count) * 100));				

			}
			 		
			return res;
		}
		
	}
}
