package ctc.ui.teacher.trainPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctc.pojobean.*;

public class TrainPlanData {
	
	public TrainPlanData(){
		super();
	}
	
	private static TrainPlanData thisData = null; 
	public static TrainPlanData getInstance(){
		if (thisData == null){
			thisData = new TrainPlanData();
		}
		return thisData;
	}
	
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
	
	public static String [] trainName;//车次名称
	public static Map<String,TrainStartEndStation> trainStartEndStatioIDMap;//车次名称和车次始到达站关系
	
	public void setTrainList(List<Train> list) {
		trainName = new String[list.size()];
		trainStartEndStatioIDMap = new HashMap<String,TrainStartEndStation>();

		if (list == null)
			return;
		
		for(int i = 0; i < list.size(); i++)
		{
			Train data = list.get(i);
			trainName[i] = data.getTrain_name();
			trainStartEndStatioIDMap.put(data.getTrain_name(), 
					            new TrainStartEndStation(data.getTrain_startstationname(),data.getTrain_endstationname()));
		}
	}
	
	//保存与某trainID相关的信息
	static String [] stationName;//车站名称
	
	//保存所有信息
	static String [] stationNameAll;//车站名称
	
	
	public void setStationList(boolean flag, List<Station> list) {

		if(flag)
			stationNameAll = new String[list.size()];
		else
			stationName = new String[list.size()];

		if (list == null)
			return;

		for(int i = 0; i < list.size(); i++)
		{
			Station data = list.get(i);
			if(flag){	
				stationNameAll[i] = data.getStation_name();
			}
			else{
				stationName[i] = data.getStation_name();
			}
		}
	}
	
	
	public static boolean firstStationFlag = false;
	public static boolean secondStationFlag = false;
	
	
	public static boolean trainFlag = true;
	static boolean searchFlag  = true;
	
	/////////////////////////////////////////////
	static boolean districtFlag = true;
	public static boolean reloadFlag = true;
	
	public static void initial(){ 
	   	reloadFlag = true;
	   	trainFlag = true;
	   	searchFlag  = true;
	   	districtFlag = true;
	}
//////////////////////
	public static String[] columnHeads = {"区段","车次","前站站名","本站站名","到站时间","离站时间"};
	private int[] columnWidths = {15,10,10,10,9,9};
	
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
	
	 
//	static boolean districtFlag = true;

	/////////////////////////////////////////////////
	int PAGE_SIZE = 15;
	final static int PAGESIZE = 15;//表格上的每页所显示的记录条数
	
	//保存检索结果的集合类对象
	static ArrayList<Plan> planList = new ArrayList<Plan>();
	//根据SQL语句在数据库中查询数据，并将获取的数据赋值个ArrayList
	public void setData(List<Plan> list){
		planList = (ArrayList<Plan>)list;
	}
	
	public ArrayList<Plan> getData() {
		if(planList == null)
			return null;
	
		int count = planList.size();
		
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
		
		ArrayList<Plan> list = new ArrayList<Plan>();
		
		//System.out.println("count="+count+":"+PAGE_SIZE);
		for(int i = 0; i< PAGE_SIZE; i++){
			list.add(planList.get(i));
		}
		return list;
	}
	
	public ArrayList<Plan> getRow(int low,int high){
		ArrayList<Plan> list = new ArrayList<Plan>();
		for(int i = low; i< high; i++)
			list.add(planList.get(i));
		//System.out.println("low:"+low+":high:"+high+":"+list.size());
		return list;
	}
	
	public int getRowCount(){//检索结果的总数
		return planList.size();	
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
		 planList.clear();//全部删除集合中的元素
	}

	public void remove(String trainID,String stationID){
		for(int i = 0; i < planList.size(); i++)
		{
			Plan data = planList.get(i);
			if(trainID.equalsIgnoreCase(data.getTrain_name()) && stationID.equalsIgnoreCase(data.getStation_name()) )
				planList.remove(i); 
		}
	}
}

class TrainStartEndStation{
	private String Train_startstationid;
	private String Train_endstationid;
	
	public TrainStartEndStation(String train_startstationid,
			String train_endstationid) {
		super();
		Train_startstationid = train_startstationid;
		Train_endstationid = train_endstationid;
	}
	public String getTrain_startstationid() {
		return Train_startstationid;
	}
	public void setTrain_startstationid(String train_startstationid) {
		Train_startstationid = train_startstationid;
	}
	public String getTrain_endstationid() {
		return Train_endstationid;
	}
	public void setTrain_endstationid(String train_endstationid) {
		Train_endstationid = train_endstationid;
	}
	
	

}