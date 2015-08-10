

package com.swtgraph.layeredcanvas;

public interface ICacheableLayer {
	
	//判断缓冲区是否可用
	boolean isCacheAvaliable();
	
	//放弃缓冲区
	boolean forgetCache();

	void dispose();
}
