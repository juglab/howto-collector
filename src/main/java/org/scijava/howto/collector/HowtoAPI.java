package org.scijava.howto.collector;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.scijava.howto.collector.model.Artifact;
import org.scijava.howto.collector.model.AvailableArtifact;
import org.scijava.howto.collector.model.Howto;
import org.scijava.howto.collector.util.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HowtoAPI {

	private static String baseURL = "http://localhost:3000/api/";

	static Integer getAvailableArtifact(AvailableArtifact artifact) {
		try {
			URL url = new URL(baseURL + "sources/" + artifact.getGroupId() + "/" + artifact.getArtifactId());
			JSONObject artifactObj = Util.getJSONObject(url);
			return artifactObj == null ? null : (Integer) artifactObj.get("id");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static void writeAvailableArtifact(AvailableArtifact artifact) {
		try {
			final String POST_PARAMS = "{\n" +
					"    \"groupId\": \"" + artifact.getGroupId() + "\",\r\n" +
					"    \"artifactId\": \"" + artifact.getArtifactId() + "\",\r\n" +
					"    \"hasHowtos\": " + 0 + "\n}";

			URL obj = new URL(baseURL + "sources/");

			post(POST_PARAMS, obj);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	static Integer getArtifact(Artifact artifact, AvailableArtifact availableArtifact) {
		try {
			URL url = new URL(baseURL + availableArtifact.getGroupId() + "/" + availableArtifact.getArtifactId() + "/" + artifact.getVersion());
			JSONArray jsonArray = Util.getJSONArray(url);
			if(jsonArray.length() == 0) return null;
			JSONObject artifactObj = jsonArray.getJSONObject(0);
			return artifactObj == null ? null : (Integer) artifactObj.get("id");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static void writeArtifact(Artifact artifact) {
		try {
			final String POST_PARAMS = "{\n" +
					"    \"availableArtifact\": {\"id\": " + artifact.getAvailableArtifactId() + "},\r\n" +
					"    \"version\": \"" + artifact.getVersion() + "\"\n}";
			URL obj = new URL(baseURL);
			post(POST_PARAMS, obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void post(String POST_PARAMS, URL obj) throws IOException {
		HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();

		postConnection.setRequestMethod("POST");
		postConnection.setRequestProperty("Content-Type", "application/json");
		postConnection.setDoOutput(true);
		OutputStream os = postConnection.getOutputStream();
		os.write(POST_PARAMS.getBytes());
		os.flush();
		os.close();

		int responseCode = postConnection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == 200) { //success
		} else {
			System.out.println(responseCode);
			System.out.println(postConnection.getResponseMessage());
		}
	}

	public static void writeHowto(Howto howto) {
		try {
			final String POST_PARAMS = "{\n" +
					"    \"artifact\": {\"id\": " + howto.getArtifactId() + "},\r\n" +
					"    \"title\": \"" + howto.getTitle() + "\",\r\n" +
					"    \"content\": \"" + StringEscapeUtils.escapeJson(howto.getContent()) + "\",\r\n" +
					"    \"author\": \"" + howto.getAuthor() + "\"\n}";
			System.out.println(POST_PARAMS);
			URL obj = new URL(baseURL + "/howtos");
			post(POST_PARAMS, obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
