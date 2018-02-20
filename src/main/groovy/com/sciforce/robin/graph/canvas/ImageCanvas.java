/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.canvas;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.sciforce.robin.graph.util.mxUtils;
import com.sciforce.robin.graph.util.mxPoint;
import com.sciforce.robin.graph.view.mxCellState;

/**
 * An implementation of a canvas that uses Graphics2D for painting. To use an
 * image canvas for an existing graphics canvas and create an image the
 * following code is used:
 * 
 * <code>BufferedImage image = mxCellRenderer.createBufferedImage(graph, cells, 1, Color.white, true, null, canvas);</code> 
 */
public class ImageCanvas implements ICanvas
{

	/**
	 * 
	 */
	protected Graphics2DCanvas canvas;

	/**
	 * 
	 */
	protected Graphics2D previousGraphics;

	/**
	 * 
	 */
	protected BufferedImage image;

	/**
	 * 
	 */
	public ImageCanvas(Graphics2DCanvas canvas, int width, int height,
                       Color background, boolean antiAlias)
	{
		this(canvas, width, height, background, antiAlias, true);
	}
	
	/**
	 * 
	 */
	public ImageCanvas(Graphics2DCanvas canvas, int width, int height,
                       Color background, boolean antiAlias, boolean textAntiAlias)
	{
		this.canvas = canvas;
		previousGraphics = canvas.getGraphics();
		image = mxUtils.createBufferedImage(width, height, background);

		if (image != null)
		{
			Graphics2D g = image.createGraphics();
			mxUtils.setAntiAlias(g, antiAlias, textAntiAlias);
			canvas.setGraphics(g);
		}
	}

	/**
	 * 
	 */
	public Graphics2DCanvas getGraphicsCanvas()
	{
		return canvas;
	}

	/**
	 * 
	 */
	public BufferedImage getImage()
	{
		return image;
	}

	/**
	 * 
	 */
	public Object drawCell(mxCellState state)
	{
		return canvas.drawCell(state);
	}

	/**
	 * 
	 */
	public Object drawLabel(String label, mxCellState state, boolean html)
	{
		return canvas.drawLabel(label, state, html);
	}

	/**
	 * 
	 */
	public double getScale()
	{
		return canvas.getScale();
	}

	/**
	 * 
	 */
	public mxPoint getTranslate()
	{
		return canvas.getTranslate();
	}

	/**
	 * 
	 */
	public void setScale(double scale)
	{
		canvas.setScale(scale);
	}

	/**
	 * 
	 */
	public void setTranslate(double dx, double dy)
	{
		canvas.setTranslate(dx, dy);
	}

	/**
	 * 
	 */
	public BufferedImage destroy()
	{
		BufferedImage tmp = image;

		if (canvas.getGraphics() != null)
		{
			canvas.getGraphics().dispose();
		}
		
		canvas.setGraphics(previousGraphics);

		previousGraphics = null;
		canvas = null;
		image = null;

		return tmp;
	}

}
