package ctc.transport.encode;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import ctc.constant.Constants;
import ctc.transport.message.AbstractMessage;

public abstract class EncoderAbstractMessage<T extends AbstractMessage> implements MessageEncoder<T> {
 
	private final int head_command;  

	protected EncoderAbstractMessage(int commandID) {  
		this.head_command = commandID;  
	}  

	public void encode(IoSession session, T message, ProtocolEncoderOutput out) throws Exception {
		
		// System.out.println(this + "--info EncoderAbstractMessage begin");  
		AbstractMessage msg = (AbstractMessage) message;
		IoBuffer buf = IoBuffer.allocate(msg.getHead_len());
		buf.setAutoExpand(true); //Enable auto-expand for easier encoding
	
		buf.order(Constants.BYTE_ORDER);  
		buf.setAutoExpand(true); // Enable auto-expand for easier encoding  

		// Encode a header  
		buf.putInt(msg.getHead_len());  
		buf.putInt(msg.getHead_command());  
		
		// Encode a body
		encodeBody(session, message, buf);
        buf.flip();
        out.write(buf);
    }

    protected abstract void encodeBody(IoSession session, T message, IoBuffer out);
}
