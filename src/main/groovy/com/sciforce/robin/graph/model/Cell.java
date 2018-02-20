/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Cells are the elements of the graph model. They represent the state
 * of the groups, vertices and edges in a graph.
 *
 * <h4>Edge Labels</h4>
 * 
 * Using the x- and y-coordinates of a cell's geometry it is
 * possible to position the label on edges on a specific location
 * on the actual edge shape as it appears on the screen. The
 * x-coordinate of an edge's geometry is used to describe the
 * distance from the center of the edge from -1 to 1 with 0
 * being the center of the edge and the default value. The
 * y-coordinate of an edge's geometry is used to describe
 * the absolute, orthogonal distance in pixels from that
 * point. In addition, the Geometry.offset is used
 * as a absolute offset vector from the resulting point.
 * 
 * The width and height of an edge geometry are ignored.
 * 
 * To add more than one edge label, add a child vertex with
 * a relative geometry. The x- and y-coordinates of that
 * geometry will have the same semantiv as the above for
 * edge labels.
 */
public class Cell implements ICell, Cloneable, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 910211337632342672L;

	/**
	 * Holds the Id. Default is null.
	 */
	protected String id;

	/**
	 * Holds the user object. Default is null.
	 */
	protected Object value;

	/**
	 * Holds the geometry. Default is null.
	 */
	protected Geometry geometry;

	/**
	 * Holds the style as a string of the form
	 * stylename[;key=value]. Default is null.
	 */
	protected String style;

	/**
	 * Specifies whether the cell is a vertex or edge and whether it is
	 * connectable, visible and collapsed. Default values are false, false,
	 * true, true and false respectively.
	 */
	protected boolean vertex = false, edge = false, connectable = true,
			visible = true, collapsed = false;

	/**
	 * Reference to the parent cell and source and target terminals for edges.
	 */
	protected ICell parent, source, target;

	/**
	 * Holds the child cells and connected edges.
	 */
	protected List<Object> children, edges;

	/**
	 * Constructs a new cell with an empty user object.
	 */
	public Cell()
	{
		this(null);
	}

	/**
	 * Constructs a new cell for the given user object.
	 * 
	 * @param value
	 *   Object that represents the value of the cell.
	 */
	public Cell(Object value)
	{
		this(value, null, null);
	}

	/**
	 * Constructs a new cell for the given parameters.
	 * 
	 * @param value Object that represents the value of the cell.
	 * @param geometry Specifies the geometry of the cell.
	 * @param style Specifies the style as a formatted string.
	 */
	public Cell(Object value, Geometry geometry, String style)
	{
		setValue(value);
		setGeometry(geometry);
		setStyle(style);
	}

	/* (non-Javadoc)
	 * @see ICell#getId()
	 */
	public String getId()
	{
		return id;
	}

	/* (non-Javadoc)
	 * @see ICell#setId(String)
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see ICell#getValue()
	 */
	public Object getValue()
	{
		return value;
	}

	/* (non-Javadoc)
	 * @see ICell#setValue(Object)
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see ICell#getGeometry()
	 */
	public Geometry getGeometry()
	{
		return geometry;
	}

	/* (non-Javadoc)
	 * @see ICell#setGeometry(Geometry)
	 */
	public void setGeometry(Geometry geometry)
	{
		this.geometry = geometry;
	}

	/* (non-Javadoc)
	 * @see ICell#getStyle()
	 */
	public String getStyle()
	{
		return style;
	}

	/* (non-Javadoc)
	 * @see ICell#setStyle(String)
	 */
	public void setStyle(String style)
	{
		this.style = style;
	}

	/* (non-Javadoc)
	 * @see ICell#isVertex()
	 */
	public boolean isVertex()
	{
		return vertex;
	}

	/* (non-Javadoc)
	 * @see ICell#setVertex(boolean)
	 */
	public void setVertex(boolean vertex)
	{
		this.vertex = vertex;
	}

	/* (non-Javadoc)
	 * @see ICell#isEdge()
	 */
	public boolean isEdge()
	{
		return edge;
	}

	/* (non-Javadoc)
	 * @see ICell#setEdge(boolean)
	 */
	public void setEdge(boolean edge)
	{
		this.edge = edge;
	}

	/* (non-Javadoc)
	 * @see ICell#isConnectable()
	 */
	public boolean isConnectable()
	{
		return connectable;
	}

	/* (non-Javadoc)
	 * @see ICell#setConnectable(boolean)
	 */
	public void setConnectable(boolean connectable)
	{
		this.connectable = connectable;
	}

	/* (non-Javadoc)
	 * @see ICell#isVisible()
	 */
	public boolean isVisible()
	{
		return visible;
	}

	/* (non-Javadoc)
	 * @see ICell#setVisible(boolean)
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	/* (non-Javadoc)
	 * @see ICell#isCollapsed()
	 */
	public boolean isCollapsed()
	{
		return collapsed;
	}

	/* (non-Javadoc)
	 * @see ICell#setCollapsed(boolean)
	 */
	public void setCollapsed(boolean collapsed)
	{
		this.collapsed = collapsed;
	}

	/* (non-Javadoc)
	 * @see ICell#getParent()
	 */
	public ICell getParent()
	{
		return parent;
	}

	/* (non-Javadoc)
	 * @see ICell#setParent(ICell)
	 */
	public void setParent(ICell parent)
	{
		this.parent = parent;
	}

	/**
	 * Returns the source terminal.
	 */
	public ICell getSource()
	{
		return source;
	}

	/**
	 * Sets the source terminal.
	 * 
	 * @param source Cell that represents the new source terminal.
	 */
	public void setSource(ICell source)
	{
		this.source = source;
	}

	/**
	 * Returns the target terminal.
	 */
	public ICell getTarget()
	{
		return target;
	}

	/**
	 * Sets the target terminal.
	 * 
	 * @param target Cell that represents the new target terminal.
	 */
	public void setTarget(ICell target)
	{
		this.target = target;
	}

	/* (non-Javadoc)
	 * @see ICell#getTerminal(boolean)
	 */
	public ICell getTerminal(boolean source)
	{
		return (source) ? getSource() : getTarget();
	}

	/* (non-Javadoc)
	 * @see ICell#setTerminal(ICell, boolean)
	 */
	public ICell setTerminal(ICell terminal, boolean isSource)
	{
		if (isSource)
		{
			setSource(terminal);
		}
		else
		{
			setTarget(terminal);
		}

		return terminal;
	}

	/* (non-Javadoc)
	 * @see ICell#getChildCount()
	 */
	public int getChildCount()
	{
		return (children != null) ? children.size() : 0;
	}

	/* (non-Javadoc)
	 * @see ICell#getIndex(ICell)
	 */
	public int getIndex(ICell child)
	{
		return (children != null) ? children.indexOf(child) : -1;
	}

	/* (non-Javadoc)
	 * @see ICell#getChildAt(int)
	 */
	public ICell getChildAt(int index)
	{
		return (children != null) ? (ICell) children.get(index) : null;
	}

	/* (non-Javadoc)
	 * @see ICell#insert(ICell)
	 */
	public ICell insert(ICell child)
	{
		int index = getChildCount();
		
		if (child.getParent() == this)
		{
			index--;
		}
		
		return insert(child, index);
	}

	/* (non-Javadoc)
	 * @see ICell#insert(ICell, int)
	 */
	public ICell insert(ICell child, int index)
	{
		if (child != null)
		{
			child.removeFromParent();
			child.setParent(this);

			if (children == null)
			{
				children = new ArrayList<Object>();
				children.add(child);
			}
			else
			{
				children.add(index, child);
			}
		}

		return child;
	}

	/* (non-Javadoc)
	 * @see ICell#remove(int)
	 */
	public ICell remove(int index)
	{
		ICell child = null;

		if (children != null && index >= 0)
		{
			child = getChildAt(index);
			remove(child);
		}

		return child;
	}

	/* (non-Javadoc)
	 * @see ICell#remove(ICell)
	 */
	public ICell remove(ICell child)
	{
		if (child != null && children != null)
		{
			children.remove(child);
			child.setParent(null);
		}

		return child;
	}

	/* (non-Javadoc)
	 * @see ICell#removeFromParent()
	 */
	public void removeFromParent()
	{
		if (parent != null)
		{
			parent.remove(this);
		}
	}

	/* (non-Javadoc)
	 * @see ICell#getEdgeCount()
	 */
	public int getEdgeCount()
	{
		return (edges != null) ? edges.size() : 0;
	}

	/* (non-Javadoc)
	 * @see ICell#getEdgeIndex(ICell)
	 */
	public int getEdgeIndex(ICell edge)
	{
		return (edges != null) ? edges.indexOf(edge) : -1;
	}

	/* (non-Javadoc)
	 * @see ICell#getEdgeAt(int)
	 */
	public ICell getEdgeAt(int index)
	{
		return (edges != null) ? (ICell) edges.get(index) : null;
	}

	/* (non-Javadoc)
	 * @see ICell#insertEdge(ICell, boolean)
	 */
	public ICell insertEdge(ICell edge, boolean isOutgoing)
	{
		if (edge != null)
		{
			edge.removeFromTerminal(isOutgoing);
			edge.setTerminal(this, isOutgoing);

			if (edges == null || edge.getTerminal(!isOutgoing) != this
					|| !edges.contains(edge))
			{
				if (edges == null)
				{
					edges = new ArrayList<Object>();
				}

				edges.add(edge);
			}
		}

		return edge;
	}

	/* (non-Javadoc)
	 * @see ICell#removeEdge(ICell, boolean)
	 */
	public ICell removeEdge(ICell edge, boolean isOutgoing)
	{
		if (edge != null)
		{
			if (edge.getTerminal(!isOutgoing) != this && edges != null)
			{
				edges.remove(edge);
			}
			
			edge.setTerminal(null, isOutgoing);
		}

		return edge;
	}

	/* (non-Javadoc)
	 * @see ICell#removeFromTerminal(boolean)
	 */
	public void removeFromTerminal(boolean isSource)
	{
		ICell terminal = getTerminal(isSource);

		if (terminal != null)
		{
			terminal.removeEdge(this, isSource);
		}
	}

	/**
	 * Returns the specified attribute from the user object if it is an XML
	 * node.
	 * 
	 * @param name Name of the attribute whose value should be returned.
	 * @return Returns the value of the given attribute or null.
	 */
	public String getAttribute(String name)
	{
		return getAttribute(name, null);
	}

	/**
	 * Returns the specified attribute from the user object if it is an XML
	 * node.
	 * 
	 * @param name Name of the attribute whose value should be returned.
	 * @param defaultValue Default value to use if the attribute has no value.
	 * @return Returns the value of the given attribute or defaultValue.
	 */
	public String getAttribute(String name, String defaultValue)
	{
		Object userObject = getValue();
		String val = null;

		if (userObject instanceof Element)
		{
			Element element = (Element) userObject;
			val = element.getAttribute(name);
		}

		if (val == null)
		{
			val = defaultValue;
		}

		return val;
	}

	/**
	 * Sets the specified attribute on the user object if it is an XML node.
	 * 
	 * @param name Name of the attribute whose value should be set.
	 * @param value New value of the attribute.
	 */
	public void setAttribute(String name, String value)
	{
		Object userObject = getValue();

		if (userObject instanceof Element)
		{
			Element element = (Element) userObject;
			element.setAttribute(name, value);
		}
	}

	/**
	 * Returns a clone of the cell.
	 */
	public Object clone() throws CloneNotSupportedException
	{
		Cell clone = (Cell) super.clone();

		clone.setValue(cloneValue());
		clone.setStyle(getStyle());
		clone.setCollapsed(isCollapsed());
		clone.setConnectable(isConnectable());
		clone.setEdge(isEdge());
		clone.setVertex(isVertex());
		clone.setVisible(isVisible());
		clone.setParent(null);
		clone.setSource(null);
		clone.setTarget(null);
		clone.children = null;
		clone.edges = null;

		Geometry geometry = getGeometry();

		if (geometry != null)
		{
			clone.setGeometry((Geometry) geometry.clone());
		}

		return clone;
	}

	/**
	 * Returns a clone of the user object. This implementation clones any XML
	 * nodes or otherwise returns the same user object instance.
	 */
	protected Object cloneValue()
	{
		Object value = getValue();

		if (value instanceof Node)
		{
			value = ((Node) value).cloneNode(true);
		}

		return value;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(64);
		builder.append(getClass().getSimpleName());
		builder.append(" [");
		builder.append("id=");
		builder.append(id);
		builder.append(", value=");
		builder.append(value);
		builder.append(", geometry=");
		builder.append(geometry);
		builder.append("]");
		
		return builder.toString();
	}

}
