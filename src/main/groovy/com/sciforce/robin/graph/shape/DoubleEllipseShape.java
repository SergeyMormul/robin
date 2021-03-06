/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.shape;

import java.awt.Rectangle;

import com.sciforce.robin.graph.canvas.Graphics2DCanvas;
import com.sciforce.robin.graph.util.Constants;
import com.sciforce.robin.graph.util.Utils;
import com.sciforce.robin.graph.view.CellState;

public class DoubleEllipseShape extends EllipseShape
{

	/**
	 * 
	 */
	public void paintShape(Graphics2DCanvas canvas, CellState state)
	{
		super.paintShape(canvas, state);

		int inset = (int) Math.round((Utils.getFloat(state.getStyle(),
				Constants.STYLE_STROKEWIDTH, 1) + 3)
				* canvas.getScale());

		Rectangle rect = state.getRectangle();
		int x = rect.x + inset;
		int y = rect.y + inset;
		int w = rect.width - 2 * inset;
		int h = rect.height - 2 * inset;
		
		canvas.getGraphics().drawOval(x, y, w, h);
	}

}
