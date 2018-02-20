/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.costfunction;

import com.sciforce.robin.graph.view.CellState;

/**
 * A constant cost function that returns <b>const</b> regardless of edge value
 */
public class ConstCostFunction extends CostFunction
{
	private double cost;
	
	public ConstCostFunction(double cost)
	{
		this.cost = cost;
	};
	
	public double getCost(CellState state)
	{
		return cost;
	};
}
