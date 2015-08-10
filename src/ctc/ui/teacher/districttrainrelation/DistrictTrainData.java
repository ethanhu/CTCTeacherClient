package ctc.ui.teacher.districttrainrelation;

import java.util.ArrayList;
import java.util.List;
import ctc.pojobean.*;

public class DistrictTrainData {
	
	public DistrictTrainData(){
		super();
	}
	
	private static DistrictTrainData thisData = null; 
	public static DistrictTrainData getInstance(){
		if (thisData == null){
			thisData = new DistrictTrainData();
		}
		return thisData;
	}
	
	static String [] trainName;//车站名称
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
	public void setTrainList(List<Train> list) {
		trainName = new String[list.size()];

		if (list == null)
			return;
		
		for(int i = 0; i < list.size(); i++)
		{
			Train data = list.get(i);
			trainName[i] = data.getTrain_name();
		}
	}
	public static String[] columnHeads = {"区段名称","车站名称"};
	private int[] columnWidths = {23,17};
	
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
	public static boolean districtFlag = true;
	public static boolean stationFlag = false;
	public static boolean searchFlag  = true;
	
	public static void initial(){ 
	   	reloadFlag = true;
	   	districtFlag = true;
	   	searchFlag  = true;
	 }
	/////////////////////////////////////////////////
	int PAGE_SIZE = 15;
	final static int PAGESIZE = 15;//表格上的每页所显示的记录条数
	//保存检索结果的集合类对象
	static ArrayList<TrainDistrictRelation> districtList = new ArrayList<TrainDistrictRelation>();
	
	//根据SQL语句在数据库中查询数据，并将获取的数据赋值个ArrayList
	public void setData(List<TrainDistrictRelation> list){
		districtList = (ArrayList<TrainDistrictRelation>)list;
	}
	
	public ArrayList<TrainDistrictRelation> getData() {
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
		
		ArrayList<TrainDistrictRelation> list = new ArrayList<TrainDistrictRelation>();
		
		//System.out.println("count="+count+":"+PAGE_SIZE);
		for(int i = 0; i< PAGE_SIZE; i++){
			list.add(districtList.get(i));
		}
		return list;
	}
	
	public ArrayList<TrainDistrictRelation> getRow(int low,int high){
		ArrayList<TrainDistrictRelation> list = new ArrayList<TrainDistrictRelation>();
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

	public void remove(String trainName){
		for(int i = 0; i < districtList.size(); i++)
		{
			TrainDistrictRelation data = districtList.get(i);
			if(trainName.equalsIgnoreCase(data.getTrain_name()))
				districtList.remove(i); 
		}
	}

}
