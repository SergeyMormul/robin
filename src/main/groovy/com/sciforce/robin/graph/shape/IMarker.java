/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.shape;

import com.sciforce.robin.graph.canvas.Graphics2DCanvas;
import com.sciforce.robin.graph.util.Point;
import com.sciforce.robin.graph.view.CellState;

public interface IMarker
{
	/**
	 * 
	 */
	Point paintMarker(Graphics2DCanvas canvas, CellState state, String type,
                      Point pe, double nx, double ny, double size, boolean source);

}
