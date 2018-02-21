/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.swing.util;

import com.sciforce.robin.graph.util.Rectangle;
import com.sciforce.robin.graph.view.CellState;

public interface ICellOverlay
{

	/**
	 * 
	 */
	Rectangle getBounds(CellState state);

}
