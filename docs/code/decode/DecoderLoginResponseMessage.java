package ctc.transport.decode;

import ctc.constant.Constants;
import ctc.transport.message.AbstractMessage;
import ctc.transport.message.LoginResponseMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class DecoderLoginResponseMessage extends DecoderAbstractMessage {

	public DecoderLoginResponseMessage() {
		super(Constants.TYPE_LOGIN_RESPONSE);
	}

	@Override
	protected AbstractMessage decodeBody(IoSession session, IoBuffer in) {
		// set byte order  
		in.order(Constants.BYTE_ORDER);  
		if (in.remaining() < this.head_len - Constants.HEADER_LEN_SUM) {  
			return null;  
		}  

		LoginResponseMessage msg = new LoginResponseMessage();  

		msg.setHead_len(this.head_len);  
		msg.setHead_command(this.head_command);  
		// set result/version  
		msg.setResult(in.getInt());// len(int)=4  
		msg.setOperation(in.getInt());// len(int)=4  
		return msg;
	}

	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
	throws Exception {
	}
}
