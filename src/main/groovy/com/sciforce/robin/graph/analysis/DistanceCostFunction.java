/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.analysis;

import com.sciforce.robin.graph.util.mxPoint;
import com.sciforce.robin.graph.view.CellState;

/**
 * Implements a cost function for the Euclidean length of an edge.
 */
public class DistanceCostFunction implements ICostFunction
{

	/**
	 * Returns the Euclidean length of the edge defined by the absolute
	 * points in the given state or 0 if no points are defined.
	 */
	public double getCost(CellState state)
	{
		double cost = 0;
		int pointCount = state.getAbsolutePointCount();

		if (pointCount > 0)
		{
			mxPoint last = state.getAbsolutePoint(0);

			for (int i = 1; i < pointCount; i++)
			{
				mxPoint point = state.getAbsolutePoint(i);
				cost += point.getPoint().distance(last.getPoint());
				last = point;
			}
		}

		return cost;
	}
}
