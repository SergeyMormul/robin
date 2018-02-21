/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.shape;

import java.awt.Color;
import java.awt.Rectangle;

import com.sciforce.robin.graph.canvas.Graphics2DCanvas;
import com.sciforce.robin.graph.util.Constants;
import com.sciforce.robin.graph.util.Utils;
import com.sciforce.robin.graph.view.CellState;

/**
 * A rectangular shape that contains a single image. See ImageBundle for
 * creating a lookup table with images which can then be referenced by key.
 */
public class ImageShape extends RectangleShape
{

	/**
	 * 
	 */
	public void paintShape(Graphics2DCanvas canvas, CellState state)
	{
		super.paintShape(canvas, state);

		boolean flipH = Utils.isTrue(state.getStyle(),
				Constants.STYLE_IMAGE_FLIPH, false);
		boolean flipV = Utils.isTrue(state.getStyle(),
				Constants.STYLE_IMAGE_FLIPV, false);

		canvas.drawImage(getImageBounds(canvas, state),
				getImageForStyle(canvas, state),
				Graphics2DCanvas.PRESERVE_IMAGE_ASPECT, flipH, flipV);
	}

	/**
	 * 
	 */
	public Rectangle getImageBounds(Graphics2DCanvas canvas, CellState state)
	{
		return state.getRectangle();
	}

	/**
	 * 
	 */
	public boolean hasGradient(Graphics2DCanvas canvas, CellState state)
	{
		return false;
	}

	/**
	 * 
	 */
	public String getImageForStyle(Graphics2DCanvas canvas, CellState state)
	{
		return canvas.getImageForStyle(state.getStyle());
	}

	/**
	 * 
	 */
	public Color getFillColor(Graphics2DCanvas canvas, CellState state)
	{
		return Utils.getColor(state.getStyle(),
				Constants.STYLE_IMAGE_BACKGROUND);
	}

	/**
	 * 
	 */
	public Color getStrokeColor(Graphics2DCanvas canvas, CellState state)
	{
		return Utils.getColor(state.getStyle(),
				Constants.STYLE_IMAGE_BORDER);
	}

}
