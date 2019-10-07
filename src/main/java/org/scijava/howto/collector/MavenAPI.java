package org.scijava.howto.collector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scijava.howto.collector.model.AvailableArtifact;
import org.scijava.howto.collector.util.Util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MavenAPI {

	public static String mavenURL = "https://dais-maven.mpi-cbg.de/";
	public static String mavenRepository = "test-maven";

	static String getRecentVersion(AvailableArtifact artifact) {
		String apiCall = mavenURL + "service/rest/v1/search?repository=" + mavenRepository
				+ "&group=" + artifact.getGroupId() + "&name=" + artifact.getArtifactId();
		try {
			JSONArray versions = Util.getJSONObject(new URL(apiCall)).getJSONArray("items");
			if(versions.length() == 0) return null;
			JSONObject recentComponent = versions.getJSONObject(versions.length() - 1);
			return recentComponent.getString("version");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URL getJAR(AvailableArtifact artifact, String recentVersion) throws MalformedURLException {
		String apiCall = mavenURL + "service/rest/v1/search/assets/download?maven.extension=jar&maven.classifier&repository="
				+ mavenRepository + "&group=" + artifact.getGroupId() + "&name=" + artifact.getArtifactId()
				+ "&version=" + recentVersion;
		return new URL(apiCall);
	}
}
