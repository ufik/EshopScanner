package cz.webcook.eshopscanner.models;

import java.io.Serializable;

public class Product implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	
	private String name;
	
	private Float price;
	
	private String barcode;
	
	private String barcodeType;
	
	private int vat;
	
	private int store;

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
	 * @return the price
	 */
	public Float getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(Float price) {
		this.price = price;
	}

	/**
	 * @return the barcode
	 */
	public String getBarcode() {
		return barcode;
	}

	/**
	 * @param barcode the barcode to set
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	/**
	 * @return the barcodeType
	 */
	public String getBarcodeType() {
		return barcodeType;
	}

	/**
	 * @param barcodeType the barcodeType to set
	 */
	public void setBarcodeType(String barcodeType) {
		this.barcodeType = barcodeType;
	}

	/**
	 * @return the vat
	 */
	public int getVat() {
		return vat;
	}

	/**
	 * @param vat the vat to set
	 */
	public void setVat(int vat) {
		this.vat = vat;
	}

	/**
	 * @return the store
	 */
	public int getStore() {
		return store;
	}

	/**
	 * @param store the store to set
	 */
	public void setStore(int store) {
		this.store = store;
	}
}
