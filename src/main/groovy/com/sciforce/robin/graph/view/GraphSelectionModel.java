/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sciforce.robin.graph.util.Event;
import com.sciforce.robin.graph.util.EventObject;
import com.sciforce.robin.graph.util.EventSource;
import com.sciforce.robin.graph.util.UndoableEdit;

/**
 * Implements the selection model for a graph.
 * 
 * This class fires the following events:
 * 
 * Event.UNDO fires after the selection was changed in changeSelection. The
 * <code>edit</code> property contains the UndoableEdit which contains the
 * SelectionChange.
 * 
 * Event.CHANGE fires after the selection changes by executing an
 * SelectionChange. The <code>added</code> and <code>removed</code>
 * properties contain Collections of cells that have been added to or removed
 * from the selection, respectively.
 * 
 * NOTE: Due to a historic bug that cannot be changed at this point the
 * names of the properties are "reversed".
 *  
 * To add a change listener to the graph selection model:
 * 
 * <code>
 * addListener(
 *   Event.CHANGE, new IEventListener()
 *   {
 *     public void invoke(Object sender, EventObject evt)
 *     {
 *       GraphSelectionModel model = (mxSelectionModel) sender;
 *       Collection added = (Collection) evt.getProperty("added");
 *       Collection removed = (Collection) evt.getProperty("removed");
 *       selectionChanged(model, added, removed);
 *     }
 *   });
 * </code>
 */
public class GraphSelectionModel extends EventSource
{

	/**
	 * Reference to the enclosing graph.
	 */
	protected Graph graph;

	/**
	 * Specifies if only one selected item at a time is allowed.
	 * Default is false.
	 */
	protected boolean singleSelection = false;

	/**
	 * Holds the selection cells.
	 */
	protected Set<Object> cells = new LinkedHashSet<Object>();

	/**
	 * Constructs a new selection model for the specified graph.
	 * 
	 * @param graph
	 */
	public GraphSelectionModel(Graph graph)
	{
		this.graph = graph;
	}

	/**
	 * @return the singleSelection
	 */
	public boolean isSingleSelection()
	{
		return singleSelection;
	}

	/**
	 * @param singleSelection the singleSelection to set
	 */
	public void setSingleSelection(boolean singleSelection)
	{
		this.singleSelection = singleSelection;
	}

	/**
	 * Returns true if the given cell is selected.
	 * 
	 * @param cell
	 * @return Returns true if the given cell is selected.
	 */
	public boolean isSelected(Object cell)
	{
		return (cell == null) ? false : cells.contains(cell);
	}

	/**
	 * Returns true if no cells are selected.
	 */
	public boolean isEmpty()
	{
		return cells.isEmpty();
	}

	/**
	 * Returns the number of selected cells.
	 */
	public int size()
	{
		return cells.size();
	}

	/**
	 * Clears the selection.
	 */
	public void clear()
	{
		changeSelection(null, cells);
	}

	/**
	 * Returns the first selected cell.
	 */
	public Object getCell()
	{
		return (cells.isEmpty()) ? null : cells.iterator().next();
	}

	/**
	 * Returns the selection cells.
	 */
	public Object[] getCells()
	{
		return cells.toArray();
	}

	/**
	 * Clears the selection and adds the given cell to the selection.
	 */
	public void setCell(Object cell)
	{
		if (cell != null)
		{
			setCells(new Object[] { cell });
		}
		else
		{
			clear();
		}
	}

	/**
	 * Clears the selection and adds the given cells.
	 */
	public void setCells(Object[] cells)
	{
		if (cells != null)
		{
			if (singleSelection)
			{
				cells = new Object[] { getFirstSelectableCell(cells) };
			}

			List<Object> tmp = new ArrayList<Object>(cells.length);

			for (int i = 0; i < cells.length; i++)
			{
				if (graph.isCellSelectable(cells[i]))
				{
					tmp.add(cells[i]);
				}
			}

			changeSelection(tmp, this.cells);
		}
		else
		{
			clear();
		}
	}

	/**
	 * Returns the first selectable cell in the given array of cells.
	 * 
	 * @param cells Array of cells to return the first selectable cell for.
	 * @return Returns the first cell that may be selected.
	 */
	protected Object getFirstSelectableCell(Object[] cells)
	{
		if (cells != null)
		{
			for (int i = 0; i < cells.length; i++)
			{
				if (graph.isCellSelectable(cells[i]))
				{
					return cells[i];
				}
			}
		}

		return null;
	}

	/**
	 * Adds the given cell to the selection.
	 */
	public void addCell(Object cell)
	{
		if (cell != null)
		{
			addCells(new Object[] { cell });
		}
	}

	/**
	 * 
	 */
	public void addCells(Object[] cells)
	{
		if (cells != null)
		{
			Collection<Object> remove = null;

			if (singleSelection)
			{
				remove = this.cells;
				cells = new Object[] { getFirstSelectableCell(cells) };
			}

			List<Object> tmp = new ArrayList<Object>(cells.length);

			for (int i = 0; i < cells.length; i++)
			{
				if (!isSelected(cells[i]) && graph.isCellSelectable(cells[i]))
				{
					tmp.add(cells[i]);
				}
			}

			changeSelection(tmp, remove);
		}
	}

	/**
	 * Removes the given cell from the selection.
	 */
	public void removeCell(Object cell)
	{
		if (cell != null)
		{
			removeCells(new Object[] { cell });
		}
	}

	/**
	 * 
	 */
	public void removeCells(Object[] cells)
	{
		if (cells != null)
		{
			List<Object> tmp = new ArrayList<Object>(cells.length);

			for (int i = 0; i < cells.length; i++)
			{
				if (isSelected(cells[i]))
				{
					tmp.add(cells[i]);
				}
			}

			changeSelection(null, tmp);
		}
	}

	/**
	 * 
	 */
	protected void changeSelection(Collection<Object> added,
			Collection<Object> removed)
	{
		if ((added != null && !added.isEmpty())
				|| (removed != null && !removed.isEmpty()))
		{
			SelectionChange change = new SelectionChange(this, added,
					removed);
			change.execute();
			UndoableEdit edit = new UndoableEdit(this, false);
			edit.add(change);
			fireEvent(new EventObject(Event.UNDO, "edit", edit));
		}
	}

	/**
	 * 
	 */
	protected void cellAdded(Object cell)
	{
		if (cell != null)
		{
			cells.add(cell);
		}
	}

	/**
	 * 
	 */
	protected void cellRemoved(Object cell)
	{
		if (cell != null)
		{
			cells.remove(cell);
		}
	}

	/**
	 *
	 */
	public static class SelectionChange implements UndoableEdit.UndoableChange
	{

		/**
		 * 
		 */
		protected GraphSelectionModel model;

		/**
		 * 
		 */
		protected Collection<Object> added, removed;

		/**
		 * 
		 * @param model
		 * @param added
		 * @param removed
		 */
		public SelectionChange(GraphSelectionModel model,
							   Collection<Object> added, Collection<Object> removed)
		{
			this.model = model;
			this.added = (added != null) ? new ArrayList<Object>(added) : null;
			this.removed = (removed != null) ? new ArrayList<Object>(removed)
					: null;
		}

		/**
		 * 
		 */
		public void execute()
		{
			if (removed != null)
			{
				Iterator<Object> it = removed.iterator();

				while (it.hasNext())
				{
					model.cellRemoved(it.next());
				}
			}

			if (added != null)
			{
				Iterator<Object> it = added.iterator();

				while (it.hasNext())
				{
					model.cellAdded(it.next());
				}
			}

			Collection<Object> tmp = added;
			added = removed;
			removed = tmp;
			model.fireEvent(new EventObject(Event.CHANGE, "added", added,
					"removed", removed));
		}

	}

}
