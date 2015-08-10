package ctc.databaseserver;

import java.util.List;
import ctc.constant.Constants;
import ctc.pojobean.District;
import ctc.transport.*;
import ctc.transport.message.CommonMessage;
import ctc.transport.message.SQLRequestMessage;
import ctc.transport.message.ScheduleErrorMessage;
import ctc.transport.message.TDCSCommandMessage;
import ctc.transport.message.TeamTdcsRsbMessage;
import ctc.transport.message.TrainLineAnchorMessage;
import ctc.tdcs.data.BaseParam;
import ctc.ui.CTCTeacherMain;
import ctc.util.ErrorLog;
import ctc.util.JsonUtil;

public class DatabaseAccessHandler {
	
	private SynClientSupport synClientSupport; // 同步
	private static AsynClientSupport asynClientSupport; //异步
	
	BaseParam districtData = BaseParam.getInstance();
	
	private static DatabaseAccessHandler thisData = null;
	public static DatabaseAccessHandler getInstance(){
		if (thisData == null){
			thisData = new DatabaseAccessHandler();
		}
		return thisData;
	}
	
	public DatabaseAccessHandler()
	{
		MinaCommunicationHandler  minaCommunicationHandler = new MinaCommunicationHandler();
		synClientSupport = minaCommunicationHandler.getSynClientSupport();
		asynClientSupport = minaCommunicationHandler.getAsynClientSupport();
	}
	
	
	//hu 2010-11-3
//	SICS发送CommonMessage消息到ZNTDCS
	public static void sendCommonMessageToServer(CommonMessage sMsg) {
		
		sMsg.setCommandMode(Constants.MODE_CS_ASYN_CLIENT);// 通信模式 异步
	    if(asynClientSupport != null){
				ErrorLog.log("教师TDCS：发送CommonMessage信息到Server:");
				asynClientSupport.sendCommonMessageToServer(sMsg);// 异步通信
			/*
				ErrorLog.log("教师TDCS：发送TrainLineAnchorMessage信息到Server - start -");
				TrainLineAnchorMessage msg2 = new TrainLineAnchorMessage();	
				msg2.setCommandMode(Constants.TYPE_DDZR_TO_ZNTDCS_ASYN);
				msg2.setTeamID(0);
				
				String stationName = "车站一";
				String sucStationName = "车站二";
				msg2.setTimeType(Constants.TDCS_TIME_TYPE_LEAVE);
				msg2.setStationName(stationName);
				msg2.setPrestationName(sucStationName);
				msg2.setTrainDirection(1);
				msg2.setTrainName("T1111");
				asynClientSupport.sendTrainLineAnchorMessageToServer(msg2);
				
				ErrorLog.log("教师TDCS：发送TrainLineAnchorMessage信息到Server - end -");
			*/
		}else{
			ErrorLog.log("设置故障：--asynClientSupport == null--");
			CTCTeacherMain.minaClient.getAsynClientSupport().sendCommonMessageToServer(sMsg);			
			
		}
	}
	
	
	
	////////////////////////////////////////////////////////////////////////
	//获取组的名称或组内成员名称 同服务器之间的通信使用operatedName域， 按照Json格式
	public String[] getTeamORMemberNameFromServer(TDCSCommandMessage sMsg)
	{
		sMsg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);//客户端发送到发向服务器的同步消息
		sMsg.setUserRole(Constants.USER_ROLE_TEACHER);//用户角色是教师
		TDCSCommandMessage rMsg = synClientSupport.TDCSMessageSynSend(sMsg);//同步通信
		
		if(rMsg == null || rMsg.getOperatedName() == null) {
			return null;		
		}else{
			String[] listStr = JsonUtil.getStringArray4Json(rMsg.getOperatedName());
			return listStr;
		}
	}	
	//向服务器发送故障或调度命令
	public boolean sendScheduleORErrorCommandToServer(ScheduleErrorMessage sMsg){
		//AbstractMessage类中定义的字段
		sMsg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);//客户端发送到发向服务器的同步消息
		sMsg.setUserRole(Constants.USER_ROLE_TEACHER);//用户角色是教师
	
		synClientSupport.ScheduleORErrorCommandToServer(sMsg);
		
		return true;//主要是为了以后处理返回结果时用
	}
	
	
	//启动实验
	public TDCSCommandMessage TDCSRunCommandToServer(TDCSCommandMessage sMsg){
		//AbstractMessage类中定义的字段
		sMsg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);//客户端发送到发向服务器的同步消息
		sMsg.setUserRole(Constants.USER_ROLE_TEACHER);//用户角色是教师
	
		return synClientSupport.TDCSMessageSynSend(sMsg);
	}
	
	//发送修改后的车次信息到服务器
	public void TDCSCommandForTrainToServer(TeamTdcsRsbMessage sMsg)
	{
		sMsg.setCommandMode(Constants.MODE_CS_ASYN_CLIENT);//异步
		sMsg.setUserRole(Constants.USER_ROLE_ZNTDCS);//设置用户角色
		
		asynClientSupport.TDCSForTrainMessageAsynSend(sMsg);
	}
	
	//关闭实验
	public void TDCSCloseCommandToServer(TDCSCommandMessage sMsg)
	{
		sMsg.setCommandMode(Constants.MODE_CS_ASYN_CLIENT);//异步
		sMsg.setUserRole(Constants.USER_ROLE_TEACHER);//设置用户角色
		
		asynClientSupport.TDCSMessageAsynSend(sMsg);
	}
	
   /////////////////////////////////////////////////////////////////////////////
	//无参数时，参数设为小写的"null";
	public boolean updateQuery(String tableName,String[] sqlArray){
		
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLBATCHINSERTDEELETE);//批量删除和批量插入
		msg.setDataBean(tableName);//要操作的表的名称
		
		//插入sql
		String sql = JsonUtil.array2json(sqlArray);
		msg.setSql(sql);
		String paprams = "null";//表示SQL语句的参数是空
		msg.setParams(paprams);
		
		String result = synClientSupport.sqlMessageSend(msg);//同步通信
		if(result == null){
			return false;		
		}else{
			return true;	 
		}
	}
	
//////////////////////////////////////////////////////////////////////////////
	
	/*功能：依据SQL语句从数据库获取相关信息，不带参数*/
   //定义泛型方法，有一个形式参数用类型参数T来定义
	public <T extends Object> List<T> sqlQuery(T t,String tableName,String sqlStr){
		
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY); 

		msg.setDataBean(tableName);
		msg.setSql(sqlStr);
		//不带参数的sql语句的使用方法
		String paprams = "null";
		msg.setParams(paprams);//转换为json字符串进行传递

		String resultListStr = synClientSupport.sqlMessageSend(msg);//同步通信
		
		if(resultListStr == null){//此情况一般不会出现
			return null;		
		}else{
			List<T> list = JsonUtil.getList4Json(resultListStr,t.getClass());
			return list;
		}
	}
	//带参数params的sql语句
	public <T extends Object> List<T> sqlQuery(T t,String tableName,String sqlStr,Object[] params){
		
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		
		msg.setDataBean(tableName);
		msg.setSql(sqlStr);
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);//转换为json字符串进行传递

		String resultListStr = synClientSupport.sqlMessageSend(msg);//同步通信
		if(resultListStr == null){//此情况一般不会出现
			return null;		
		}else{
			List<T> list = JsonUtil.getList4Json(resultListStr,t.getClass());
			return list;
		}
	}

}
