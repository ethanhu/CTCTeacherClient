package ctc.transport.encode;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import ctc.constant.Constants;
import ctc.transport.message.LoginResponseMessage;

public class EncoderLoginResponseMessage <T extends LoginResponseMessage> extends EncoderAbstractMessage<T> {
	
    public EncoderLoginResponseMessage() {
        super(Constants.TYPE_LOGIN_RESPONSE);
    }

    @Override
    protected void encodeBody(IoSession session, T message, IoBuffer out) {
    	
    	LoginResponseMessage msg = (LoginResponseMessage) message; 
    	
    	//System.out.println(this + "--info encodeBody begin");
    	
    	out.putInt(msg.getResult());   
    	out.putInt(msg.getOperation());   
    	// System.out.println(this + "--info encodeBody end");
    }

    public void dispose() throws Exception {
    }
}
