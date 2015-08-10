package ctc.tdcs.command;

import ctc.constant.Constants;
import ctc.databaseserver.DatabaseServer;

public class TeamMemberData {
	
	public TeamMemberData(){
		super();
	}
	
	private static TeamMemberData thisData = null; 
	public static TeamMemberData getInstance(){
		if (thisData == null){
			thisData = new TeamMemberData();
		}
		return thisData;
	}
	
	private DatabaseServer databaseServer = DatabaseServer.getInstance();
	
	//发送调度命令
	public void SendScheduleCommand(int teamID,String name,String content)
	{
		int CommandType = Constants.TDCS_SCHEDULE_COMMAND;//向服务器发送调度命令
		databaseServer.sendScheduleORErrorCommandToServer(CommandType,teamID,name,content);
	}
	
	//发送故障命令
	public void SendErrorCommand(int teamID,String name,String content)
	{
		int CommandType = Constants.TDCS_ERROR_COMMAND;//向服务器发送故障命令
		databaseServer.sendScheduleORErrorCommandToServer(CommandType,teamID,name,content);
	}
	
	//从服务器获取组数据，并保存在有关变量中
	public void getTeamsInfo()
	{
		int CommandType = Constants.TDCS_TEAM_NAME;//从服务器获取组的名称
		setTeamNamesList(databaseServer.getTeamORMemberNameFromServer(CommandType,"-1"));
	}
	
	//从服务器获取组内成员名称，并保存在有关变量中
	public void getTeamMembersInfo(String teamID)
	{
		int CommandType = Constants.TDCS_TEAM_MEMBER_NAME;//从服务器获取组内成员的名称
		setTeamMembersList(databaseServer.getTeamORMemberNameFromServer(CommandType,teamID));
	}
	/////////////////////////////////////////////////////////////////////
	static String [] teamItems;//组名
	static String [] teamMemberItems;//组内成员的名称
	
	
	public void setTeamNamesList(String[] listStr) {
		teamItems = listStr;
	}
	
	public void setTeamMembersList(String[] listStr) {
		teamMemberItems = listStr;
	
	}
	
	public static boolean teamFlag = false;
	
}
