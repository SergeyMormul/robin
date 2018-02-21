/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.shape;

import java.util.Map;

import com.sciforce.robin.graph.canvas.Graphics2DCanvas;
import com.sciforce.robin.graph.view.CellState;

public interface ITextShape
{
	/**
	 * 
	 */
	void paintShape(Graphics2DCanvas canvas, String text, CellState state,
                    Map<String, Object> style);

}
