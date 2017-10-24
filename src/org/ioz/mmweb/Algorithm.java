package org.ioz.mmweb;

import java.util.HashSet;


public class Algorithm {
	
	private String id;
	private String algorithmid;
	private String name;
	private String description;
	private int sequence=0;
	private HashSet<AlgorithmParameter> parameters;
	private String version;
	private String overview;
	private String algorithmauthor;
	private String bibliography;
	private String codeauthor;
	private String codeauthorscontact;
	private String acceptcategoricaldata;
	private String needpseudoabsencepoints;
	private boolean available;
	private int type;
	private int ispseudoN;
	
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAlgorithmid() {
		return algorithmid;
	}
	public void setAlgorithmid(String algorithmid) {
		this.algorithmid = algorithmid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public HashSet<AlgorithmParameter> getParameters() {
		return parameters;
	}
	public void setParameters(HashSet<AlgorithmParameter> parameters) {
		this.parameters = parameters;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	public String getAlgorithmauthor() {
		return algorithmauthor;
	}
	public void setAlgorithmauthor(String algorithmauthor) {
		this.algorithmauthor = algorithmauthor;
	}
	public String getBibliography() {
		return bibliography;
	}
	public void setBibliography(String bibliography) {
		this.bibliography = bibliography;
	}
	public String getCodeauthor() {
		return codeauthor;
	}
	public void setCodeauthor(String codeauthor) {
		this.codeauthor = codeauthor;
	}
	public String getCodeauthorscontact() {
		return codeauthorscontact;
	}
	public void setCodeauthorscontact(String codeauthorscontact) {
		this.codeauthorscontact = codeauthorscontact;
	}
	public String getAcceptcategoricaldata() {
		return acceptcategoricaldata;
	}
	public void setAcceptcategoricaldata(String acceptcategoricaldata) {
		this.acceptcategoricaldata = acceptcategoricaldata;
	}
	public String getNeedpseudoabsencepoints() {
		return needpseudoabsencepoints;
	}
	public void setNeedpseudoabsencepoints(String needpseudoabsencepoints) {
		this.needpseudoabsencepoints = needpseudoabsencepoints;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getIspseudoN() {
		return ispseudoN;
	}
	public void setIspseudoN(int ispseudoN) {
		this.ispseudoN = ispseudoN;
	}
	
	
	
	
	
}
