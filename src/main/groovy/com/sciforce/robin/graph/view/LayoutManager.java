/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.view;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sciforce.robin.graph.layout.IGraphLayout;
import com.sciforce.robin.graph.model.GraphModel;
import com.sciforce.robin.graph.model.IGraphModel;
import com.sciforce.robin.graph.util.*;
import com.sciforce.robin.graph.util.Event;

/**
 * Implements a layout manager that updates the layout for a given transaction.
 * The following example installs an automatic tree layout in a graph:
 * 
 * <code>
 * new LayoutManager(graph) {
 * 
 *   CompactTreeLayout layout = new CompactTreeLayout(graph);
 *   
 *   public IGraphLayout getLayout(Object parent)
 *   {
 *     if (graph.getModel().getChildCount(parent) > 0) {
 *       return layout;
 *     }
 *     return null;
 *   }
 * };
 * </code>
 * 
 * This class fires the following event:
 * 
 * Event.LAYOUT_CELLS fires between begin- and endUpdate after all cells have
 * been layouted in layoutCells. The <code>cells</code> property contains all
 * cells that have been passed to layoutCells.
 */
public class LayoutManager extends EventSource
{

	/**
	 * Defines the type of the source or target terminal. The type is a string
	 * passed to Cell.is to check if the rule applies to a cell.
	 */
	protected Graph graph;

	/**
	 * Optional string that specifies the value of the attribute to be passed
	 * to Cell.is to check if the rule applies to a cell. Default is true.
	 */
	protected boolean enabled = true;

	/**
	 * Optional string that specifies the attributename to be passed to
	 * Cell.is to check if the rule applies to a cell. Default is true.
	 */
	protected boolean bubbling = true;

	/**
	 * 
	 */
	protected mxIEventListener undoHandler = new mxIEventListener()
	{
		public void invoke(Object source, EventObject evt)
		{
			if (isEnabled())
			{
				beforeUndo((UndoableEdit) evt.getProperty("edit"));
			}
		}
	};

	/**
	 * 
	 */
	protected mxIEventListener moveHandler = new mxIEventListener()
	{
		public void invoke(Object source, EventObject evt)
		{
			if (isEnabled())
			{
				cellsMoved((Object[]) evt.getProperty("cells"), (Point) evt
						.getProperty("location"));
			}
		}
	};

	/**
	 * 
	 */
	public LayoutManager(Graph graph)
	{
		setGraph(graph);
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * @param value the enabled to set
	 */
	public void setEnabled(boolean value)
	{
		enabled = value;
	}

	/**
	 * @return the bubbling
	 */
	public boolean isBubbling()
	{
		return bubbling;
	}

	/**
	 * @param value the bubbling to set
	 */
	public void setBubbling(boolean value)
	{
		bubbling = value;
	}

	/**
	 * @return the graph
	 */
	public Graph getGraph()
	{
		return graph;
	}

	/**
	 * @param value the graph to set
	 */
	public void setGraph(Graph value)
	{
		if (graph != null)
		{
			IGraphModel model = graph.getModel();
			model.removeListener(undoHandler);
			graph.removeListener(moveHandler);
		}

		graph = value;

		if (graph != null)
		{
			IGraphModel model = graph.getModel();
			model.addListener(Event.BEFORE_UNDO, undoHandler);
			graph.addListener(Event.MOVE_CELLS, moveHandler);
		}
	}

	/**
	 * 
	 */
	protected IGraphLayout getLayout(Object parent)
	{
		return null;
	}

	/**
	 * 
	 */
	protected void cellsMoved(Object[] cells, Point location)
	{
		if (cells != null && location != null)
		{
			IGraphModel model = getGraph().getModel();

			// Checks if a layout exists to take care of the moving
			for (int i = 0; i < cells.length; i++)
			{
				IGraphLayout layout = getLayout(model.getParent(cells[i]));

				if (layout != null)
				{
					layout.moveCell(cells[i], location.x, location.y);
				}
			}
		}
	}

	/**
	 * 
	 */
	protected void beforeUndo(UndoableEdit edit)
	{
		Collection<Object> cells = getCellsForChanges(edit.getChanges());
		IGraphModel model = getGraph().getModel();

		if (isBubbling())
		{
			Object[] tmp = GraphModel.getParents(model, cells.toArray());

			while (tmp.length > 0)
			{
				cells.addAll(Arrays.asList(tmp));
				tmp = GraphModel.getParents(model, tmp);
			}
		}

		layoutCells(Utils.sortCells(cells, false).toArray());
	}

	/**
	 * 
	 */
	protected Collection<Object> getCellsForChanges(
			List<UndoableEdit.UndoableChange> changes)
	{
		Set<Object> result = new HashSet<Object>();
		Iterator<UndoableEdit.UndoableChange> it = changes.iterator();

		while (it.hasNext())
		{
			UndoableEdit.UndoableChange change = it.next();

			if (change instanceof GraphModel.RrootChange)
			{
				return new HashSet<Object>();
			}
			else
			{
				result.addAll(getCellsForChange(change));
			}
		}

		return result;
	}

	/**
	 * 
	 */
	protected Collection<Object> getCellsForChange(UndoableEdit.UndoableChange change)
	{
		IGraphModel model = getGraph().getModel();
		Set<Object> result = new HashSet<Object>();

		if (change instanceof GraphModel.ChildChange)
		{
			GraphModel.ChildChange cc = (GraphModel.ChildChange) change;
			Object parent = model.getParent(cc.getChild());

			if (cc.getChild() != null)
			{
				result.add(cc.getChild());
			}

			if (parent != null)
			{
				result.add(parent);
			}

			if (cc.getPrevious() != null)
			{
				result.add(cc.getPrevious());
			}
		}
		else if (change instanceof GraphModel.TerminalChange
				|| change instanceof GraphModel.GeometryChange)
		{
			Object cell = (change instanceof GraphModel.TerminalChange) ? ((GraphModel.TerminalChange) change)
					.getCell()
					: ((GraphModel.GeometryChange) change).getCell();

			if (cell != null)
			{
				result.add(cell);
				Object parent = model.getParent(cell);

				if (parent != null)
				{
					result.add(parent);
				}
			}
		}

		return result;
	}

	/**
	 * 
	 */
	protected void layoutCells(Object[] cells)
	{
		if (cells.length > 0)
		{
			// Invokes the layouts while removing duplicates
			IGraphModel model = getGraph().getModel();

			model.beginUpdate();
			try
			{
				for (int i = 0; i < cells.length; i++)
				{
					if (cells[i] != model.getRoot())
					{
						executeLayout(getLayout(cells[i]), cells[i]);
					}
				}

				fireEvent(new EventObject(Event.LAYOUT_CELLS, "cells",
						cells));
			}
			finally
			{
				model.endUpdate();
			}
		}
	}

	/**
	 * 
	 */
	protected void executeLayout(IGraphLayout layout, Object parent)
	{
		if (layout != null && parent != null)
		{
			layout.execute(parent);
		}
	}

	/**
	 * 
	 */
	public void destroy()
	{
		setGraph(null);
	}

}
