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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
		this.running = true;

		try {
			// Je gère les problèmes d'encodage pouvant survenir dans le nom des fichiers demandés (ticket #50)
			String URLarticle = _url.substring(_url.lastIndexOf("/") + 1, _url.length());
			String URLreste = _url.substring(0, _url.lastIndexOf("/") + 1);
			
			String monURL = URLreste + URLEncoder.encode(URLarticle, "UTF-8");
			
			URL url = new URL(monURL);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("content-type", "UTF-8");
			
			if (httpMethodType.equals("POST"))
				connection.setDoOutput(true);

			connection.setRequestMethod(httpMethodType);

			if (headers != null)
				for (Entry<String, String> entry : headers.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();

					connection.setRequestProperty(key, value);
				}

			if (httpMethodType.equals("POST")) {
				OutputStream writer = connection.getOutputStream();

				byte[] output_buffer = new byte[128];
				int readBytesCount;

				while (((readBytesCount = outgoing_is.read(output_buffer, 0, output_buffer.length)) > 0) && running) {
					writer.write(output_buffer, 0, readBytesCount);
				}

				if (outgoing_is != null) {
					outgoing_is.close();
				}
			}

			connection.connect();

			int responseCode = 0;

			responseCode = connection.getResponseCode();

			if (responseCode != HTTP_OK && running) {
				if (running)
					Delegate.didFailWithError("HTTP NOT OK : " + String.valueOf(responseCode), state);

				return;
			} else {
				InputStream incoming_is = null;
				incoming_is = connection.getInputStream();

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

				byte[] buffer = new byte[128];
				int read = 0;

				while (((read = incoming_is.read(buffer, 0, 128)) > 0) && running) {
					outputStream.write(buffer, 0, read);
				}

				incoming_is.close();

				if (running)
					Delegate.didConnectionResult(outputStream.toByteArray(), state, tag);
			}
		} catch (Exception e) {
			Delegate.didFailWithError(e.getMessage(), state);
			return;
		}

	}

}
