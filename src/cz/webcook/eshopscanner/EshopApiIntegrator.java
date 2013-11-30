package cz.webcook.eshopscanner;

import android.annotation.SuppressLint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

@SuppressLint("NewApi")
public class EshopApiIntegrator {
	
	private String apiUrl;
	
	private String identificator;
	
	private String token;
	
	private final String productsAction = "product";
	
	private final String ordersAction = "order";
	
	public EshopApiIntegrator(String apiUrl, String identificator, String token) {
		
		this.apiUrl = apiUrl;
		this.identificator = identificator;
		this.token = token;
	}

	public JSONObject uploadProduct(Product product){
		
		// Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(this.apiUrl + this.productsAction);

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("title", product.getName()));
	        nameValuePairs.add(new BasicNameValuePair("price", product.getPrice().toString()));
	        nameValuePairs.add(new BasicNameValuePair("barcode", product.getBarcode()));
	        nameValuePairs.add(new BasicNameValuePair("barcodeType", product.getBarcodeType()));
	        nameValuePairs.add(new BasicNameValuePair("vat", Integer.toString(product.getVat())));
	        nameValuePairs.add(new BasicNameValuePair("store", Integer.toString(product.getStore())));
	        nameValuePairs.add(new BasicNameValuePair("identificator", this.identificator));
	        nameValuePairs.add(new BasicNameValuePair("token", this.token));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	        InputStream in = response.getEntity().getContent();
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        String text = null;
			try {
	          while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	          }
	          text = sb.toString();
	        } catch (Exception ex) {

	        } finally {
	          try {

	            in.close();
	          } catch (Exception ex) {
	          }
	        }
	        
			JSONObject r = new JSONObject(text);
					
			return r;
	        
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	e.getStackTrace();
	    	
	    	return null;
	    } catch(Exception e){
	    	e.getStackTrace();
	    	
	    	return null;
	    }
		
	}
	
	/**
	 * @return the apiUrl
	 */
	public String getApiUrl() {
		return apiUrl;
	}

	/**
	 * @param apiUrl the apiUrl to set
	 */
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	/**
	 * @return the identificator
	 */
	public String getIdentificator() {
		return identificator;
	}

	/**
	 * @param identificator the identificator to set
	 */
	public void setIdentificator(String identificator) {
		this.identificator = identificator;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
}
