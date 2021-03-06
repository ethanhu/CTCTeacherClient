package ctc.transport.message;

import java.io.Serializable;
//此类为所有消息的公共头
public abstract class AbstractMessage implements Serializable {
    
	private static final long serialVersionUID = -3861736263558839958L;

	private int commandMode;//通信模式  同步或异步 服务器发向客户段 或客户端发向服务器等
	
	private int commandType;//通信类别  此数据报所表示的 具体操作.
	
	private int userRole;//用户角色
	
	public int getCommandMode() {
		return commandMode;
	}
	public void setCommandMode(int commandMode) {
		this.commandMode = commandMode;
	}
	public int getCommandType() {
		return commandType;
	}
	public void setCommandType(int commandType) {
		this.commandType = commandType;
	}
	public int getUserRole() {
		return userRole;
	}
	public void setUserRole(int userRole) {
		this.userRole = userRole;
	}
	

  
}