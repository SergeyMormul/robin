/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.swing.view;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;

import com.sciforce.robin.graph.canvas.Graphics2DCanvas;
import com.sciforce.robin.graph.shape.BasicShape;
import com.sciforce.robin.graph.shape.IShape;
import com.sciforce.robin.graph.swing.GraphComponent;
import com.sciforce.robin.graph.util.Constants;
import com.sciforce.robin.graph.util.Point;
import com.sciforce.robin.graph.util.Utils;
import com.sciforce.robin.graph.view.CellState;

public class InteractiveCanvas extends Graphics2DCanvas
{
	/**
	 * 
	 */
	protected ImageObserver imageObserver = null;

	/**
	 * 
	 */
	public InteractiveCanvas()
	{
		this(null);
	}

	/**
	 * 
	 */
	public InteractiveCanvas(ImageObserver imageObserver)
	{
		setImageObserver(imageObserver);
	}

	/**
	 * 
	 */
	public void setImageObserver(ImageObserver value)
	{
		imageObserver = value;
	}

	/**
	 * 
	 */
	public ImageObserver getImageObserver()
	{
		return imageObserver;
	}

	/**
	 * Overrides graphics call to use image observer.
	 */
	protected void drawImageImpl(Image image, int x, int y)
	{
		g.drawImage(image, x, y, imageObserver);
	}

	/**
	 * Returns the size for the given image.
	 */
	protected Dimension getImageSize(Image image)
	{
		return new Dimension(image.getWidth(imageObserver),
				image.getHeight(imageObserver));
	}

	/**
	 * 
	 */
	public boolean contains(GraphComponent graphComponent, Rectangle rect,
							CellState state)
	{
		return state != null && state.getX() >= rect.x
				&& state.getY() >= rect.y
				&& state.getX() + state.getWidth() <= rect.x + rect.width
				&& state.getY() + state.getHeight() <= rect.y + rect.height;
	}

	/**
	 * 
	 */
	public boolean intersects(GraphComponent graphComponent, Rectangle rect,
							  CellState state)
	{
		if (state != null)
		{
			// Checks if the label intersects
			if (state.getLabelBounds() != null
					&& state.getLabelBounds().getRectangle().intersects(rect))
			{
				return true;
			}

			int pointCount = state.getAbsolutePointCount();

			// Checks if the segments of the edge intersect
			if (pointCount > 0)
			{
				rect = (Rectangle) rect.clone();
				int tolerance = graphComponent.getTolerance();
				rect.grow(tolerance, tolerance);

				Shape realShape = null;

				// FIXME: Check if this should be used for all shapes
				if (Utils.getString(state.getStyle(),
						Constants.STYLE_SHAPE, "").equals(
						Constants.SHAPE_ARROW))
				{
					IShape shape = getShape(state.getStyle());

					if (shape instanceof BasicShape)
					{
						realShape = ((BasicShape) shape).createShape(this,
								state);
					}
				}

				if (realShape != null && realShape.intersects(rect))
				{
					return true;
				}
				else
				{
					Point p0 = state.getAbsolutePoint(0);

					for (int i = 0; i < pointCount; i++)
					{
						Point p1 = state.getAbsolutePoint(i);

						if (rect.intersectsLine(p0.getX(), p0.getY(),
								p1.getX(), p1.getY()))
						{
							return true;
						}

						p0 = p1;
					}
				}
			}
			else
			{
				// Checks if the bounds of the shape intersect
				return state.getRectangle().intersects(rect);
			}
		}

		return false;
	}

	/**
	 * Returns true if the given point is inside the content area of the given
	 * swimlane. (The content area of swimlanes is transparent to events.) This
	 * implementation does not check if the given state is a swimlane, it is
	 * assumed that the caller has checked this before using this method.
	 */
	public boolean hitSwimlaneContent(GraphComponent graphComponent,
                                      CellState swimlane, int x, int y)
	{
		if (swimlane != null)
		{
			int start = (int) Math.max(2, Math.round(Utils.getInt(
					swimlane.getStyle(), Constants.STYLE_STARTSIZE,
					Constants.DEFAULT_STARTSIZE)
					* graphComponent.getGraph().getView().getScale()));
			Rectangle rect = swimlane.getRectangle();

			if (Utils.isTrue(swimlane.getStyle(),
					Constants.STYLE_HORIZONTAL, true))
			{
				rect.y += start;
				rect.height -= start;
			}
			else
			{
				rect.x += start;
				rect.width -= start;
			}

			return rect.contains(x, y);
		}

		return false;
	}

}
