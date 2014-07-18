package com.nextinpact;

import android.content.Context;

import com.nextinpact.managers.ArticleManager;
import com.nextinpact.models.ArticlesWrapper;

public class NextInpact {
	public static boolean DL_COMMENTS = true;
	public static int THEME = R.style.MyTheme;
	// public static int THEME =R.style.Theme_Sherlock_Light;
	// public static int THEME =R.style.Theme_Sherlock_Light_DarkActionBar;
	public final static String NEXT_INPACT_URL = "http://m.nextinpact.com";

	static private NextInpact instance;

	ArticlesWrapper wrapper;
	Context context;

	private NextInpact(Context context) {
		this.context = context;
	}

	static public NextInpact getInstance(Context context) {
		if (instance == null || instance.context == null)
			instance = new NextInpact(context);

		instance.context = context;
		return instance;
	}

	public ArticlesWrapper getArticlesWrapper() {
		if (wrapper == null)
			wrapper = ArticleManager.getSavedArticlesWrapper(context);

		if (wrapper == null)
			wrapper = new ArticlesWrapper();

		return wrapper;
	}

}
