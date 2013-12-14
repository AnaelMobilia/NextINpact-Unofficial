package com.pcinpact.connection;

public interface IConnectable {
	
	
	public void didConnectionResult(byte[] result, int state, String tag);
	public void didFailWithError(String error, int state);
	public void setDownloadProgress(int i);
	public void setUploadProgress(int i); 
	
}
