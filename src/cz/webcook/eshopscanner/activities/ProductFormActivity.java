package cz.webcook.eshopscanner.activities;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import cz.webcook.eshopscanner.R;
import cz.webcook.eshopscanner.R.id;
import cz.webcook.eshopscanner.R.layout;
import cz.webcook.eshopscanner.R.menu;
import cz.webcook.eshopscanner.R.string;
import cz.webcook.eshopscanner.models.Product;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class ProductFormActivity extends Activity implements OnClickListener {

	private EditText editName;
	
	private EditText editPrice;
	
	private EditText editBarcode;
	
	private EditText editBarcodeType;
	
	private EditText editVat;
	
	private EditText editStore;
	
	private Button buttonSave;
	
	private Button buttonScan;
	
	private Button buttonCamera;
	
	private Product product;
	
	private String activity;
	
	private Gallery sdcardImages;
	private ImageAdapter iAdapter = new ImageAdapter(this);
	
	/**
     * Cursor used to access the results from querying for images on the SD card.
     */
    private Cursor cursor;
    /*
     * Column index for the Thumbnails Image IDs.
     */
    private int columnIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_form);
		
		editName = (EditText) findViewById(R.id.editName);
		editPrice = (EditText) findViewById(R.id.editPrice);
		editBarcode = (EditText) findViewById(R.id.editBarcode);
		editBarcodeType = (EditText) findViewById(R.id.editBarcodeType);
		editVat = (EditText) findViewById(R.id.editVat);
		editStore = (EditText) findViewById(R.id.editStore);
		
		this.product = new Product();
		
		// default values
		Bundle bundle = getIntent().getExtras();
		
		if(bundle != null){
			if(bundle.containsKey("product")){
				Product product = (Product) bundle.getSerializable("product");
				
				if(product != null){
					this.product = product;
					editName.setText(product.getName());
					editPrice.setText(product.getPrice().toString());
					editBarcode.setText(product.getBarcode());
					editBarcodeType.setText(product.getBarcodeType());
					editVat.setText(Integer.toString(product.getVat()));
					editStore.setText(Integer.toString(product.getStore()));
				}
			}
			
			if(bundle.containsKey("activity")){
				activity = bundle.getString("activity");
			}
			
		}
		
		setTitle(this.product.getName());
		
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonScan = (Button) findViewById(R.id.buttonScan);
		buttonCamera = (Button) findViewById(R.id.buttonCamera);
		
		buttonSave.setOnClickListener(this);
		buttonScan.setOnClickListener(this);
		buttonCamera.setOnClickListener(this);
		
		this.updateCursor();

        sdcardImages = (Gallery) findViewById(R.id.gallery1);
        sdcardImages.setAdapter(iAdapter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		this.updateCursor();
        iAdapter.notifyDataSetChanged();
	}
	
	private void updateCursor(){
		
		String[] projection = {MediaStore.Images.Thumbnails._ID};
		
		// Create the cursor pointing to the SDCard
        cursor = managedQuery( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, 
                MediaStore.Images.Media.DATA + " like ? ",
                new String[] {"%EshopScanner/"+ product.getBarcode() +"%"},  
                null);
        // Get the column index of the Thumbnails Image ID
        if(cursor != null){
        	if(cursor.getColumnCount() > 0) columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
        }
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.product_form, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                
            	Intent i1 = new Intent(ProductFormActivity.this, SettingsActivity.class);
            	startActivity(i1);
            	
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.buttonSave){
			
			Boolean ok = true;
			if(editBarcode.getText().toString().length() == 0){
				ok = false;
			}
			
			if(editPrice.getText().toString().length() == 0){
				editPrice.setText("0");
			}
			
			if(editVat.getText().toString().length() == 0){
				editVat.setText("0");
			}
			
			if(editStore.getText().toString().length() == 0){
				editStore.setText("0");
			}
			
			if(ok){
				this.product.setName(editName.getText().toString());
				this.product.setPrice(Float.valueOf(editPrice.getText().toString()));
				this.product.setBarcode(editBarcode.getText().toString());
				this.product.setBarcodeType(editBarcodeType.getText().toString());
				this.product.setVat(Integer.valueOf(editVat.getText().toString()));
				this.product.setStore(Integer.valueOf(editStore.getText().toString()));
				
				Intent i = null;
				if(activity.equals("product")){ 
					i = new Intent(ProductFormActivity.this, ProductsActivity.class);
				}else if(activity.equals("barcode")){
					i = new Intent(ProductFormActivity.this, BarcodeActivity.class);
				}
				
				i.putExtra("product", this.product);
				startActivity(i);
				
				finish();
			
			}else{
				Toast.makeText(this, R.string.product_form_data_error, Toast.LENGTH_LONG).show();
			}
			
		}else if(v.getId() == R.id.buttonScan){
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
		}else if(v.getId() == R.id.buttonCamera){
			Intent i = new Intent(ProductFormActivity.this, CameraActivity.class);
			i.putExtra("product", this.product);
			startActivity(i);
		}
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve scan result
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		
		if (scanningResult != null) {
			//we have a result
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			
			editBarcodeType.setText(scanFormat);
			editBarcode.setText(scanContent);
			
		}else{
		    Toast toast = Toast.makeText(getApplicationContext(),
		            "No scan data received!", Toast.LENGTH_SHORT);
		      
		    toast.show();
		}
	}
	
	/**
     * Adapter for our image files.
     */
    private class ImageAdapter extends BaseAdapter {

        private Context context;

        public ImageAdapter(Context localContext) {
            context = localContext;
        }

        public int getCount() {
            try {
            	return cursor.getCount();
			} catch (Exception e) {
				return 0;
			}
        	
        }
        
        public Object getItem(int position) {
            return position;
        }
        
        public long getItemId(int position) {
            return position;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
        	ImageView i = new ImageView(context);
            // Move cursor to current position
            cursor.moveToPosition(position);
            // Get the current value for the requested column
            int imageID = cursor.getInt(columnIndex);
            
            Bitmap b = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(),
            		imageID, MediaStore.Images.Thumbnails.MINI_KIND, null);

            i.setImageBitmap(b);
            i.setLayoutParams(new Gallery.LayoutParams(150, 100));
            i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setBackgroundResource(0);
            return i;
        }

    }

}
