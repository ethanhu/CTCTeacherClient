package ctc.ui.teacher.districtstation;

import java.util.ArrayList;
import java.util.List;
import ctc.pojobean.*;

public class DistrictStationData {
	
	public DistrictStationData(){
		super();
	}
	
	private static DistrictStationData thisData = null; 
	public static DistrictStationData getInstance(){
		if (thisData == null){
			thisData = new DistrictStationData();
		}
		return thisData;
	}
	
	static String [] stationName;//车站名称
	static String [] districtName;//区段名称
	
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
	
	public void setStationList(List<Station> list) {
		
		stationName = new String[list.size()];

		if (list == null)
			return;
		
		for(int i = 0; i < list.size(); i++)
		{
			Station data = list.get(i);
			stationName[i] = data.getStation_name();
		}
	}
	
	public static String[] columnHeads = {"区段名称","前站站名","本站站名","站间距离"};
	
	private int[] columnWidths = {25,15,15,10};
	
	public int[] getColumnWidths() {
		return columnWidths;
	}
	public String getColumnHead(int i){
		return getColumnHeads()[i];
	}
	public String[] getColumnHeads() {
		return columnHeads;
	}
	public int getColumnWidth(int i){
		return getColumnWidths()[i];
	}
	public int getColumnCount(){
		return columnHeads.length;
	}
	
	public static boolean reloadFlag = true; 
	static boolean districtFlag = true;
	public static boolean firstStationFlag = false;
	public static boolean secondStationFlag = false;
	
	static boolean searchFlag  = true;
	
	public static void initial(){ 
	   	
		reloadFlag = true;
	   	districtFlag = true;
	   	searchFlag  = true;
	 }
	/////////////////////////////////////////////////
	int PAGE_SIZE = 15;
	final static int PAGESIZE = 15;//表格上的每页所显示的记录条数
	//保存检索结果的集合类对象
	static ArrayList<StationDistrictRelation> districtList = new ArrayList<StationDistrictRelation>();
	
	//根据SQL语句在数据库中查询数据，并将获取的数据赋值个ArrayList
	public void setData(List<StationDistrictRelation> list){
		districtList = (ArrayList<StationDistrictRelation>)list;
	}
	public ArrayList<StationDistrictRelation> getData() {
		if(districtList == null)
			return null;
	
		int count = districtList.size();
		
		if( count == 0)
			return null;
		else
		if( count < PAGE_SIZE)
			PAGE_SIZE = count;
		else
		if( count >= PAGE_SIZE && count <= PAGESIZE){
			PAGE_SIZE = count;
		}else
		if( count > PAGESIZE){
			PAGE_SIZE = PAGESIZE;
		}
		
		ArrayList<StationDistrictRelation> list = new ArrayList<StationDistrictRelation>();
		
		//System.out.println("count="+count+":"+PAGE_SIZE);
		for(int i = 0; i< PAGE_SIZE; i++){
			list.add(districtList.get(i));
		}
		return list;
	}
	
	public ArrayList<StationDistrictRelation> getRow(int low,int high){
		ArrayList<StationDistrictRelation> list = new ArrayList<StationDistrictRelation>();
		for(int i = low; i< high; i++)
			list.add(districtList.get(i));
		//System.out.println("low:"+low+":high:"+high+":"+list.size());
		return list;
	}
	
	public int getRowCount(){//检索结果的总数
		return districtList.size();	
	}
	//在表格控件上显示所有检索结果所需的总页数
	public int getTotalPageNum() {
		int totalPage = 1;// 检索结果显示的总页数
		if (PAGE_SIZE != 0) {
			double temp = (float) getRowCount() / (float) PAGE_SIZE;
			//凑整取整数
			totalPage = (int) Math.ceil(temp);
		}
		if (totalPage == 0) {
			totalPage = 1;
		}
		return totalPage;
	}

	public void removeAll(){
		 districtList.clear();//全部删除集合中的元素
	}

	public void remove(String stationName){
		for(int i = 0; i < districtList.size(); i++)
		{
			StationDistrictRelation data = districtList.get(i);
			if(stationName.equalsIgnoreCase(data.getStation_name()))
				districtList.remove(i); 
		}
	}

}
