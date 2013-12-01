package cz.webcook.eshopscanner.models;

import java.io.Serializable;
import java.util.ArrayList;


public class Order implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id = 0;
	
	private String name = "Eshop scanner";
	
	private ArrayList<Product> products = new ArrayList<Product>();

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

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	public Float getTotalPrice(){
		Float total = 0f;
		
		for (Product product : this.products) {
			total += product.getPrice();
		}
		
		return total;
	}
}
