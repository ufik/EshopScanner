package cz.webcook.eshopscanner.services;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import cz.webcook.eshopscanner.AeSimpleSHA1;
import cz.webcook.eshopscanner.models.Product;

@SuppressLint("NewApi")
public class EshopApiIntegrator {
	
	private String apiUrl;
	
	private String identificator;
	
	private String token;
	
	private static final String ACTION_PRODUCT = "product";
	
	private static final String ACTION_ORDER = "order";
	
	private static final String ACTION_BARCODE = "barcode";
	
	private static final String ACTION_CONFIGURATION = "configuration";
	
	public static final int ONLY_WITHOUT_BARCODE = 0;
	
	public static final int ALL_PRODUCTS = 1;
	
	public EshopApiIntegrator(String apiUrl, String identificator, String token) {
		
		this.apiUrl = apiUrl;
		this.identificator = identificator;
		this.token = token;
	}

	public JSONObject uploadProduct(Product product){
		
		// Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(this.apiUrl + ACTION_PRODUCT);

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("title", product.getName()));
	        nameValuePairs.add(new BasicNameValuePair("price", product.getPrice().toString()));
	        nameValuePairs.add(new BasicNameValuePair("barcode", product.getBarcode()));
	        nameValuePairs.add(new BasicNameValuePair("barcodeType", product.getBarcodeType()));
	        nameValuePairs.add(new BasicNameValuePair("vat", Integer.toString(product.getVat())));
	        nameValuePairs.add(new BasicNameValuePair("store", Integer.toString(product.getStore())));
	        nameValuePairs.add(new BasicNameValuePair("hash", this.getApiHash(this.identificator, this.token)));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	        String text = this.decodeResponse(response);
	        
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
	
	public JSONObject uploadBarcode(Product product){
		// Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(this.apiUrl + ACTION_BARCODE);

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(product.getId())));
	        nameValuePairs.add(new BasicNameValuePair("barcode", product.getBarcode()));
	        nameValuePairs.add(new BasicNameValuePair("barcodeType", product.getBarcodeType()));
	        nameValuePairs.add(new BasicNameValuePair("hash", this.getApiHash(this.identificator, this.token)));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	        String text = this.decodeResponse(response);
	        
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
	
	public JSONObject downloadProducts(int config) {
		
		// Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    
	    String url;
	    if(config == ONLY_WITHOUT_BARCODE){
	    	url = this.apiUrl + ACTION_PRODUCT + "?onlyWithoutBarcode=1";
	    }else{
	    	url = this.apiUrl + ACTION_PRODUCT;
	    }
		
	    HttpGet httpget = new HttpGet(url);
	    
	    HttpResponse response = null;
	    try {
			response = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    String text = this.decodeResponse(response);
	    
	    JSONObject r = null;
		try {
			r = new JSONObject(text);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return r;
	}
	
	public boolean checkSettings() {
		
		// Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(this.apiUrl + ACTION_CONFIGURATION);
	    
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("hash", this.getApiHash(this.identificator, this.token)));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	       
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	        String text = this.decodeResponse(response);
	        
			JSONObject r = new JSONObject(text);
			
			if(r.getString("status").equals("200")){
				return true;
			}else{
				return false;
			}
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	e.getStackTrace();
	    	return false;
	    } catch(Exception e){
	    	e.getStackTrace();
	    	
	    	return false;
	    }
	}
	
	public String decodeResponse(HttpResponse response){
		
		InputStream in = null;
		try {
			in = response.getEntity().getContent();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		
		return text;
	}
	
	public String getApiHash(String identificator, String token){
		
		String hash = null;
		try {
			hash = AeSimpleSHA1.SHA1(identificator + token + identificator);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return hash;
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
