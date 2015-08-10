package ctc.transport.decode;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import ctc.constant.Constants;
import ctc.transport.message.*;

public class DecoderLoginMessage extends DecoderAbstractMessage{

	protected DecoderLoginMessage() {
		super(Constants.TYPE_LOGIN);//login message;  
	}

	@Override
	protected AbstractMessage decodeBody(IoSession session, IoBuffer in) {
		//System.out.println("--info DecoderLoginMessage decodeBody begin");  

		// set byte order  
		in.order(Constants.BYTE_ORDER);  

		
		if (in.remaining() < Constants.LOGIN_LEN_LEAST) {  
			return null;  
		}  
		
		LoginMessage msg = new LoginMessage();  
		msg.setHead_len(this.head_len);  
		msg.setHead_command(this.head_command);  
		
		//注意顺序
		msg.setRole(in.getInt());//int=4  读取角色  role
		
		int userNameLength = in.getInt();//int=4 读取  msgLength
		msg.setUsernameLength(userNameLength);
		
		// set user from byte buffer  
		byte[] usernameByte = new byte[userNameLength];  
		in.get(usernameByte);
		String usernameString = new String(usernameByte);
		msg.setUsername(usernameString);//  

		int passwordLength = in.getInt();//int=4 读取 passwordLength
		msg.setPasswordLength(passwordLength);
		// set password  
		byte[] passwordByte = new byte[passwordLength];  
		in.get(passwordByte);
		String passwordString = new String(passwordByte);
		msg.setUsername(passwordString);//
		
		//System.out.println("--info DecoderLoginMessage decodeBody end");  
		return msg;  
	}
	
	//解码完成后调用的方法
	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
	}
}
