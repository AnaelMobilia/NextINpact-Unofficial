package com.nextinpact.managers;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.nextinpact.models.INPactComment;
import com.nextinpact.parsers.HtmlParser;

public class CommentManager {
	public static List<INPactComment> getCommentsFromFile(Context context,
			String path) {
		List<INPactComment> comments = null;
		try {
			FileInputStream l_Stream = context.openFileInput(path);
			HtmlParser hh = new HtmlParser(l_Stream);
			comments = hh.getComments();
			l_Stream.close();
		} catch (FileNotFoundException e) {
			Log.e("WTF", "" + e.getMessage());
		} catch (IOException e) {
			Log.e("WTF", "" + e.getMessage());
		}

		catch (Exception e) {
			Log.e("WTF", "" + e.getMessage());
		}

		if (comments == null)
			comments = new ArrayList<INPactComment>();

		return comments;
	}

	public static void saveComments(Context context, byte[] result, String tag) {

		try {
			FileOutputStream l_Stream = context.openFileOutput(tag
					+ "_comms.html", Context.MODE_PRIVATE);
			l_Stream.write(result);
		} catch (Exception e) {
			Log.e("WTFException", "" + e.getMessage());
		}

	}

	public static List<INPactComment> getCommentsFromBytes(byte[] result) {

		InputStream is = null;
		try {
			is = new ByteArrayInputStream(result);
		}

		catch (Exception e) {
			return new ArrayList<INPactComment>();
		}

		HtmlParser parser = null;
		try {
			parser = new HtmlParser(is);
		} catch (IOException e) {
			return new ArrayList<INPactComment>();
		}

		return parser.getComments();

	}
}
