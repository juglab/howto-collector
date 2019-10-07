package org.scijava.howto.collector;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.scijava.howto.collector.model.Artifact;
import org.scijava.howto.collector.model.AvailableArtifact;
import org.scijava.howto.collector.model.Howto;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Collector {

	private static String howtoInJar = "docs/howtos/index.html";

	public void collect() {
		getAvailableArtifacts().forEach(availableArtifact -> {
			// check if available artifact is in database
			Integer availableArtifactId = HowtoAPI.getAvailableArtifact(availableArtifact);
			if(availableArtifactId == null) {
				//write to database, get ID
				HowtoAPI.writeAvailableArtifact(availableArtifact);
				availableArtifactId = HowtoAPI.getAvailableArtifact(availableArtifact);
			}
			// get latest versioned artifact of available artifact from maven
			String recentVersion = MavenAPI.getRecentVersion(availableArtifact);

			if(recentVersion == null) {
				System.out.println("Could not find artifact " + availableArtifact.getGroupId() + ":" + availableArtifact.getArtifactId());
				return;
			}
			Artifact artifact = makeArtifact(availableArtifactId, recentVersion);
			//  check if versioned artifact is already in database
			Integer artifactId = HowtoAPI.getArtifact(artifact, availableArtifact);
			if(artifactId == null) {
				//write to database, get ID
				HowtoAPI.writeArtifact(artifact);
				artifactId = HowtoAPI.getArtifact(artifact, availableArtifact);
				// check if there are howtos
				List<Howto> howtos = getHowtos(artifactId, availableArtifact, recentVersion);
				// write to database
				howtos.forEach(HowtoAPI::writeHowto);
			} else {
				System.out.println("Artifact " + availableArtifact.getGroupId() + ":" + availableArtifact.getArtifactId() + " " + artifact.getVersion() + " already in database.");
			}
		});
	}

	private List<Howto> getHowtos(int artifactId, AvailableArtifact availableArtifact, String recentVersion) {
		Document document = getHowtoDocument(availableArtifact, recentVersion);
		Elements howtoElements = document.getElementsByClass("howto");
		List<Howto> res = new ArrayList<>();
		howtoElements.forEach(howto -> res.add(makeHowto(howto, artifactId)));
		return res;
	}

	private Howto makeHowto(Element element, int artifactId) {
		Elements h3 = element.getElementsByTag("h3");
		String title = h3.text();
		String content = element.html();
		content = content.replace(h3.outerHtml(), "");
		content = StringEscapeUtils.unescapeHtml4(content);
		return new Howto(artifactId, title, content);
	}

	private Document getHowtoDocument(AvailableArtifact availableArtifact, String recentVersion) {
		try {
			URL recentJAR = MavenAPI.getJAR(availableArtifact, recentVersion);
			File downloadDir = Files.createTempDirectory("howtos").toFile();
			String name = availableArtifact.getGroupId() + "-" + availableArtifact.getArtifactId() + "-" + recentVersion;
			File downloadFile = new File(downloadDir, name);
			InputStream in = recentJAR.openStream();
			Files.copy(in, downloadFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			JarFile jar = new JarFile(downloadFile);
			JarEntry resource = jar.getJarEntry(howtoInJar);
			if (resource == null) {
				System.out.println("No Howtos found in " + name);
			}
			try (InputStream stream = jar.getInputStream(resource)) {
				StringBuilder textBuilder = new StringBuilder();
				try (Reader reader = new BufferedReader(new InputStreamReader
						(stream, Charset.forName(StandardCharsets.UTF_8.name())))) {
					int c;
					while ((c = reader.read()) != -1) {
						textBuilder.append((char) c);
					}
				}
				Document doc = Jsoup.parse(textBuilder.toString());
				return doc;

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Artifact makeArtifact(Integer availableArtifactId, String recentVersion) {
		return new Artifact(recentVersion, availableArtifactId);
	}

	private List<AvailableArtifact> getAvailableArtifacts() {
		List<AvailableArtifact> res = new ArrayList<>();
		try {
			InputStream stream = getClass().getResourceAsStream("/availableArtifacts.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			List<String> artifacts = new ArrayList<>();
			String line;
			while ((line = reader.readLine()) != null) {
				artifacts.add(line);
			}
			reader.close();
			artifacts.forEach(artifact -> res.add(resolveArtifact(artifact)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	private AvailableArtifact resolveArtifact(String artifact) {
		String[] parts = artifact.split(":");
		assert(parts.length == 2);
		return new AvailableArtifact(parts[0], parts[1]);
	}

	public static void main(String...args) {
		new Collector().collect();
	}

}
