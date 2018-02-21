/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.swing.handler;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import com.sciforce.robin.graph.canvas.Graphics2DCanvas;
import com.sciforce.robin.graph.model.ICell;
import com.sciforce.robin.graph.swing.GraphComponent;
import com.sciforce.robin.graph.util.*;
import com.sciforce.robin.graph.view.CellState;
import com.sciforce.robin.graph.view.Graph;
import com.sciforce.robin.graph.model.Geometry;
import com.sciforce.robin.graph.util.EventObject;

/**
 * Connection handler creates new connections between cells. This control is used to display the connector
 * icon, while the preview is used to draw the line.
 */
public class ConnectPreview extends EventSource
{
	/**
	 * 
	 */
	protected GraphComponent graphComponent;

	/**
	 * 
	 */
	protected CellState previewState;

	/**
	 * 
	 */
	protected CellState sourceState;

	/**
	 * 
	 */
	protected Point startPoint;

	/**
	 * 
	 * @param graphComponent
	 */
	public ConnectPreview(GraphComponent graphComponent)
	{
		this.graphComponent = graphComponent;

		// Installs the paint handler
		graphComponent.addListener(Event.AFTER_PAINT, new mxIEventListener()
		{
			public void invoke(Object sender, EventObject evt)
			{
				Graphics g = (Graphics) evt.getProperty("g");
				paint(g);
			}
		});
	}

	/**
	 * Creates a new instance of mxShape for previewing the edge.
	 */
	protected Object createCell(CellState startState, String style)
	{
		Graph graph = graphComponent.getGraph();
		ICell cell = ((ICell) graph
				.createEdge(null, null, "",
						(startState != null) ? startState.getCell() : null,
						null, style));
		((ICell) startState.getCell()).insertEdge(cell, true);

		return cell;
	}
	
	/**
	 * 
	 */
	public boolean isActive()
	{
		return sourceState != null;
	}

	/**
	 * 
	 */
	public CellState getSourceState()
	{
		return sourceState;
	}

	/**
	 * 
	 */
	public CellState getPreviewState()
	{
		return previewState;
	}

	/**
	 * 
	 */
	public Point getStartPoint()
	{
		return startPoint;
	}

	/**
	 * Updates the style of the edge preview from the incoming edge
	 */
	public void start(MouseEvent e, CellState startState, String style)
	{
		Graph graph = graphComponent.getGraph();
		sourceState = startState;
		startPoint = transformScreenPoint(startState.getCenterX(),
				startState.getCenterY());
		Object cell = createCell(startState, style);
		graph.getView().validateCell(cell);
		previewState = graph.getView().getState(cell);
		
		fireEvent(new EventObject(Event.START, "event", e, "state",
				previewState));
	}

	/**
	 * 
	 */
	public void update(MouseEvent e, CellState targetState, double x, double y)
	{
		Graph graph = graphComponent.getGraph();
		ICell cell = (ICell) previewState.getCell();

		Rectangle dirty = graphComponent.getGraph().getPaintBounds(
				new Object[] { previewState.getCell() });

		if (cell.getTerminal(false) != null)
		{
			cell.getTerminal(false).removeEdge(cell, false);
		}

		if (targetState != null)
		{
			((ICell) targetState.getCell()).insertEdge(cell, false);
		}

		Geometry geo = graph.getCellGeometry(previewState.getCell());

		geo.setTerminalPoint(startPoint, true);
		geo.setTerminalPoint(transformScreenPoint(x, y), false);

		revalidate(previewState);
		fireEvent(new EventObject(Event.CONTINUE, "event", e, "x", x, "y",
				y));

		// Repaints the dirty region
		// TODO: Cache the new dirty region for next repaint
		java.awt.Rectangle tmp = getDirtyRect(dirty);

		if (tmp != null)
		{
			graphComponent.getGraphControl().repaint(tmp);
		}
		else
		{
			graphComponent.getGraphControl().repaint();
		}
	}

	/**
	 * 
	 */
	protected java.awt.Rectangle getDirtyRect()
	{
		return getDirtyRect(null);
	}

	/**
	 * 
	 */
	protected java.awt.Rectangle getDirtyRect(Rectangle dirty)
	{
		if (previewState != null)
		{
			Rectangle tmp = graphComponent.getGraph().getPaintBounds(
					new Object[] { previewState.getCell() });

			if (dirty != null)
			{
				dirty.add(tmp);
			}
			else
			{
				dirty = tmp;
			}

			if (dirty != null)
			{
				// TODO: Take arrow size into account
				dirty.grow(2);

				return dirty.getRectangle();
			}
		}

		return null;
	}

	/**
	 * 
	 */
	protected Point transformScreenPoint(double x, double y)
	{
		Graph graph = graphComponent.getGraph();
		Point tr = graph.getView().getTranslate();
		double scale = graph.getView().getScale();

		return new Point(graph.snap(x / scale - tr.getX()), graph.snap(y
				/ scale - tr.getY()));
	}

	/**
	 * 
	 */
	public void revalidate(CellState state)
	{
		state.getView().invalidate(state.getCell());
		state.getView().validateCellState(state.getCell());
	}

	/**
	 * 
	 */
	public void paint(Graphics g)
	{
		if (previewState != null)
		{
			Graphics2DCanvas canvas = graphComponent.getCanvas();

			if (graphComponent.isAntiAlias())
			{
				Utils.setAntiAlias((Graphics2D) g, true, false);
			}

			float alpha = graphComponent.getPreviewAlpha();

			if (alpha < 1)
			{
				((Graphics2D) g).setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, alpha));
			}

			Graphics2D previousGraphics = canvas.getGraphics();
			Point previousTranslate = canvas.getTranslate();
			double previousScale = canvas.getScale();

			try
			{
				canvas.setScale(graphComponent.getGraph().getView().getScale());
				canvas.setTranslate(0, 0);
				canvas.setGraphics((Graphics2D) g);

				paintPreview(canvas);
			}
			finally
			{
				canvas.setScale(previousScale);
				canvas.setTranslate(previousTranslate.getX(), previousTranslate.getY());
				canvas.setGraphics(previousGraphics);
			}
		}
	}

	/**
	 * Draws the preview using the graphics canvas.
	 */
	protected void paintPreview(Graphics2DCanvas canvas)
	{
		graphComponent.getGraphControl().drawCell(graphComponent.getCanvas(),
				previewState.getCell());
	}

	/**
	 *
	 */
	public Object stop(boolean commit)
	{
		return stop(commit, null);
	}

	/**
	 *
	 */
	public Object stop(boolean commit, MouseEvent e)
	{
		Object result = (sourceState != null) ? sourceState.getCell() : null;

		if (previewState != null)
		{
			Graph graph = graphComponent.getGraph();

			graph.getModel().beginUpdate();
			try
			{
				ICell cell = (ICell) previewState.getCell();
				Object src = cell.getTerminal(true);
				Object trg = cell.getTerminal(false);

				if (src != null)
				{
					((ICell) src).removeEdge(cell, true);
				}

				if (trg != null)
				{
					((ICell) trg).removeEdge(cell, false);
				}

				if (commit)
				{
					result = graph.addCell(cell, null, null, src, trg);
				}

				fireEvent(new EventObject(Event.STOP, "event", e, "commit",
						commit, "cell", (commit) ? result : null));

				// Clears the state before the model commits
				if (previewState != null)
				{
					java.awt.Rectangle dirty = getDirtyRect();
					graph.getView().clear(cell, false, true);
					previewState = null;

					if (!commit && dirty != null)
					{
						graphComponent.getGraphControl().repaint(dirty);
					}
				}
			}
			finally
			{
				graph.getModel().endUpdate();
			}
		}

		sourceState = null;
		startPoint = null;

		return result;
	}

}
