/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.shape;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.util.Map;

import com.sciforce.robin.graph.canvas.Graphics2DCanvas;
import com.sciforce.robin.graph.util.Constants;
import com.sciforce.robin.graph.util.Rectangle;
import com.sciforce.robin.graph.util.Utils;
import com.sciforce.robin.graph.view.CellState;

public class BasicShape implements IShape
{

	/**
	 * 
	 */
	public void paintShape(Graphics2DCanvas canvas, CellState state)
	{
		Shape shape = createShape(canvas, state);

		if (shape != null)
		{
			// Paints the background
			if (configureGraphics(canvas, state, true))
			{
				canvas.fillShape(shape, hasShadow(canvas, state));
			}

			// Paints the foreground
			if (configureGraphics(canvas, state, false))
			{
				canvas.getGraphics().draw(shape);
			}
		}
	}

	/**
	 * 
	 */
	public Shape createShape(Graphics2DCanvas canvas, CellState state)
	{
		return null;
	}

	/**
	 * Configures the graphics object ready to paint.
	 * @param canvas the canvas to be painted to
	 * @param state the state of cell to be painted
	 * @param background whether or not this is the background stage of 
	 * 			the shape paint
	 * @return whether or not the shape is ready to be drawn
	 */
	protected boolean configureGraphics(Graphics2DCanvas canvas,
                                        CellState state, boolean background)
	{
		Map<String, Object> style = state.getStyle();

		if (background)
		{
			// Paints the background of the shape
			Paint fillPaint = hasGradient(canvas, state) ? canvas
					.createFillPaint(getGradientBounds(canvas, state), style)
					: null;

			if (fillPaint != null)
			{
				canvas.getGraphics().setPaint(fillPaint);

				return true;
			}
			else
			{
				Color color = getFillColor(canvas, state);
				canvas.getGraphics().setColor(color);

				return color != null;
			}
		}
		else
		{
			canvas.getGraphics().setPaint(null);
			Color color = getStrokeColor(canvas, state);
			canvas.getGraphics().setColor(color);
			canvas.getGraphics().setStroke(canvas.createStroke(style));

			return color != null;
		}
	}

	/**
	 * 
	 */
	protected Rectangle getGradientBounds(Graphics2DCanvas canvas,
                                          CellState state)
	{
		return state;
	}

	/**
	 * 
	 */
	public boolean hasGradient(Graphics2DCanvas canvas, CellState state)
	{
		return true;
	}

	/**
	 * 
	 */
	public boolean hasShadow(Graphics2DCanvas canvas, CellState state)
	{
		return Utils
				.isTrue(state.getStyle(), Constants.STYLE_SHADOW, false);
	}

	/**
	 * 
	 */
	public Color getFillColor(Graphics2DCanvas canvas, CellState state)
	{
		return Utils.getColor(state.getStyle(), Constants.STYLE_FILLCOLOR);
	}

	/**
	 * 
	 */
	public Color getStrokeColor(Graphics2DCanvas canvas, CellState state)
	{
		return Utils
				.getColor(state.getStyle(), Constants.STYLE_STROKECOLOR);
	}

}
