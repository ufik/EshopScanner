package cz.webcook.eshopscanner.adapters;

import java.util.List;

import cz.webcook.eshopscanner.R;
import cz.webcook.eshopscanner.R.id;
import cz.webcook.eshopscanner.R.layout;
import cz.webcook.eshopscanner.activities.ProductsActivity;
import cz.webcook.eshopscanner.models.Product;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ProductAdapter extends ArrayAdapter<Product> {
	
	private Context context;
	private SharedPreferences sp;
	
	public ProductAdapter(Context context, List<Product> objects, SharedPreferences sp) {
		super(context, 0, objects);
		
		this.context = context;
		this.sp = sp;
	}

	public View getView(int position, View cv, ViewGroup parent) {
		
		Boolean simpleView = sp.getBoolean("product_view", false);
		
		final Product r = getItem(position);
		
		if (cv == null){
			cv = View.inflate(getContext(), R.layout.product_item, null);
		}
		
		CheckBox upload = (CheckBox) cv.findViewById(R.id.uploadCheckbox);
		TextView name = (TextView) cv.findViewById(R.id.itemName);
		TextView barcode = (TextView) cv.findViewById(R.id.itemBarcode);
		TextView price = (TextView) cv.findViewById(R.id.itemPrice);
		
		final CheckBox copyUpload = upload;
		
		name.setText(r.getName());
		barcode.setText(this.context.getString(R.string.title_barcode) + " " + r.getBarcode() + " / " + r.getBarcodeType());
		price.setText(this.context.getString(R.string.title_price) + " " + r.getPrice() + " (" + r.getVat() + "% " + this.context.getString(R.string.title_vat) + ")");
		if(r.getChecked() != null){
			upload.setChecked(r.getChecked());
		}
		
		upload.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Boolean checked;
				if(copyUpload.isChecked()){
					checked = true;
				}else{
					checked = false;
				}
				
				((ProductsActivity) context).updateProduct(r, checked);
			}
		});
		
		if(simpleView){
			barcode.setVisibility(View.GONE);
			price.setVisibility(View.GONE);
		}else{
			barcode.setVisibility(View.VISIBLE);
			price.setVisibility(View.VISIBLE);
		}
		
		return cv;
	}

}
