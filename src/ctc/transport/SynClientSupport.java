package ctc.transport;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import ctc.constant.Constants;
import ctc.transport.message.*;

public class SynClientSupport {
	
	//设置Session空闲通知的时间间隔
	private static final Long CONNECT_TIMEOUT = 60*1000L; //60 seconds
		
	private static IoSession session;
	private static String username;
	private static String password;
	private static int userole;

	public SynClientSupport(){};
	public SynClientSupport(String username, String password,int userole) {
		this.userole = userole;
		this.username = username;
		this.password = password;
	}

	public boolean connect(NioSocketConnector connector, SocketAddress address) {
		boolean loginFlag = false;
		
		if (session != null && session.isConnected()) {
			throw new IllegalStateException("Already connected. Disconnect first.");
		}

		SocketSessionConfig cfg = connector.getSessionConfig();
		cfg.setUseReadOperation(true);//实现同步的客户端, MINA默认是异步的
		//session.getConfig().setUseReadOperation(true);

		ConnectFuture future = connector.connect(address);//建立连接
		future.awaitUninterruptibly();//等待连接创建完成  awaitUninterruptibly(CONNECT_TIMEOUT);
		if (! future.isConnected()) {
			return loginFlag;
		}
		session = future.getSession();//获取一个会话
		if (session == null)
			return loginFlag;
		else
			return !loginFlag;
	}

	  /////////////下面的方法用于向服务器发送-接收消息, 实现同步通信////////////////////

	//向服务器发送故障或调度命令
	public void ScheduleORErrorCommandToServer(ScheduleErrorMessage sMsg){

		if( session == null)
			return;
		
		session.write(sMsg);//发送消息
	}
	
	
	//发送-接收TDCS消息TDCSCommandMessage
	public TDCSCommandMessage TDCSMessageSynSend(TDCSCommandMessage sMsg){
		
		session.write(sMsg);//发送消息
		
		//接收消息
		ReadFuture readFuture = session.read();
		if (readFuture.awaitUninterruptibly(CONNECT_TIMEOUT, TimeUnit.SECONDS)) 
		{//正常收到服务器发来的消息
			Object obj = readFuture.getMessage();

			TDCSCommandMessage rMsg = (TDCSCommandMessage)obj;

			if( (rMsg == null) || (rMsg.getResult() == Constants.SERVER_RESULT_ERROR))
				return null;
			return rMsg;
		}
		else {//读超时
			return null;	
		}
	}
	/////////////////////以上为新写方法 2020-2-3/////////////////////////////////////////////////////////////////////
	
	
	
	/*  关于通信，由于同一程序实现了同步和异步两种通信方法，当同步通信接受到消息有可能是异步发送的，反过来也一样 . 尽量不要让同步与一不同时起作用，否则程序需要等待自己需要的报文*/
	
	//实现策略：登录时建立连接，整个程序退出时，关闭连接
	//供CTCClient_T调用
	//发送-接收登录消息
	public LoginResponseMessage loginMsgSend() {
		
		if( session == null)
			return null;
		
		//组装登录消息
		LoginMessage loginMsg = new LoginMessage(); 
		loginMsg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);   
		loginMsg.setCommandType(Constants.TYPE_CLIENT_LOGIN);
		loginMsg.setUserRole(userole);
		
		loginMsg.setPassword(password);
		loginMsg.setUsername(username);
		
		//发送消息
		session.write(loginMsg).awaitUninterruptibly();
		
		//接收消息
		ReadFuture readFuture = session.read();
		if (readFuture.awaitUninterruptibly(CONNECT_TIMEOUT, TimeUnit.SECONDS))
		{
			Object obj = readFuture.getMessage();
			if(obj instanceof LoginResponseMessage)
				return (LoginResponseMessage)obj;
		} else {//读超时
			return null;	
		}
		return null;
	}

	
	//发送-接收退出消息. 目前 对接收到的信息并没有进行处理
	public boolean logoutMsgSend(int quitFlag) {
		LogoutMessage logOutMsg = new LogoutMessage();
		logOutMsg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);   
		logOutMsg.setCommandType(Constants.TYPE_CLIENT_LOGOUT);
		logOutMsg.setUserRole(userole);
		logOutMsg.setUsername(username);
		logOutMsg.setQuitFlag(quitFlag);

		session.write(logOutMsg);//发送消息
		//接收消息
		ReadFuture readFuture = session.read();
		if (readFuture.awaitUninterruptibly(CONNECT_TIMEOUT, TimeUnit.SECONDS)) 
		{
			Object obj = readFuture.getMessage();
			if (obj instanceof LogoutResponseMessage)
			{
				LogoutResponseMessage rMsg = (LogoutResponseMessage)obj;

				if( (rMsg != null) && (rMsg.getResult() == Constants.SERVER_RESULT_OK)) 
					return true;
				else //非法用户
					return false;
			}
			return false;
		} else 
		{//读超时
			return false;	
		}
	}

	//发送-接收所发命令消息 同步通信
	public boolean ExperimentCommandMessageSend(ExperimentCommandMessage msg){
		/*这里的代码是发送后，不等待处理结果，应该算异步发送*/
		session.write(msg);//发送消息
	
		boolean flag = false;
		ReadFuture readFuture = session.read();//接收消息
		if (readFuture.awaitUninterruptibly(CONNECT_TIMEOUT, TimeUnit.SECONDS)) 
		{//正常接收
			Object obj = readFuture.getMessage();
			if (obj instanceof ExperimentCommandResponseMessage)
			{
				ExperimentCommandResponseMessage rMsg = (ExperimentCommandResponseMessage)readFuture.getMessage();
				if((rMsg != null) && ( rMsg.getResult() == Constants.SERVER_RESULT_OK))
					flag = true;
			}
		}else 
		{
			//读超时
		}
		return flag;
	}

	
	//////////////////////////////////////////////////////
	//发送-接收SQL操作消息
	public String sqlMessageSend(SQLRequestMessage msg){
		session.write(msg);//发送消息
		//接收消息
		ReadFuture readFuture = session.read();
		if (readFuture.awaitUninterruptibly(CONNECT_TIMEOUT, TimeUnit.SECONDS)) 
		{//正常收到服务器发来的消息
			Object obj = readFuture.getMessage();

			SQLResponseMessage rMsg = (SQLResponseMessage)obj;

			if( (rMsg == null) || (rMsg.getResult() == Constants.SERVER_RESULT_ERROR))
				return null;

			switch(rMsg.getCommandType()){
			case Constants.TYPE_SQLUPDATE_RESPONSE:
			case Constants.TYPE_SQLDELETE_RESPONSE:
			case Constants.TYPE_SQLINSERT_RESPONSE:
				return "TRUE";
			case Constants.TYPE_SQLQUERY_RESPONSE:
				return rMsg.getList();
			default:
				return null;	
			}
		}
		else {//读超时
			return null;	
		}
	}
	
	public void quit(int quitFlag) {
		if (session != null) {
			if (session.isConnected()) {
				logoutMsgSend(quitFlag);
				//Wait until the chat ends.
				session.getCloseFuture().awaitUninterruptibly();
			}
			//true to close this session immediately 
			session.close(true);//.awaitUninterruptibly(1000);加上后面的就会出现死锁现象,原因不明
			session = null;
		}
	}
	public void serverQuit() {
		if (session != null) {
			if (session.isConnected()) {
				session.close(true);
				session = null;	
			}
		}
		System.exit(1);
	}

}
