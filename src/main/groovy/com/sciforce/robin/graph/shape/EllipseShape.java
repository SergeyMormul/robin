/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.shape;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import com.sciforce.robin.graph.canvas.Graphics2DCanvas;
import com.sciforce.robin.graph.view.CellState;

public class EllipseShape extends BasicShape
{

	/**
	 * 
	 */
	public Shape createShape(Graphics2DCanvas canvas, CellState state)
	{
		Rectangle temp = state.getRectangle();

		return new Ellipse2D.Float(temp.x, temp.y, temp.width, temp.height);
	}

}
