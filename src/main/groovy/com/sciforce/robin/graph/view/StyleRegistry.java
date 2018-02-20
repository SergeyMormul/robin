/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.view;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.sciforce.robin.graph.util.mxConstants;

/**
 * Singleton class that acts as a global converter from string to object values
 * in a style. This is currently only used to perimeters and edge styles.
 */
public class StyleRegistry
{

	/**
	 * Maps from strings to objects.
	 */
	protected static Map<String, Object> values = new Hashtable<String, Object>();

	// Registers the known object styles
	static
	{
		putValue(mxConstants.EDGESTYLE_ELBOW, EdgeStyle.ElbowConnector);
		putValue(mxConstants.EDGESTYLE_ENTITY_RELATION,
				EdgeStyle.EntityRelation);
		putValue(mxConstants.EDGESTYLE_LOOP, EdgeStyle.Loop);
		putValue(mxConstants.EDGESTYLE_SIDETOSIDE, EdgeStyle.SideToSide);
		putValue(mxConstants.EDGESTYLE_TOPTOBOTTOM, EdgeStyle.TopToBottom);
		putValue(mxConstants.EDGESTYLE_ORTHOGONAL, EdgeStyle.OrthConnector);
		putValue(mxConstants.EDGESTYLE_SEGMENT, EdgeStyle.SegmentConnector);

		putValue(mxConstants.PERIMETER_ELLIPSE, Perimeter.EllipsePerimeter);
		putValue(mxConstants.PERIMETER_RECTANGLE,
				Perimeter.RectanglePerimeter);
		putValue(mxConstants.PERIMETER_RHOMBUS, Perimeter.RhombusPerimeter);
		putValue(mxConstants.PERIMETER_TRIANGLE, Perimeter.TrianglePerimeter);
		putValue(mxConstants.PERIMETER_HEXAGON, Perimeter.HexagonPerimeter);
	}

	/**
	 * Puts the given object into the registry under the given name.
	 */
	public static void putValue(String name, Object value)
	{
		values.put(name, value);
	}

	/**
	 * Returns the value associated with the given name.
	 */
	public static Object getValue(String name)
	{
		return values.get(name);
	}

	/**
	 * Returns the name for the given value.
	 */
	public static String getName(Object value)
	{
		Iterator<Map.Entry<String, Object>> it = values.entrySet().iterator();

		while (it.hasNext())
		{
			Map.Entry<String, Object> entry = it.next();

			if (entry.getValue() == value)
			{
				return entry.getKey();
			}
		}

		return null;
	}

}
