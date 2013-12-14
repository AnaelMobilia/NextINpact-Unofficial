package com.pcinpact;

import android.content.Context;

import com.pcinpact.managers.ArticleManager;
import com.pcinpact.models.ArticlesWrapper;

public class PCInpact
{
	public static boolean DL_COMMENTS =true;
	public static int THEME =R.style.MyTheme;
	//public static int THEME =R.style.Theme_Sherlock_Light;	
	//public static int THEME =R.style.Theme_Sherlock_Light_DarkActionBar;
	public final static String PC_INPACT_URL ="http://m.pcinpact.com";
	//public final static String PC_INPACT_URL ="http://ikoula.nomalys.com/pcinpact/";
	
	static private PCInpact instance;
	
	ArticlesWrapper wrapper;
	Context context;
	
	private PCInpact (Context context)
	{
		this.context = context;
	}
	
	static public PCInpact getInstance(Context context)
	{
		if( instance==null || instance.context ==null)
			instance = new PCInpact(context);
		
		instance.context =context;
		return instance;
	}
	
	public ArticlesWrapper getArticlesWrapper()
	{
		if( wrapper==null)
			wrapper = ArticleManager.getSavedArticlesWrapper(context);
		
		if(wrapper==null)
			wrapper=new ArticlesWrapper();
	
		return wrapper;
	}
	

	
}
