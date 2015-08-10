package ctc.databaseserver;

import ctc.tdcs.drawgraphics.DrawCurrentTimeAxis;
import ctc.tdcs.drawgraphics.RelocationPlanTrain;
import ctc.transport.AsynClientHandler.Callback;
import ctc.transport.message.TDCSCommandMessage;
import ctc.transport.message.TeamTdcsRsbMessage;
import ctc.transport.message.TrainLineAnchorMessage;

public class CallbackServer implements Callback{

	
	private CallbackServer thisData = null;
	public CallbackServer getInstance(){
		if (thisData == null){
			thisData = new CallbackServer();
		}
		return thisData;
	}
//////////////////////////////////////////////////////////////////////////////////	
	
	@Override
	public void receivedTDCSCommandMessage(TDCSCommandMessage rMsg) {
		//目前没有用 将来用做接受
	}

	@Override
	public void receivedSQLMessage(String result) {
		//目前没有用
	}
	
	/**处理服务器端转发来的TeamTdcsRsbMessage*/
	@Override
	public void receivedTeamTdcsRsbMessage(TeamTdcsRsbMessage rMsg){
		System.out.println(rMsg.getTeamID());
	}
	
	/**接受服务器转发来普通站机发送给TDCS的TrainLineAnchorMessage消息*/
	@Override
	public void receivedTrainLineAnchorMessage(TrainLineAnchorMessage rMsg){
		new RelocationPlanTrain().drawTrainLine(rMsg);
	}
	
	

}
