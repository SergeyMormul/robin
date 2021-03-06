/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.swing.handler;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.sciforce.robin.graph.model.Geometry;
import com.sciforce.robin.graph.model.IGraphModel;
import com.sciforce.robin.graph.swing.GraphComponent;
import com.sciforce.robin.graph.swing.util.MouseAdapter;
import com.sciforce.robin.graph.util.*;
import com.sciforce.robin.graph.util.Constants;
import com.sciforce.robin.graph.view.CellState;
import com.sciforce.robin.graph.view.Graph;
import com.sciforce.robin.graph.view.GraphView;

/**
 * Connection handler creates new connections between cells. This control is used to display the connector
 * icon, while the preview is used to draw the line.
 * 
 * Event.CONNECT fires between begin- and endUpdate in mouseReleased. The <code>cell</code>
 * property contains the inserted edge, the <code>event</code> and <code>target</code> 
 * properties contain the respective arguments that were passed to mouseReleased.
 */
public class ConnectionHandler extends MouseAdapter
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2543899557644889853L;

	/**
	 * 
	 */
	public static Cursor CONNECT_CURSOR = new Cursor(Cursor.HAND_CURSOR);

	/**
	 * 
	 */
	protected GraphComponent graphComponent;

	/**
	 * Holds the event source.
	 */
	protected EventSource eventSource = new EventSource(this);

	/**
	 * 
	 */
	protected ConnectPreview connectPreview;

	/**
	 * Specifies the icon to be used for creating new connections. If this is
	 * specified then it is used instead of the handle. Default is null.
	 */
	protected ImageIcon connectIcon = null;

	/**
	 * Specifies the size of the handle to be used for creating new
	 * connections. Default is Constants.CONNECT_HANDLE_SIZE.
	 */
	protected int handleSize = Constants.CONNECT_HANDLE_SIZE;

	/**
	 * Specifies if a handle should be used for creating new connections. This
	 * is only used if no connectIcon is specified. If this is false, then the
	 * source cell will be highlighted when the mouse is over the hotspot given
	 * in the marker. Default is Constants.CONNECT_HANDLE_ENABLED.
	 */
	protected boolean handleEnabled = Constants.CONNECT_HANDLE_ENABLED;

	/**
	 * 
	 */
	protected boolean select = true;

	/**
	 * Specifies if the source should be cloned and used as a target if no
	 * target was selected. Default is false.
	 */
	protected boolean createTarget = false;

	/**
	 * Appearance and event handling order wrt subhandles.
	 */
	protected boolean keepOnTop = true;

	/**
	 * 
	 */
	protected boolean enabled = true;

	/**
	 * 
	 */
	protected transient java.awt.Point first;

	/**
	 * 
	 */
	protected transient boolean active = false;

	/**
	 * 
	 */
	protected transient java.awt.Rectangle bounds;

	/**
	 * 
	 */
	protected transient CellState source;

	/**
	 * 
	 */
	protected transient CellMarker marker;

	/**
	 * 
	 */
	protected transient String error;

	/**
	 * 
	 */
	protected transient EventSource.IEventListener resetHandler = new EventSource.IEventListener()
	{
		public void invoke(Object source, EventObject evt)
		{
			reset();
		}
	};

	/**
	 * 
	 * @param graphComponent
	 */
	public ConnectionHandler(GraphComponent graphComponent)
	{
		this.graphComponent = graphComponent;

		// Installs the paint handler
		graphComponent.addListener(Event.AFTER_PAINT, new EventSource.IEventListener()
		{
			public void invoke(Object sender, EventObject evt)
			{
				Graphics g = (Graphics) evt.getProperty("g");
				paint(g);
			}
		});

		connectPreview = createConnectPreview();

		GraphComponent.GraphControl graphControl = graphComponent.getGraphControl();
		graphControl.addMouseListener(this);
		graphControl.addMouseMotionListener(this);

		// Installs the graph listeners and keeps them in sync
		addGraphListeners(graphComponent.getGraph());

		graphComponent.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt.getPropertyName().equals("graph"))
				{
					removeGraphListeners((Graph) evt.getOldValue());
					addGraphListeners((Graph) evt.getNewValue());
				}
			}
		});

		marker = new CellMarker(graphComponent)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 103433247310526381L;

			// Overrides to return cell at location only if valid (so that
			// there is no highlight for invalid cells that have no error
			// message when the mouse is released)
			protected Object getCell(MouseEvent e)
			{
				Object cell = super.getCell(e);

				if (isConnecting())
				{
					if (source != null)
					{
						error = validateConnection(source.getCell(), cell);

						if (error != null && error.length() == 0)
						{
							cell = null;

							// Enables create target inside groups
							if (createTarget)
							{
								error = null;
							}
						}
					}
				}
				else if (!isValidSource(cell))
				{
					cell = null;
				}

				return cell;
			}

			// Sets the highlight color according to isValidConnection
			protected boolean isValidState(CellState state)
			{
				if (isConnecting())
				{
					return error == null;
				}
				else
				{
					return super.isValidState(state);
				}
			}

			// Overrides to use marker color only in highlight mode or for
			// target selection
			protected Color getMarkerColor(MouseEvent e, CellState state,
					boolean isValid)
			{
				return (isHighlighting() || isConnecting()) ? super
						.getMarkerColor(e, state, isValid) : null;
			}

			// Overrides to use hotspot only for source selection otherwise
			// intersects always returns true when over a cell
			protected boolean intersects(CellState state, MouseEvent e)
			{
				if (!isHighlighting() || isConnecting())
				{
					return true;
				}

				return super.intersects(state, e);
			}
		};

		marker.setHotspotEnabled(true);
	}

	/**
	 * Installs the listeners to update the handles after any changes.
	 */
	protected void addGraphListeners(Graph graph)
	{
		// LATER: Install change listener for graph model, view
		if (graph != null)
		{
			GraphView view = graph.getView();
			view.addListener(Event.SCALE, resetHandler);
			view.addListener(Event.TRANSLATE, resetHandler);
			view.addListener(Event.SCALE_AND_TRANSLATE, resetHandler);

			graph.getModel().addListener(Event.CHANGE, resetHandler);
		}
	}

	/**
	 * Removes all installed listeners.
	 */
	protected void removeGraphListeners(Graph graph)
	{
		if (graph != null)
		{
			GraphView view = graph.getView();
			view.removeListener(resetHandler, Event.SCALE);
			view.removeListener(resetHandler, Event.TRANSLATE);
			view.removeListener(resetHandler, Event.SCALE_AND_TRANSLATE);

			graph.getModel().removeListener(resetHandler, Event.CHANGE);
		}
	}

	/**
	 * 
	 */
	protected ConnectPreview createConnectPreview()
	{
		return new ConnectPreview(graphComponent);
	}

	/**
	 * 
	 */
	public ConnectPreview getConnectPreview()
	{
		return connectPreview;
	}

	/**
	 * 
	 */
	public void setConnectPreview(ConnectPreview value)
	{
		connectPreview = value;
	}

	/**
	 * Returns true if the source terminal has been clicked and a new
	 * connection is currently being previewed.
	 */
	public boolean isConnecting()
	{
		return connectPreview.isActive();
	}

	/**
	 * 
	 */
	public boolean isActive()
	{
		return active;
	}
	
	/**
	 * Returns true if no connectIcon is specified and handleEnabled is false.
	 */
	public boolean isHighlighting()
	{
		return connectIcon == null && !handleEnabled;
	}

	/**
	 * 
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * 
	 */
	public void setEnabled(boolean value)
	{
		enabled = value;
	}

	/**
	 * 
	 */
	public boolean isKeepOnTop()
	{
		return keepOnTop;
	}

	/**
	 * 
	 */
	public void setKeepOnTop(boolean value)
	{
		keepOnTop = value;
	}

	/**
	 * 
	 */
	public void setConnectIcon(ImageIcon value)
	{
		connectIcon = value;
	}

	/**
	 * 
	 */
	public ImageIcon getConnecIcon()
	{
		return connectIcon;
	}

	/**
	 * 
	 */
	public void setHandleEnabled(boolean value)
	{
		handleEnabled = value;
	}

	/**
	 * 
	 */
	public boolean isHandleEnabled()
	{
		return handleEnabled;
	}

	/**
	 * 
	 */
	public void setHandleSize(int value)
	{
		handleSize = value;
	}

	/**
	 * 
	 */
	public int getHandleSize()
	{
		return handleSize;
	}

	/**
	 * 
	 */
	public CellMarker getMarker()
	{
		return marker;
	}

	/**
	 * 
	 */
	public void setMarker(CellMarker value)
	{
		marker = value;
	}

	/**
	 * 
	 */
	public void setCreateTarget(boolean value)
	{
		createTarget = value;
	}

	/**
	 * 
	 */
	public boolean isCreateTarget()
	{
		return createTarget;
	}

	/**
	 * 
	 */
	public void setSelect(boolean value)
	{
		select = value;
	}

	/**
	 * 
	 */
	public boolean isSelect()
	{
		return select;
	}

	/**
	 * 
	 */
	public void reset()
	{
		connectPreview.stop(false);
		setBounds(null);
		marker.reset();
		active = false;
		source = null;
		first = null;
		error = null;
	}

	/**
	 * 
	 */
	public Object createTargetVertex(MouseEvent e, Object source)
	{
		Graph graph = graphComponent.getGraph();
		Object clone = graph.cloneCells(new Object[] { source })[0];
		IGraphModel model = graph.getModel();
		Geometry geo = model.getGeometry(clone);

		if (geo != null)
		{
			Point point = graphComponent.getPointForEvent(e);
			geo.setX(graph.snap(point.getX() - geo.getWidth() / 2));
			geo.setY(graph.snap(point.getY() - geo.getHeight() / 2));
		}

		return clone;
	}

	/**
	 * 
	 */
	public boolean isValidSource(Object cell)
	{
		return graphComponent.getGraph().isValidSource(cell);
	}

	/**
	 * Returns true. The call to Graph.isValidTarget is implicit by calling
	 * Graph.getEdgeValidationError in validateConnection. This is an
	 * additional hook for disabling certain targets in this specific handler.
	 */
	public boolean isValidTarget(Object cell)
	{
		return true;
	}

	/**
	 * Returns the error message or an empty string if the connection for the
	 * given source target pair is not valid. Otherwise it returns null.
	 */
	public String validateConnection(Object source, Object target)
	{
		if (target == null && createTarget)
		{
			return null;
		}

		if (!isValidTarget(target))
		{
			return "";
		}

		return graphComponent.getGraph().getEdgeValidationError(
				connectPreview.getPreviewState().getCell(), source, target);
	}

	/**
	 * 
	 */
	public void mousePressed(MouseEvent e)
	{
		if (!graphComponent.isForceMarqueeEvent(e)
				&& !graphComponent.isPanningEvent(e)
				&& !e.isPopupTrigger()
				&& graphComponent.isEnabled()
				&& isEnabled()
				&& !e.isConsumed()
				&& ((isHighlighting() && marker.hasValidState()) || (!isHighlighting()
						&& bounds != null && bounds.contains(e.getPoint()))))
		{
			start(e, marker.getValidState());
			e.consume();
		}
	}

	/**
	 * 
	 */
	public void start(MouseEvent e, CellState state)
	{
		first = e.getPoint();
		connectPreview.start(e, state, "");
	}

	/**
	 * 
	 */
	public void mouseMoved(MouseEvent e)
	{
		mouseDragged(e);

		if (isHighlighting() && !marker.hasValidState())
		{
			source = null;
		}

		if (!isHighlighting() && source != null)
		{
			int imgWidth = handleSize;
			int imgHeight = handleSize;

			if (connectIcon != null)
			{
				imgWidth = connectIcon.getIconWidth();
				imgHeight = connectIcon.getIconHeight();
			}

			int x = (int) source.getCenterX() - imgWidth / 2;
			int y = (int) source.getCenterY() - imgHeight / 2;

			if (graphComponent.getGraph().isSwimlane(source.getCell()))
			{
				Rectangle size = graphComponent.getGraph().getStartSize(
						source.getCell());

				if (size.getWidth() > 0)
				{
					x = (int) (source.getX() + size.getWidth() / 2 - imgWidth / 2);
				}
				else
				{
					y = (int) (source.getY() + size.getHeight() / 2 - imgHeight / 2);
				}
			}

			setBounds(new java.awt.Rectangle(x, y, imgWidth, imgHeight));
		}
		else
		{
			setBounds(null);
		}

		if (source != null && (bounds == null || bounds.contains(e.getPoint())))
		{
			graphComponent.getGraphControl().setCursor(CONNECT_CURSOR);
			e.consume();
		}
	}

	/**
	 * 
	 */
	public void mouseDragged(MouseEvent e)
	{
		if (!e.isConsumed() && graphComponent.isEnabled() && isEnabled())
		{
			// Activates the handler
			if (!active && first != null)
			{
				double dx = Math.abs(first.getX() - e.getX());
				double dy = Math.abs(first.getY() - e.getY());
				int tol = graphComponent.getTolerance();
				
				if (dx > tol || dy > tol)
				{
					active = true;
				}
			}
			
			if (e.getButton() == 0 || (isActive() && connectPreview.isActive()))
			{
				CellState state = marker.process(e);
	
				if (connectPreview.isActive())
				{
					connectPreview.update(e, marker.getValidState(), e.getX(),
							e.getY());
					setBounds(null);
					e.consume();
				}
				else
				{
					source = state;
				}
			}
		}
	}

	/**
	 * 
	 */
	public void mouseReleased(MouseEvent e)
	{
		if (isActive())
		{
			if (error != null)
			{
				if (error.length() > 0)
				{
					JOptionPane.showMessageDialog(graphComponent, error);
				}
			}
			else if (first != null)
			{
				Graph graph = graphComponent.getGraph();
				double dx = first.getX() - e.getX();
				double dy = first.getY() - e.getY();
	
				if (connectPreview.isActive()
						&& (marker.hasValidState() || isCreateTarget() || graph
								.isAllowDanglingEdges()))
				{
					graph.getModel().beginUpdate();
	
					try
					{
						Object dropTarget = null;
	
						if (!marker.hasValidState() && isCreateTarget())
						{
							Object vertex = createTargetVertex(e, source.getCell());
							dropTarget = graph.getDropTarget(
									new Object[] { vertex }, e.getPoint(),
									graphComponent.getCellAt(e.getX(), e.getY()));
	
							if (vertex != null)
							{
								// Disables edges as drop targets if the target cell was created
								if (dropTarget == null
										|| !graph.getModel().isEdge(dropTarget))
								{
									CellState pstate = graph.getView().getState(
											dropTarget);
	
									if (pstate != null)
									{
										Geometry geo = graph.getModel()
												.getGeometry(vertex);
	
										Point origin = pstate.getOrigin();
										geo.setX(geo.getX() - origin.getX());
										geo.setY(geo.getY() - origin.getY());
									}
								}
								else
								{
									dropTarget = graph.getDefaultParent();
								}
	
								graph.addCells(new Object[] { vertex }, dropTarget);
							}
	
							// FIXME: Here we pre-create the state for the vertex to be
							// inserted in order to invoke update in the connectPreview.
							// This means we have a cell state which should be created
							// after the model.update, so this should be fixed.
							CellState targetState = graph.getView().getState(
									vertex, true);
							connectPreview.update(e, targetState, e.getX(),
									e.getY());
						}
	
						Object cell = connectPreview.stop(
								graphComponent.isSignificant(dx, dy), e);
	
						if (cell != null)
						{
							graphComponent.getGraph().setSelectionCell(cell);
							eventSource.fireEvent(new EventObject(
									Event.CONNECT, "cell", cell, "event", e,
									"target", dropTarget));
						}
	
						e.consume();
					}
					finally
					{
						graph.getModel().endUpdate();
					}
				}
			}
		}

		reset();
	}

	/**
	 * 
	 */
	public void setBounds(java.awt.Rectangle value)
	{
		if ((bounds == null && value != null)
				|| (bounds != null && value == null)
				|| (bounds != null && value != null && !bounds.equals(value)))
		{
			java.awt.Rectangle tmp = bounds;

			if (tmp != null)
			{
				if (value != null)
				{
					tmp.add(value);
				}
			}
			else
			{
				tmp = value;
			}

			bounds = value;

			if (tmp != null)
			{
				graphComponent.getGraphControl().repaint(tmp);
			}
		}
	}

	/**
	 * Adds the given event listener.
	 */
	public void addListener(String eventName, EventSource.IEventListener listener)
	{
		eventSource.addListener(eventName, listener);
	}

	/**
	 * Removes the given event listener.
	 */
	public void removeListener(EventSource.IEventListener listener)
	{
		eventSource.removeListener(listener);
	}

	/**
	 * Removes the given event listener for the specified event name.
	 */
	public void removeListener(EventSource.IEventListener listener, String eventName)
	{
		eventSource.removeListener(listener, eventName);
	}

	/**
	 * 
	 */
	public void paint(Graphics g)
	{
		if (bounds != null)
		{
			if (connectIcon != null)
			{
				g.drawImage(connectIcon.getImage(), bounds.x, bounds.y,
						bounds.width, bounds.height, null);
			}
			else if (handleEnabled)
			{
				g.setColor(Color.BLACK);
				g.draw3DRect(bounds.x, bounds.y, bounds.width - 1,
						bounds.height - 1, true);
				g.setColor(Color.GREEN);
				g.fill3DRect(bounds.x + 1, bounds.y + 1, bounds.width - 2,
						bounds.height - 2, true);
				g.setColor(Color.BLUE);
				g.drawRect(bounds.x + bounds.width / 2 - 1, bounds.y
						+ bounds.height / 2 - 1, 1, 1);
			}
		}
	}

}
