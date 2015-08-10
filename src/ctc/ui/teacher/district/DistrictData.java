package ctc.ui.teacher.district;

import java.util.ArrayList;
import java.util.List;
import ctc.pojobean.*;

public class DistrictData {
	
	private static DistrictData thisData = null;
	public static DistrictData getInstance(){
		if (thisData == null){
			thisData = new DistrictData();
		}
		return thisData;
	}
	public DistrictData(){
		super();
	}
	
	 public static boolean reloadFlag = true; 
	 static boolean startFlag = true;
	 static boolean endFlag = true;
	 static boolean searchEndFlag  = true;
	 static boolean searchStartFlag  = true;
	 static boolean editEndFlag  = false;
	 static boolean editStartFlag  = false;
	 
	 public static void initial(){ 
	   	reloadFlag = true;
	   	
	   	startFlag = true;
	   	endFlag = true;
	   	
	   	searchStartFlag  = true;
	   	searchEndFlag  = true;
	   	
	   	editStartFlag  = true;
	   	editEndFlag  = true;
	 }
	
	
	static String [] bureau = {"北京","郑州"};//铁路局名称
	static String [] stationName;//车站名称
	
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
	
	//以下为浏览用
	int PAGE_SIZE = 16;
	final static int PAGESIZE = 16;//表格上的每页所显示的记录条数
	public static String[] columnHeads = {"区段开始站","区段结束站","区段站数量","区段所属铁路局"};
	private int[] columnWidths = {10,10,10,15};
	
	//保存检索结果的集合类对象
	private ArrayList<District> districtList = new ArrayList<District>();
	
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
	
	public void removeAll(){
		 districtList.clear();//全部删除集合中的元素
	}

	//根据SQL语句在数据库中查询数据，并将获取的数据赋值个ArrayList
	public void setData(List<District> list){
		districtList = (ArrayList<District>)list;
	}
	//保证首次装入tableviewer的数据
	public ArrayList<District> getData() {
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
		
		ArrayList<District> list = new ArrayList<District>();
		
		for(int i = 0; i< PAGE_SIZE; i++)
			list.add(districtList.get(i));
		
		return list;
	}

	public void remove(String districtName){
		for(int i = 0; i < districtList.size(); i++)
		{
			District data = districtList.get(i);
			if(districtName.equalsIgnoreCase(data.getDistrict_name()))
				districtList.remove(i); 
		}
	}
	public ArrayList<District> getRow(int low,int high){
		ArrayList<District> list = new ArrayList<District>();
		for(int i = low; i< high; i++)
			list.add(districtList.get(i));
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
			// 凑整取整数
			totalPage = (int) Math.ceil(temp);
		}
		if (totalPage == 0) {
			totalPage = 1;
		}
		return totalPage;
	}

}