package ctc.transport;

import java.net.InetSocketAddress;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import ctc.constant.Constants;
import ctc.transport.AsynClientHandler.*;
import ctc.transport.message.*;

//异步客户端  目前不用
public class MinaClient implements Callback, QuitCallback{

	//设置Session空闲通知的时间间隔
	private static final Long CONNECT_TIMEOUT = 30*1000L; // 30 seconds

	private static int port = 8080;
	private static String IPAddress;
	
	private static NioSocketConnector connector;
	private static AsynClientHandler asynHandler;
	private static AsynClientSupport asynClientSupport;
	private static SynClientSupport synClientSupport;

	private static Callback callback;
	
	public MinaClient(){}
	
	public MinaClient(Callback callback){
		this.callback = callback;
	}
	public static SynClientSupport getSynClientSupport(){
		return synClientSupport;
	}
	
	public static AsynClientSupport getAsynClientSupport(){
		return asynClientSupport;
	}
	

	public boolean connect(String hostAddress ,String ipport, String username, String password, int userrole){//建立连接
		
		
		IPAddress = hostAddress;	
		port = Integer.parseInt(ipport);

		connector = new NioSocketConnector();//创建客户端连接器

		DefaultIoFilterChainBuilder chain = connector.getFilterChain();//创建接收数据的过滤器

		//Configure the service.
		connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
		//connector.setConnectTimeoutCheckInterval(CONNECT_TIMEOUT); 
		//10秒内没有读写就设置为空闲通道
		//connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		//设置缓冲区的大小
		//connector.getSessionConfig().setReadBufferSize(2048);

		//inject some key IoSession properties 如 remoteAddress  localAddress
		MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
		chain.addLast("mdc", mdcInjectionFilter);

		//设置编码过滤器
		chain.addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));//系统定义包解析
		
		//chain.addLast("logger", new LoggingFilter());

		//绑定异步消息处理类
		asynHandler = new AsynClientHandler(MinaClient.this,MinaClient.this);//接收异步消息用
		asynClientSupport = new AsynClientSupport(username,userrole,asynHandler);//发送异步消息用
		boolean asynClientFlag = asynClientSupport.connect(connector, new InetSocketAddress(IPAddress,port));
		//System.out.println("asyn:" + asynClientFlag);
		
		//绑定同步消息处理类
		synClientSupport = new SynClientSupport(username,password,userrole);//收发同步消息用
		boolean synClientFlag = synClientSupport.connect(connector, new InetSocketAddress(IPAddress,this.port));
		//System.out.println("syn:" + synClientFlag);
		
		// 绑定address
		if ((! synClientFlag) ||(! synClientFlag))
		{//连接服务器失败
			closeConnection(Constants.CLIENT_CLOSE_NORMAL);
			return false;
		}
		return true;
	}
	
   //供其他类调用. 客户机退出时的处理
	public void closeConnection(int quitFlag){//连接断开后释放资源,供CTCTeacherMain调用(选退出按钮)
		if (asynClientSupport != null)
			asynClientSupport.quit(quitFlag);
		
		if (synClientSupport != null)
			synClientSupport.quit(quitFlag);
		
		if (connector != null)
			connector.dispose();
	}
	
	//接收到服务器发送主动退出消息的处理
	public void serverCloseConnection(){
		if (asynClientSupport != null)
			asynClientSupport.serverQuit();
		
		if (synClientSupport != null)
			synClientSupport.serverQuit();
		
		if (connector != null)
			connector.dispose();
	}

	public LoginResponseMessage login(){//同步用户登录
		
		if (synClientSupport != null)
			return synClientSupport.loginMsgSend();
		return null;
	}
	
	//异步通知的回调方法
	@Override
	public void receivedSQLMessage(String result) {
		callback.receivedSQLMessage(result);
	}
	public void receivedTDCSCommandMessage(TDCSCommandMessage rMsg){
		callback.receivedTDCSCommandMessage(rMsg);
	}
	public void receivedTeamTdcsRsbMessage(TeamTdcsRsbMessage rMsg){
		callback.receivedTeamTdcsRsbMessage(rMsg);
	}
	public void receivedTrainLineAnchorMessage(TrainLineAnchorMessage rMsg){
		callback.receivedTrainLineAnchorMessage(rMsg);
	}
	
	@Override
	public void loggedOut() {
		serverCloseConnection();//系统退出
	}

}
