package cz.webcook.eshopscanner;

import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProductAdapter extends ArrayAdapter<Product> {

	public ProductAdapter(Context context, List<Product> objects) {
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
