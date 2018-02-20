/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.analysis;

import com.sciforce.robin.graph.view.mxCellState;

/**
 * Implements a cost function for a constant cost per traversed cell.
 */
public class ConstantCostFunction implements ICostFunction
{

	/**
	 * 
	 */
	protected double cost = 0;

	/**
	 * 
	 * @param cost the cost value for this function
	 */
	public ConstantCostFunction(double cost)
	{
		this.cost = cost;
	}

	/**
	 *
	 */
	public double getCost(mxCellState state)
	{
		return cost;
	}

}
