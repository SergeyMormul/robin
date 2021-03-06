/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements an undo history.
 * 
 * This class fires the following events:
 * 
 * Event.CLEAR fires after clear was executed. The event has no properties.
 * 
 * Event.UNDO fires afer a significant edit was undone in undo. The
 * <code>edit</code> property contains the UndoableEdit that was undone.
 * 
 * Event.REDO fires afer a significant edit was redone in redo. The
 * <code>edit</code> property contains the UndoableEdit that was redone.
 * 
 * Event.ADD fires after an undoable edit was added to the history. The
 * <code>edit</code> property contains the UndoableEdit that was added.
 */
public class UndoManager extends EventSource
{

	/**
	 * Maximum command history size. 0 means unlimited history. Default is 100.
	 */
	protected int size;

	/**
	 * List that contains the steps of the command history.
	 */
	protected List<UndoableEdit> history;

	/**
	 * Index of the element to be added next.
	 */
	protected int indexOfNextAdd;

	/**
	 * Constructs a new undo manager with a default history size.
	 */
	public UndoManager()
	{
		this(100);
	}

	/**
	 * Constructs a new undo manager for the specified size.
	 */
	public UndoManager(int size)
	{
		this.size = size;
		clear();
	}

	/**
	 * 
	 */
	public boolean isEmpty()
	{
		return history.isEmpty();
	}

	/**
	 * Clears the command history.
	 */
	public void clear()
	{
		history = new ArrayList<UndoableEdit>(size);
		indexOfNextAdd = 0;
		fireEvent(new EventObject(Event.CLEAR));
	}

	/**
	 * Returns true if an undo is possible.
	 */
	public boolean canUndo()
	{
		return indexOfNextAdd > 0;
	}

	/**
	 * Undoes the last change.
	 */
	public void undo()
	{
		while (indexOfNextAdd > 0)
		{
			UndoableEdit edit = history.get(--indexOfNextAdd);
			edit.undo();

			if (edit.isSignificant())
			{
				fireEvent(new EventObject(Event.UNDO, "edit", edit));
				break;
			}
		}
	}

	/**
	 * Returns true if a redo is possible.
	 */
	public boolean canRedo()
	{
		return indexOfNextAdd < history.size();
	}

	/**
	 * Redoes the last change.
	 */
	public void redo()
	{
		int n = history.size();

		while (indexOfNextAdd < n)
		{
			UndoableEdit edit = history.get(indexOfNextAdd++);
			edit.redo();

			if (edit.isSignificant())
			{
				fireEvent(new EventObject(Event.REDO, "edit", edit));
				break;
			}
		}
	}

	/**
	 * Method to be called to add new undoable edits to the history.
	 */
	public void undoableEditHappened(UndoableEdit undoableEdit)
	{
		trim();

		if (size > 0 && size == history.size())
		{
			history.remove(0);
		}

		history.add(undoableEdit);
		indexOfNextAdd = history.size();
		fireEvent(new EventObject(Event.ADD, "edit", undoableEdit));
	}

	/**
	 * Removes all pending steps after indexOfNextAdd from the history,
	 * invoking die on each edit. This is called from undoableEditHappened.
	 */
	protected void trim()
	{
		while (history.size() > indexOfNextAdd)
		{
			UndoableEdit edit = history
					.remove(indexOfNextAdd);
			edit.die();
		}
	}

}
