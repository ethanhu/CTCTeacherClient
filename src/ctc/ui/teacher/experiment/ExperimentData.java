package ctc.ui.teacher.experiment;


import java.util.List;

import ctc.pojobean.*;

public class ExperimentData {
	
	public ExperimentData(){
		super();
	}
	
	private static ExperimentData thisData = null; 
	public static ExperimentData getInstance(){
		if (thisData == null){
			thisData = new ExperimentData();
		}
		return thisData;
	}
	
	public static String [] districtName;//区段名称
	
	public void setDistrictList(List<District> list) {
		districtName = new String[list.size()];
		
		if (list == null)
			return;
		
		for(int i = 0; i < list.size(); i++)
		{
			District data = list.get(i);
			districtName[i] = data.getDistrict_name();
		}
	}
	
	
	public static boolean reloadFlag = true; 
	public static boolean districtFlag = true;
	
	public static void initial(){ 
	   	reloadFlag = true;
	   	districtFlag = true;
	 }
	
}
