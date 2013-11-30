package cz.webcook.eshopscanner.models;

import java.util.ArrayList;


public class Order {
	
	private String name = "Eshop scanner";
	
	private ArrayList<Product> products;

	public void addProduct(Product product){
		
		this.products.add(product);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the products
	 */
	public ArrayList<Product> getProducts() {
		return products;
	}

	/**
	 * @param products the products to set
	 */
	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}
}
