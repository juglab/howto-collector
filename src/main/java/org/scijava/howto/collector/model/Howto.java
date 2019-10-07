package org.scijava.howto.collector.model;

public class Howto {

	private int artifactId;
	private String title;
	private String content;

	private String author;

	public Howto(int artifactId, String title, String content) {
		this.artifactId = artifactId;
		this.title = title;
		this.content = content;
	}

	public int getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
