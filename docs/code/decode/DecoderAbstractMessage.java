package ctc.transport.decode;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import ctc.constant.Constants;
import ctc.transport.message.AbstractMessage;

 // 检查指定的缓冲区decodable 解码器
public abstract class DecoderAbstractMessage implements MessageDecoder{
	
	protected int head_len;// first
	protected int head_command;//secode
	
	private boolean readHeader;
	 
	protected DecoderAbstractMessage(int head_command) {  
	      this.head_command = head_command;  
	}
	
	//判断是否可以decode
	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		// System.out.println("--info DecoderAbstractMessage begin " + this);  

		// set byte order  
		in.order(Constants.BYTE_ORDER);
		
		// Return NEED_DATA if the whole header is not read yet.  
		if (in.remaining() < Constants.HEADER_LEN_SUM) {
			return MessageDecoderResult.NEED_DATA;  
		 }  

		
		//skip 获取包头中的head_len数据  
		int len = in.getInt();  
		
		// check command id  
		int command = in.getInt();//读取AbstractMessage 中head_command的值  
		if (head_command == command) {  
			return MessageDecoderResult.OK;  
		}  

		// System.out.println("--info DecoderAbstractMessage end" + this);  

		// Return NOT_OK if not matches.  
		return MessageDecoderResult.NOT_OK;  
	}

	//解码二进制或协议的具体内容到更高级别的消息对象
	public MessageDecoderResult decode(IoSession session, IoBuffer in,ProtocolDecoderOutput out) throws Exception 
	{
		// Try to skip header if not read.注意顺序
		if (!readHeader) {
			this.head_len = in.getInt();//head_len  
			this.head_command = in.getInt();//command  
			readHeader = true;
		}

		// Try to decode body
		AbstractMessage msg = decodeBody(session, in);
		
		//Return NEED_DATA if the body is not fully read.
		if (msg == null) {
			return MessageDecoderResult.NEED_DATA;
		} else {
			readHeader = false; // reset readHeader for the next decode
		}
		msg.setHead_len(this.head_len);  
		msg.setHead_command(this.head_command);
		
		out.write(msg);

		return MessageDecoderResult.OK;
	}

    /**
     * @return <tt>null</tt> if the whole body is not read yet
     */
    protected abstract AbstractMessage decodeBody(IoSession session,IoBuffer in);

}
