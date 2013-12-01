package cz.webcook.eshopscanner.adapters;

import java.util.List;
import cz.webcook.eshopscanner.R;
import cz.webcook.eshopscanner.models.Order;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OrderAdapter extends ArrayAdapter<Order> {
	
	private Context context;
	
	public OrderAdapter(Context context, List<Order> objects) {
		super(context, 0, objects);
		
		this.context = context;
	}

	public View getView(int position, View cv, ViewGroup parent) {
		
		Order o = getItem(position);
		
		if (cv == null){
			cv = View.inflate(getContext(), R.layout.order_item, null);
		}
		
		TextView name = (TextView) cv.findViewById(R.id.itemName);
		TextView productCount = (TextView) cv.findViewById(R.id.itemProductCount);
		TextView totalPrice = (TextView) cv.findViewById(R.id.itemTotalPrice);
		
		name.setText(o.getName());
		productCount.setText(context.getString(R.string.order_item_product_count) + " " + Integer.toString(o.getProducts().size()));
		totalPrice.setText(context.getString(R.string.order_item_total_price) + " " + Float.toString(o.getTotalPrice()));
		
		return cv;
	}

}
