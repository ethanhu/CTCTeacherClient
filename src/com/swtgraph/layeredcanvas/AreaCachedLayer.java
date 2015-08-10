package com.swtgraph.layeredcanvas;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;





public abstract class AreaCachedLayer implements ICanvasLayer, ICacheableLayer {

	private ImageData cacheData;
	
	public abstract void buildImage(GC gc);
	
	public void paint(GC gc) {
		Rectangle bounds = getLayerArea(gc);
		
		if (!isCacheAvaliable()) {
			Image imageCache = new Image(gc.getDevice(), bounds);
			GC cacheGc = new GC(imageCache);
			buildImage(cacheGc);
			cacheData = imageCache.getImageData();
			imageCache.dispose();
			cacheGc.dispose();
			
			RGB transparentPixel = getTransparentPixel();
			if (transparentPixel != null)
				cacheData.transparentPixel = cacheData.palette.getPixel(transparentPixel);
		}
		
		Image cache = new Image(gc.getDevice(), cacheData);
		gc.drawImage(cache, bounds.x, bounds.y);
		cache.dispose();
	}
	
	protected Rectangle getLayerArea(GC gc) {
		return gc.getClipping();
	}

	
	
	public RGB getTransparentPixel() {
		return null;
	}

	
	
	public void dispose() {
		if (isCacheAvaliable()) {
			cacheData = null;
		}
	}

	public boolean isCacheAvaliable() {
		return cacheData != null;
	}

	public boolean forgetCache() {
		if (cacheData != null) {
			cacheData = null;
			return true;
		}
		return false;
	}
}
