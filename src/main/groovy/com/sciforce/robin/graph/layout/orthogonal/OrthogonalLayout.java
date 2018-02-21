/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.layout.orthogonal;

import com.sciforce.robin.graph.layout.GraphLayout;
import com.sciforce.robin.graph.layout.orthogonal.model.OrthogonalModel;
import com.sciforce.robin.graph.view.Graph;

/**
 *
 */
/**
*
*/
public class OrthogonalLayout extends GraphLayout
{

  /**
   * 
   */
  protected OrthogonalModel orthModel;

  /**
   * Whether or not to route the edges along grid lines only, if the grid
   * is enabled. Default is false
   */
  protected boolean routeToGrid = false;
  
  /**
   * 
   */
  public OrthogonalLayout(Graph graph)
  {
     super(graph);
     orthModel = new OrthogonalModel(graph);
  }

  /**
   * 
   */
  public void execute(Object parent)
  {
     // Create the rectangulation
     
  }

}
