package org.ku.nicheanalyst.dataset;

import org.ioz.niche.breadth.JorgeNicheDefination;


public class NicheBreadthParameters {
	private GompertzCurveParameters[] variableParameters;
	private String[] variableFolders;
	private EllipsoidParameters ellipsoids;
	private int duration;
	private double migrationAbility;
	private int migrationInterval;
	private int initialRandomSpeciesSeeds;
	private String initialManualSpeciesSeeds;
	private String information;
	private int initialType;//0: random 1: manual
	private boolean is3D;
	private JorgeNicheDefination niche;
	public NicheBreadthParameters(
			GompertzCurveParameters[] variableParameters, 
			String[] variableFolders,
			EllipsoidParameters ellipsoids,
			int duration,
			double migrationAbility,
			int initialRandomSpeciesSeeds, 
			String initialManualSpeciesSeeds,
			String information,
			int initialType){
		this.variableParameters = variableParameters;
		this.variableFolders = variableFolders;
		this.ellipsoids = ellipsoids;
		this.duration = duration;
		this.migrationAbility = migrationAbility;
		this.initialRandomSpeciesSeeds = initialRandomSpeciesSeeds;
		this.initialManualSpeciesSeeds = initialManualSpeciesSeeds;
		this.initialType = initialType;
		this.information = information;
		this.is3D = (variableFolders.length==3);
	}
	public NicheBreadthParameters(
			GompertzCurveParameters[] variableParameters,
			String[] variableFolders, JorgeNicheDefination niche, int duration,
			int migrationAbility, int migrationInterval) {
		this.variableParameters = variableParameters;
		this.variableFolders = variableFolders;
		this.migrationInterval = migrationInterval;
		this.niche = niche;
		this.duration = duration;
		this.migrationAbility = migrationAbility;
	}
	public GompertzCurveParameters[] getVariableParameters() {
		return variableParameters;
	}
	public String[] getVariableFolders() {
		return variableFolders;
	}
	public EllipsoidParameters getEllipsoids() {
		return ellipsoids;
	}
	public int getDuration() {
		return duration;
	}
	public double getMigrationAbility() {
		return migrationAbility;
	}
	public String getInformation(){
		return information;
	}
	public int getInitialRandomSpeciesSeeds() {
		return initialRandomSpeciesSeeds;
	}
	public String getInitialManualSpeciesSeeds() {
		return initialManualSpeciesSeeds;
	}
	public int getInitialType() {
		return initialType;
	}
	public boolean is3D(){
		return is3D;
	}
	public int getMigrationInterval() {
		return migrationInterval;
	}
	public boolean isIs3D() {
		return is3D;
	}
	public JorgeNicheDefination getNiche() {
		return niche;
	}
	
}
