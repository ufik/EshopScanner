package cz.webcook.eshopscanner.services;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

import cz.webcook.eshopscanner.InternalStorage;
import cz.webcook.eshopscanner.models.Product;

public class ProductsService {

	public static final String PRODUCT_KEY = "productsStorage";
	
	private ArrayList<Product> products;
	
	private Context context;
	
	public ProductsService(Context context){
		this.context = context;
		this.initializeProducts();
	}
	
	public Product getProductByBarcode(String barcode){
		for (Product product : this.products) {
			if(product.getBarcode().equals(barcode)){
				return product;
			}
		}
		
		return null;
	}
	
	public ArrayList<Product> getProducts(){
		return this.products;
	}
	
	private void initializeProducts() {
		try {
			this.products = (ArrayList<Product>) InternalStorage.readObject(this.context, ProductsService.PRODUCT_KEY);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				this.products = new ArrayList<Product>();
				InternalStorage.writeObject(this.context, ProductsService.PRODUCT_KEY, this.products);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}
}
