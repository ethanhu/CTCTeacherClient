package ctc.transport;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import ctc.constant.*;
import ctc.transport.message.*;

/*
 * client端的业务代码 处理消息接收事件
 * 处理服务端通信的类，用于和服务端建立通信，处理由服务端发送过来的消息。
 * 
 *  异步接受服务器发来的消息
 */
public class AsynClientHandler extends IoHandlerAdapter {

	//定义供异步调用的方法
	public interface Callback {
	   void receivedSQLMessage(String result);
	   void receivedTDCSCommandMessage(TDCSCommandMessage rMsg);
	   void receivedTeamTdcsRsbMessage(TeamTdcsRsbMessage rMsg);
	   void receivedTrainLineAnchorMessage(TrainLineAnchorMessage rMsg);
	   
	}
	
	public interface QuitCallback {
		   void loggedOut();
	}
	
	///////////////////////////////////////////////
	//返回给MinaClient类进行处理
	private Callback callback;
	private QuitCallback quitCallback;

	public AsynClientHandler(Callback callback,QuitCallback quitCallback) {
		//System.out.println("callback001:" + callback);
		this.callback = callback;
		this.quitCallback = quitCallback;
	}

	/////////以下是实现由接口IoHandlerAdapter所定义的方法////////////////////////
	//下面方法的功能是用来接收服务器发来的消息,并通过调用Callback接口中定义的方法来通知类CTCSynClient进行具体处理
	/*
	 * 处理异常: 一旦收到执行中的异常，为保证后续任务可用，强行关闭本次session连接
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		// LOGGER.warn("Unexpected exception.", cause);
		//cause.printStackTrace();
		// Close connection when unexpected exception is caught.
		session.close(true);
	}
	//在sessionCreated调用之后被调用；当会话开始时被触发
	@Override
	public void sessionOpened(IoSession session) throws Exception {
	}

	//接收服务器发送的消息  调用各任务类的处理函数. 只处理服务器发送的异步消息  
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception 
	{
		if (message instanceof LogoutResponseMessage)//处理退出消息     对同步退出消息不处理 
		{
			LogoutResponseMessage rMsg = (LogoutResponseMessage) message;

			if ( (rMsg.getCommandMode() == Constants.MODE_CS_ASYN_SERVER) &&
				 (rMsg.getUserRole() == Constants.USER_ROLE_SERVER) )
			{
				if (rMsg.getResult() == Constants.SERVER_RESULT_OK)
				{
					quitCallback.loggedOut();
				}
			}
		}else	
		if (message instanceof SQLResponseMessage)//处理SQL消息 
		{
			SQLResponseMessage rMsg = (SQLResponseMessage) message;
			if (rMsg.getCommandMode() == Constants.MODE_CS_SYN_SERVER)
				return;//对同步消息不处理
		
			if (rMsg.getResult() == Constants.SERVER_RESULT_OK) {
				callback.receivedSQLMessage(rMsg.getList());
			}else {
				callback.receivedSQLMessage(null);
			}
		}else
		if (message instanceof TDCSCommandMessage)//接受服务器发来的TDCS消息 
		{
			Object obj = message;
			TDCSCommandMessage rMsg;
			if (obj instanceof TDCSCommandMessage)
				rMsg = (TDCSCommandMessage) message;
			else
				return;//非P2PCommandResponseMessage
	
			//对同步消息不处理 实际上目前服务器只发送异步消息
			if (rMsg.getCommandMode() == Constants.MODE_CS_SYN_SERVER){
				return;
			}
			callback.receivedTDCSCommandMessage(rMsg);
 		} //处理服务器发送的消息
		
		else/**接受服务器转发来TDCS发送的TeamTdcsRsbMessage消息*/
		if (message instanceof TeamTdcsRsbMessage) 
		{
			Object obj = message;
			TeamTdcsRsbMessage rMsg;
			if (obj instanceof TeamTdcsRsbMessage)
				rMsg = (TeamTdcsRsbMessage) message;
			else
				return;//非P2PCommandResponseMessage
	
			//对同步消息不处理 实际上目前服务器只发送异步消息
			if (rMsg.getCommandMode() == Constants.MODE_CS_SYN_SERVER){
				return;
			}
			callback.receivedTeamTdcsRsbMessage(rMsg);
 		}
		else/**接受服务器转发来普通站机发送给TDCS的TrainLineAnchorMessage消息*/
		if (message instanceof TrainLineAnchorMessage) 
		{
			Object obj = message;
			TrainLineAnchorMessage rMsg;
			if (obj instanceof TrainLineAnchorMessage)
				rMsg = (TrainLineAnchorMessage) message;
			else
				return;//非P2PCommandResponseMessage
		
			//对同步消息不处理 实际上目前服务器只发送异步消息
			if (rMsg.getCommandMode() == Constants.MODE_CS_SYN_SERVER){
				return;
			}
			callback.receivedTrainLineAnchorMessage(rMsg);
	 	}
		
	}


	/*
	 * 响应session关闭事件
	 * 
	 */
	@Override
	public void sessionClosed(IoSession session) {
		//处理远程主机强迫关闭了一个现有的连接 即当客户机已经连在服务器的情况下,服务器异常退出时,客户端的处理代码
		session.close(true);
		quitCallback.loggedOut();
	}

}
