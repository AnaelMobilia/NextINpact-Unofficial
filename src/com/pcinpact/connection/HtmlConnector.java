/*
 * Copyright 2013, 2014 Sami Ferhah, Anael Mobilia
 * 
 * This file is part of NextINpact-Unofficial.
 * 
 * NextINpact-Unofficial is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * NextINpact-Unofficial is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with NextINpact-Unofficial. If not, see <http://www.gnu.org/licenses/>
 */
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
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.net.ConnectivityManager;

import com.pcinpact.R;

public class HtmlConnector {

	public static final String TAG = "WCFConnector";

	private IConnectable Delegate;

	private static final int HTTP_OK = 200;

	private Context context;

	boolean running;

	public int state;
	public String tag;

	public HtmlConnector(Context context, IConnectable delegate) {
		this.context = context;
		this.Delegate = delegate;
		running = true;
	}

	public void stop() {
		running = false;
	}

	public void sendRequest(final String _url, final String httpMethodType, final String postData,
			final Map<String, String> headers) {

		byte[] dataAsBytes = postData.getBytes();
		final float outgoing_data_length = dataAsBytes.length;
		InputStream is = null;

		try {
			is = new ByteArrayInputStream(postData.getBytes("UTF-8"));
		}

		catch (UnsupportedEncodingException e) {
			Delegate.didFailWithError(e.getMessage(), state);
			return;
		}

		final InputStream outgoing_is = is;

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				request(_url, httpMethodType, outgoing_is, outgoing_data_length, headers);
			}
		});

		t.start();
	}

	public void sendRequest(final String _url, final String httpMethodType, final InputStream outgoing_is,
			final float outgoing_data_length, final Map<String, String> headers) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				request(_url, httpMethodType, outgoing_is, outgoing_data_length, headers);
			}
		});

		t.start();
	}

	public void request(String _url, String httpMethodType, InputStream outgoing_is, float outgoing_data_length,
			Map<String, String> headers) {

		ConnectivityManager l_Connection = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (l_Connection.getActiveNetworkInfo() == null || !l_Connection.getActiveNetworkInfo().isConnected()) {
			Delegate.didFailWithError(context.getString(R.string.chargementPasInternet), state);
			return;
		}

		URL url = null;

		ByteArrayOutputStream outputStream = null;
		this.running = true;

		try {
			url = new URL(_url);
		} catch (MalformedURLException e) {
			Delegate.didFailWithError(e.getMessage(), state);
			return;
		}

		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();

		} catch (IOException e) {
			Delegate.didFailWithError(e.getMessage(), state);
			return;
		}

		if (httpMethodType.equals("POST"))
			connection.setDoOutput(true);

		try {
			connection.setRequestMethod(httpMethodType);

		} catch (ProtocolException e) {
			Delegate.didFailWithError(e.getMessage(), state);
			return;
		}

		if (headers != null)
			for (Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();

				connection.setRequestProperty(key, value);
			}

		if (httpMethodType.equals("POST")) {
			OutputStream writer = null;
			try {
				writer = connection.getOutputStream();
			} catch (IOException e) {
				Delegate.didFailWithError(e.getMessage(), state);
				return;

			}

			byte[] output_buffer = new byte[128];
			int readBytesCount;
			try {

				while (((readBytesCount = outgoing_is.read(output_buffer, 0, output_buffer.length)) > 0) && running) {
					writer.write(output_buffer, 0, readBytesCount);
				}

			} catch (IOException e) {
				Delegate.didFailWithError(e.getMessage(), state);
				return;
			}

			if (outgoing_is != null) {
				try {
					outgoing_is.close();
				} catch (IOException e) {
					Delegate.didFailWithError(e.getMessage(), state);
					return;
				}
			}
		}

		try {
			connection.connect();
		} catch (IOException e) {
			Delegate.didFailWithError(e.getMessage(), state);
			return;
		}

		int responseCode = 0;

		try {
			responseCode = connection.getResponseCode();
		} catch (IOException e) {
			Delegate.didFailWithError(e.getMessage(), state);
			return;
		}

		if (responseCode != HTTP_OK && running) {
			if (running)
				Delegate.didFailWithError("HTTP NOT OK : " + String.valueOf(responseCode), state);

			return;
		} else {
			InputStream incoming_is = null;
			try {
				incoming_is = connection.getInputStream();
			} catch (IOException e) {
				Delegate.didFailWithError(e.getMessage(), state);
				return;
			}

			outputStream = new ByteArrayOutputStream();

			byte[] buffer = new byte[128];
			int read = 0;

			try {
				while (((read = incoming_is.read(buffer, 0, 128)) > 0) && running) {
					outputStream.write(buffer, 0, read);
				}
			} catch (IOException e) {
				Delegate.didFailWithError(e.getMessage(), state);
				return;
			}

			try {
				incoming_is.close();
			} catch (IOException e) {
				Delegate.didFailWithError(e.getMessage(), state);
				return;
			}

			if (running)
				Delegate.didConnectionResult(outputStream.toByteArray(), state, tag);
		}

	}

}
