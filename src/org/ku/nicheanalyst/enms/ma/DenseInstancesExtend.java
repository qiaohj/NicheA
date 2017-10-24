package org.ku.nicheanalyst.enms.ma;

import java.util.ArrayList;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.InterquartileRange;

public class DenseInstancesExtend {
	private ArrayList<DenseInstanceExtend> denseInstancesExtend;
	public DenseInstancesExtend(){
		denseInstancesExtend = new ArrayList<DenseInstanceExtend>();
	}
	public int removeNoise(Instances oinstances) throws Exception{
		Attribute attribute = new Attribute("distance"); 
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(attribute);
		Instances instances = new Instances("distance", attributes, denseInstancesExtend.size());
		double avgValue = 0;
		for (DenseInstanceExtend instance : denseInstancesExtend){
			DenseInstance dinstance = new DenseInstance(1);
			dinstance.setDataset(instances);
			double varValue = instance.getAvgDistance();
			dinstance.setValue(attribute, varValue);
			avgValue += varValue;
			instances.add(dinstance);
		}
		avgValue = avgValue / instances.size();
//		CommonFun.SaveArffToFile("./test.arff", instances);
		InterquartileRange filter = new InterquartileRange();
		filter.setInputFormat(instances);
		filter.setAttributeIndices("first");
		filter.setOutlierFactor(1.349);
		filter.setExtremeValuesFactor(Integer.MAX_VALUE);
		
		//filter.setInputFormat(instances);
        Instances filteredInstances = Filter.useFilter(instances, filter);
//        CommonFun.SaveArffToFile("./test_filter.arff", filteredInstances);
        int i=0;
        int movedCount = 0;
        ArrayList<DenseInstanceExtend> removedinstances = new ArrayList<DenseInstanceExtend>();
        ArrayList<Instance> removedoinstances = new ArrayList<Instance>();
        for (Instance instance : filteredInstances){
        	if ((instance.value(1)>0)&&(instance.value(0)>avgValue)){
//        		System.out.println("No." + i + " is removed.");
        		
        		removedinstances.add(denseInstancesExtend.get(i));
        		removedoinstances.add(oinstances.get(i));
        		//denseInstancesExtend.remove(i);
        		//oinstances.remove(i);
        		movedCount++;
        	}
        	i++;
        }
        for (int j=0;j<removedinstances.size();j++){
        	denseInstancesExtend.remove(removedinstances.get(j));
        	oinstances.remove(removedoinstances.get(j));
        }
        return movedCount;
		
	}
	public void addDenseInstanceExtend(DenseInstanceExtend denseInstanceExtend){
		denseInstancesExtend.add(denseInstanceExtend);
	}
	
	public DenseInstanceExtend getDenseInstanceExtend(int index){
		return denseInstancesExtend.get(index);
	}
	public double getEpsilon(){
		double epsilon = -1;
		for (int i=0;i<denseInstancesExtend.size();i++){
			if (denseInstancesExtend.get(i).getMinDistance()>0){
				if (epsilon<denseInstancesExtend.get(i).getMinDistance()){
					epsilon = denseInstancesExtend.get(i).getMinDistance();
				}
			}
		}
		return epsilon;
	}
	
	
	public ArrayList<DenseInstanceExtend> getDenseInstancesExtend() {
		return denseInstancesExtend;
	}
	
}
