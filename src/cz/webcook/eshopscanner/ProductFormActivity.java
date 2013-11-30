package cz.webcook.eshopscanner;

import java.text.BreakIterator;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import cz.webcook.eshopscanner.activities.MainActivity;
import cz.webcook.eshopscanner.activities.SettingsActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
	
	private Product product;
	
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
		
		// default values
		Bundle bundle = getIntent().getExtras();
		
		if(bundle != null){
			if(bundle.containsKey("product")){
				Product product = (Product) bundle.getSerializable("product");
				
				if(product != null){
					editName.setText(product.getName());
					editPrice.setText(product.getPrice().toString());
					editBarcode.setText(product.getBarcode());
					editBarcodeType.setText(product.getBarcodeType());
					editVat.setText(Integer.toString(product.getVat()));
					editStore.setText(Integer.toString(product.getStore()));
				}
			}
		}
		
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonScan = (Button) findViewById(R.id.buttonScan);
		
		buttonSave.setOnClickListener(this);
		buttonScan.setOnClickListener(this);
		
		product = new Product();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.product_form, menu);
		return true;
		
		
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
			
			if(ok){
				this.product.setName(editName.getText().toString());
				this.product.setPrice(Float.valueOf(editPrice.getText().toString()));
				this.product.setBarcode(editBarcode.getText().toString());
				this.product.setBarcodeType(editBarcodeType.getText().toString());
				this.product.setVat(Integer.valueOf(editVat.getText().toString()));
				this.product.setStore(Integer.valueOf(editStore.getText().toString()));
				
				Intent i = new Intent(ProductFormActivity.this, ProductsActivity.class);
				i.putExtra("product", this.product);
				startActivity(i);
				
				finish();
			
			}else{
				Toast.makeText(this, R.string.product_form_data_error, Toast.LENGTH_LONG).show();
			}
			
		}else if(v.getId() == R.id.buttonScan){
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
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

}
