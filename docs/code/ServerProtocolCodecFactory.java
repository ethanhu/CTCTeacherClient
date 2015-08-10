package ctc.transport;

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import ctc.transport.decode.DecoderLoginMessage;
import ctc.transport.decode.DecoderLoginResponseMessage;
import ctc.transport.encode.EncoderLoginMessage;
import ctc.transport.encode.EncoderLoginResponseMessage;
import ctc.transport.message.AbstractMessage;
import ctc.transport.message.LoginResponseMessage;
//参考代码：http://read.pudn.com/downloads71/sourcecode/java/257691/minaSocket/examples/org/apache/mina/examples/call/encode/EncoderLoginMessage.java__.htm
import ctc.transport.message.LoginMessage;

public class ServerProtocolCodecFactory extends DemuxingProtocolCodecFactory{
    

	public ServerProtocolCodecFactory(boolean client) {
		if (client) {
			//login  
			super.addMessageEncoder(LoginResponseMessage.class,(Class<? extends MessageEncoder>) EncoderLoginResponseMessage.class);
			super.addMessageDecoder(DecoderLoginMessage.class);


		}else {//client

			// login  
			super.addMessageDecoder(DecoderLoginMessage.class);  
			super.addMessageEncoder(LoginResponseMessage.class,(Class<? extends MessageEncoder>) EncoderLoginResponseMessage.class);  
		}
	}
	
}