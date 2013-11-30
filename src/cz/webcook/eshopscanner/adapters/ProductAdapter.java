package cz.webcook.eshopscanner.adapters;

import java.util.List;

import cz.webcook.eshopscanner.R;
import cz.webcook.eshopscanner.R.id;
import cz.webcook.eshopscanner.R.layout;
import cz.webcook.eshopscanner.models.Product;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProductAdapter extends ArrayAdapter<Product> {
	
	private Context context;
	
	public ProductAdapter(Context context, List<Product> objects) {
		super(context, 0, objects);
		
		this.context = context;
	}

	public View getView(int position, View cv, ViewGroup parent) {
		
		Product r = getItem(position);
		
		if (cv == null){
			cv = View.inflate(getContext(), R.layout.product_item, null);
		}
		
		TextView name = (TextView) cv.findViewById(R.id.itemName);
		TextView barcode = (TextView) cv.findViewById(R.id.itemBarcode);
		TextView price = (TextView) cv.findViewById(R.id.itemPrice);
		
		name.setText(r.getName());
		barcode.setText(this.context.getString(R.string.title_barcode) + " " + r.getBarcode() + " / " + r.getBarcodeType());
		price.setText(this.context.getString(R.string.title_price) + " " + r.getPrice() + " (" + r.getVat() + "% " + this.context.getString(R.string.title_vat) + ")");
		
		return cv;
	}

}
