package ctc.transport.encode;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import ctc.constant.Constants;
import ctc.transport.message.LoginMessage;
import ctc.util.ObjectByteUtil;

//网络传输是byte，所以编码时需要将字符串转换为字节码，解码时相反操作
public class EncoderLoginMessage<T extends LoginMessage> extends EncoderAbstractMessage<T> {
    public EncoderLoginMessage() {
        super(Constants.TYPE_LOGIN);
    }

    @Override
    protected void encodeBody(IoSession session, T message, IoBuffer out) {
    	//System.out.println(this + "--info encodeBody  begin");   
		LoginMessage msg = (LoginMessage) message;
		out.putInt(msg.getUsernameLength());   
		out.putInt(msg.getPasswordLength());
		
		byte[] bytes = ObjectByteUtil.hexStringToByte(msg.getUsername());
		out.put(bytes);
		
		bytes = ObjectByteUtil.hexStringToByte(msg.getPassword());
		out.put(bytes);   
		//System.out.println(this + "--info encodeBody  end");   
    }

    public void dispose() throws Exception {
    }
}
 
