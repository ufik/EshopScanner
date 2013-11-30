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

public class BarcodeAdapter extends ArrayAdapter<Product> {

	public BarcodeAdapter(Context context, List<Product> objects) {
		super(context, 0, objects);
	}

	public View getView(int position, View cv, ViewGroup parent) {
		
		Product r = getItem(position);
		
		if (cv == null){
			cv = View.inflate(getContext(), R.layout.product_item, null);
		}
		
		TextView name = (TextView) cv.findViewById(R.id.itemName);
		TextView barcode = (TextView) cv.findViewById(R.id.itemBarcode);
		
		name.setText(r.getName());
		barcode.setText(r.getBarcode() + " / " + r.getBarcodeType());
		
		return cv;
	}

}
