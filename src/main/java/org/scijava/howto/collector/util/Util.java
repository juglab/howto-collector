package org.scijava.howto.collector.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Util {

	public static JSONObject getJSONObject(URL url) throws IOException {
		String inline = getJSONString(url);
		if (inline == null) return null;
		return new JSONObject(inline);
	}

	public static JSONArray getJSONArray(URL url) throws IOException {
		String inline = getJSONString(url);
		if (inline == null) return null;
		return new JSONArray(inline);
	}

	private static String getJSONString(URL url) throws IOException {
		//		System.out.println(url);
		String inline = "";
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();
		int responsecode = conn.getResponseCode();
		if(responsecode == 404) return null;
		if(responsecode != 200)
			throw new RuntimeException("HttpResponseCode: " +responsecode);
		else {
			Scanner sc = new Scanner(url.openStream());
			while(sc.hasNext()) {
				inline+=sc.nextLine();
			}
//			System.out.println("\nJSON Response in String format");
//			System.out.println(inline);
			sc.close();
		}
		conn.disconnect();
		return inline;
	}

}
