/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.shape;

import com.sciforce.robin.graph.canvas.Graphics2DCanvas;
import com.sciforce.robin.graph.view.CellState;

public interface IShape
{
	/**
	 * 
	 */
	void paintShape(Graphics2DCanvas canvas, CellState state);

}
