
package com.swtgraph.layeredcanvas;
import org.eclipse.swt.graphics.GC;

public interface ICanvasLayer {

	void paint(GC gc);
	
	void dispose();
	
}
