package com.groupcdg.maven.tidesdk;

import java.util.ArrayList;
import java.util.Collection;

public class Display {

	private Integer width;

	private Integer maxWidth;

	private Integer minWidth;

	private Integer height;

	private Integer maxHeight;

	private Integer minHeight;

	private Boolean fullscreen;

	private Boolean resizable;

	private Boolean chrome;

	private Boolean scrollable;

	private Boolean maximizable;

	private Boolean minimizable;

	private Boolean closeable;



	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
	}

	public void setMinWidth(Integer minWidth) {
		this.minWidth = minWidth;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public void setMaxHeight(Integer maxHeight) {
		this.maxHeight = maxHeight;
	}

	public void setMinHeight(Integer minHeight) {
		this.minHeight = minHeight;
	}

	public void setFullscreen(Boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	public void setResizable(Boolean resizable) {
		this.resizable = resizable;
	}

	public void setChrome(Boolean chrome) {
		this.chrome = chrome;
	}

	public void setScrollable(Boolean scrollable) {
		this.scrollable = scrollable;
	}

	public void setMaximizable(Boolean maximizable) {
		this.maximizable = maximizable;
	}

	public void setMinimizable(Boolean minimizable) {
		this.minimizable = minimizable;
	}

	public void setCloseable(Boolean closeable) {
		this.closeable = closeable;
	}



	Collection<String> createXml(final String name, final String index) {
		return new ArrayList<String>() {{
			add("<window>");
			add("\t<id>initial</id>");
			add("\t<title>" + name + "</title>");
			add("\t<url>app://" + index + "</url>");

			if(width != null) add("\t<width>" + width + "</width>");
			if(maxWidth != null) add("\t<max-width>" + maxWidth + "</max-width>");
			if(minWidth != null) add("\t<min-width>" + minWidth + "</min-width>");
			if(height != null) add("\t<height>" + height + "</height>");
			if(maxHeight != null) add("\t<max-height>" + maxHeight + "</max-height>");
			if(minHeight != null) add("\t<min-height>" + minHeight + "</min-height>");
			if(fullscreen != null) add("\t<fullscreen>" + String.valueOf(fullscreen) + "</fullscreen>");
			if(resizable != null) add("\t<resizable>" + String.valueOf(resizable) + "</resizable>");

			add("\t<chrome scrollbars=\"" + String.valueOf(scrollable == null || scrollable)
					+ "\">" + String.valueOf(chrome == null || chrome) + "</chrome>");
			if(maximizable != null) add("\t<maximizable>" + String.valueOf(maximizable) + "</maximizable>");
			if(minimizable != null) add("\t<minimizable>" + String.valueOf(minimizable) + "</minimizable>");
			if(closeable != null) add("\t<closeable>" + String.valueOf(closeable) + "</closeable>");

			add("</window>");
		}};
	}


}
