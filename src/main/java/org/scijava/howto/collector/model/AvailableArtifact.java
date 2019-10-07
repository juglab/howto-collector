package org.scijava.howto.collector.model;

public class AvailableArtifact {
	private String groupId;
	private String artifactId;

	public AvailableArtifact(String parentId, String artifactId) {
		this.groupId = parentId;
		this.artifactId = artifactId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
}
