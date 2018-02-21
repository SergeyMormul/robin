/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.layout.hierarchical.model;

import java.util.LinkedHashSet;

/**
 * An abstraction of a rank in the hierarchy layout. Should be ordered, perform
 * remove in constant time and contains in constant time
 */
public class GraphHierarchyRank extends LinkedHashSet<GraphAbstractHierarchyCell>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2781491210687143878L;
}
