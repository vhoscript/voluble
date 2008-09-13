package com.notehive.maven.repo;

/**
 * records differences between folders.
 * 
 * @author hansloedolff
 */
public class Difference {
	
	/** folder from repo 1 */
	private FolderInfo repoFolder1;
	/** folder from repo 2 */
	private FolderInfo repoFolder2;
	
	/** text description of discrepancy */
	private String discrepancy;

	public FolderInfo getRepoFolder1() {
		return repoFolder1;
	}

	public void setRepoFolder1(FolderInfo repoFolder1) {
		this.repoFolder1 = repoFolder1;
	}

	public FolderInfo getRepoFolder2() {
		return repoFolder2;
	}

	public void setRepoFolder2(FolderInfo repoFolder2) {
		this.repoFolder2 = repoFolder2;
	}

	public String getDiscrepancy() {
		return discrepancy;
	}

	public void setDiscrepancy(String discrepancy) {
		this.discrepancy = discrepancy;
	}

	public boolean hasVersion() {
		return (repoFolder1 != null && repoFolder1.hasVersion()) || 
			(repoFolder2 != null && repoFolder2.hasVersion());
	}

	public String[] getQualifyingNames() {
		if (repoFolder1 != null && repoFolder1.hasVersion()) {
			return repoFolder1.getQualifyingNames();
		}
		if (repoFolder2 != null && repoFolder2.hasVersion()) {
			return repoFolder2.getQualifyingNames();
		}
		throw new IllegalStateException("Neither folder has a version");
	}

	public String getName() {
		if (repoFolder1 != null) {
			return repoFolder1.getName();
		} else {
			return repoFolder2.getName();
		}
	}

}
