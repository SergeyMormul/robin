/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.generatorfunction;

import com.sciforce.robin.graph.view.CellState;

/**
 * A constant cost function that can be used during graph generation
 * All generated edges will have the weight <b>cost</b> 
 */
public class GeneratorConstFunction extends GeneratorFunction
{
	private double cost;
	
	public GeneratorConstFunction(double cost)
	{
		this.cost = cost;
	};
	
	public double getCost(CellState state)
	{
		return cost;
	};
}
