package com.pcinpact.connection;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.util.Log;

public class HtmlConnector {
	
	public static final String TAG = "WCFConnector";	
	
	private IConnectable Delegate;
	
	private static final int HTTP_OK                    = 200;
	
	private Context context;
	
	boolean running;
	
	public int state;
	public String tag;
	
	public HtmlConnector(Context context, IConnectable delegate)
	{
		this.context = context;
		this.Delegate = delegate;
		running = true;
	}
	
	public void stop()
	{
		running = false;
	}
	
	public void sendRequest(final String _url, final String httpMethodType, final String postData, final Map<String, String> headers)
	{
		
		 
        byte[] dataAsBytes =  postData.getBytes();
        final float outgoing_data_length = dataAsBytes.length;
		InputStream is = null;
		
		try 
		{
			is = new ByteArrayInputStream(postData.getBytes("UTF-8"));
		}
		
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		
		final InputStream outgoing_is = is;
		
		Thread t = new Thread(new Runnable() {
			
			public void run() 
			{
				/*try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				request(_url, httpMethodType, outgoing_is,outgoing_data_length, headers);
			}
		});
		
		t.start();
	}
	
	public void sendRequest(final String _url, final String httpMethodType, final InputStream outgoing_is, final float outgoing_data_length, final Map<String, String> headers)
	{
		Thread t = new Thread(new Runnable() {
			
			public void run() 
			{
				/*try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				request(_url, httpMethodType, outgoing_is,outgoing_data_length, headers);
			}
		});
		
		t.start();
	}
	
	
	
	public void request(String _url, String httpMethodType, InputStream outgoing_is, float outgoing_data_length, Map<String, String> headers)
	{	
		
		ConnectivityManager l_Connection = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (l_Connection.getActiveNetworkInfo()== null || !l_Connection.getActiveNetworkInfo().isConnected())
		{
			Delegate.didFailWithError("Vous n'êtes pas connecté à internet, veuillez vérifier vos paramètres de connexion",state);
			return;
		}
		
		URL url = null;
		
		 ByteArrayOutputStream outputStream = null;
		 this.running = true;
			
	
		try 
		{
			url = new URL(_url);
		}
		catch (MalformedURLException e) 
		{
			Delegate.didFailWithError(e.getMessage(),state);
    		return;
		}
			
		

		HttpURLConnection connection = null;
		try 
		{
			connection = (HttpURLConnection) url.openConnection();
			
		} 
		catch (IOException e) 
		{
			Delegate.didFailWithError(e.getMessage(),state);
		
    		return;
		}
		
		if(httpMethodType.equals("POST"))
			connection.setDoOutput(true);
	    
	    
		try 
    	{
			connection.setRequestMethod(httpMethodType);
			
		} 
    	catch (ProtocolException e) 
    	{
    		Delegate.didFailWithError(e.getMessage(),state);
    	
    		return;
		}

		
		
	    
		
	    if(headers!=null)
		    for(Entry<String, String> entry :  headers.entrySet()) 
		    {
			    String key = entry.getKey();
			    String value = entry.getValue();
			    
				connection.setRequestProperty(key,value);	    	    
	    	}
	      
	  

		
	   if(httpMethodType.equals("POST"))
	   {
		    OutputStream writer = null;
			try 
			{
				writer = connection.getOutputStream();
			} 
			catch (IOException e) 
			{
				Delegate.didFailWithError(e.getMessage(),state);
				
	    		return;
	    
			}
	    	
	    	byte[] output_buffer = new byte[128];
	        int totalReadBytesCount = 0;
	        int readBytesCount;
	        try 
	        {
	        	
				while (((readBytesCount = outgoing_is.read(output_buffer, 0, output_buffer.length)) > 0) && running)
				{
					writer.write(output_buffer, 0, readBytesCount);
				    totalReadBytesCount += readBytesCount;
				    float progress = (float) (totalReadBytesCount * 100.0 / outgoing_data_length);
				    Delegate.setUploadProgress((int) progress);	 
				   
				}
				
			} 
	        catch (IOException e) 
			{
				Delegate.didFailWithError(e.getMessage(),state);
				
	    		return;
			}
	        
	        
	        
	        if (outgoing_is != null) 
			{
				try 
				{
					outgoing_is.close();
				
				} 
				catch (IOException e) 
				{
					Delegate.didFailWithError(e.getMessage(),state);
				
		    		return;
				}
			}
	   }    	
	    
    	try 
    	{
    		connection.connect();
    	
		} 
    	catch (IOException e) 
    	{
    		Delegate.didFailWithError(e.getMessage(),state);
    		return;
		}
    	
		
	    int responseCode = 0;
	    
		try 
		{
			responseCode = connection.getResponseCode();
		} 
		catch (IOException e) 
		{
			Delegate.didFailWithError(e.getMessage(),state);
    		return;
		}
	    	
		if (responseCode != HTTP_OK && running)
		{
			if(running)
				Delegate.didFailWithError("HTTP NOT OK",state);
			
			return;
		}
		else
		{
			InputStream incoming_is = null;
			try 
			{
				incoming_is = connection.getInputStream();
			} 
			catch (IOException e) 
			{
				Delegate.didFailWithError(e.getMessage(),state);
	    		return;
			}
			
			outputStream = new ByteArrayOutputStream();
			
			byte[] buffer = new byte[128];
			int read = 0;
			int dataSize = connection.getContentLength();

			try 
			{
				while (((read = incoming_is.read(buffer, 0, 128)) > 0) && running)
				{					
					outputStream.write(buffer, 0, read);	
					float progress = ((float)outputStream.size()*100/dataSize);
					Delegate.setDownloadProgress((int) progress);				
				}
			} 
			catch (IOException e) 
			{
				Delegate.didFailWithError(e.getMessage(),state);
	    		return;
			}			

			try 
			{
				incoming_is.close();
			} 
			catch (IOException e) 
			{
				Delegate.didFailWithError(e.getMessage(),state);
	    		return;
			}
				
			if(running)
				Delegate.didConnectionResult(outputStream.toByteArray(),state,tag);
		}
    
	}
	
}

