/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.canvas;

import com.sciforce.robin.graph.util.mxPoint;
import com.sciforce.robin.graph.view.CellState;

/**
 * Defines the requirements for a canvas that paints the vertices and edges of
 * a graph.
 */
public interface ICanvas
{
	/**
	 * Sets the translation for the following drawing requests.
	 */
	void setTranslate(double x, double y);

	/**
	 * Returns the current translation.
	 * 
	 * @return Returns the current translation.
	 */
	mxPoint getTranslate();

	/**
	 * Sets the scale for the following drawing requests.
	 */
	void setScale(double scale);

	/**
	 * Returns the scale.
	 */
	double getScale();

	/**
	 * Draws the given cell.
	 * 
	 * @param state State of the cell to be painted.
	 * @return Object that represents the cell.
	 */
	Object drawCell(CellState state);

	/**
	 * Draws the given label.
	 * 
	 * @param text String that represents the label.
	 * @param state State of the cell whose label is to be painted.
	 * @param html Specifies if the label contains HTML markup.
	 * @return Object that represents the label.
	 */
	Object drawLabel(String text, CellState state, boolean html);

}
