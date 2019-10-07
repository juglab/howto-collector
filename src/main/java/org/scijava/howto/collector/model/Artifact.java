package org.scijava.howto.collector.model;

public class Artifact {
	private String version;
	private long availableArtifactId;

	public Artifact(String version, long availableArtifactId) {
		this.version = version;
		this.availableArtifactId = availableArtifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getAvailableArtifactId() {
		return availableArtifactId;
	}

	public void setAvailableArtifactId(long availableArtifactId) {
		this.availableArtifactId = availableArtifactId;
	}
}
