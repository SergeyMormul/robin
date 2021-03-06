/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.shape;

import java.awt.RenderingHints;
import java.util.List;
import java.util.Map;

import com.sciforce.robin.graph.canvas.Graphics2DCanvas;
import com.sciforce.robin.graph.util.Constants;
import com.sciforce.robin.graph.util.Curve;
import com.sciforce.robin.graph.util.Line;
import com.sciforce.robin.graph.util.Point;
import com.sciforce.robin.graph.view.CellState;

public class CurveShape extends ConnectorShape
{
	/**
	 * Cache of the points between which drawing straight lines views as a
	 * curve
	 */
	protected Curve curve;

	/**
	 * 
	 */
	public CurveShape()
	{
		this(new Curve());
	}
	
	/**
	 * 
	 */
	public CurveShape(Curve curve)
	{
		this.curve = curve;
	}

	/**
	 * 
	 */
	public Curve getCurve()
	{
		return curve;
	}

	/**
	 * 
	 */
	public void paintShape(Graphics2DCanvas canvas, CellState state)
	{
		Object keyStrokeHint = canvas.getGraphics().getRenderingHint(
				RenderingHints.KEY_STROKE_CONTROL);
		canvas.getGraphics().setRenderingHint(
				RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);

		super.paintShape(canvas, state);

		canvas.getGraphics().setRenderingHint(
				RenderingHints.KEY_STROKE_CONTROL, keyStrokeHint);
	}

	/**
	 * 
	 */
	protected void paintPolyline(Graphics2DCanvas canvas,
								 List<Point> points, Map<String, Object> style)
	{
		double scale = canvas.getScale();
		validateCurve(points, scale, style);

		canvas.paintPolyline(curve.getCurvePoints(Curve.CORE_CURVE), false);
	}

	/**
	 * Forces underlying curve to a valid state
	 * @param points
	 */
	public void validateCurve(List<Point> points, double scale,
							  Map<String, Object> style)
	{
		if (curve == null)
		{
			curve = new Curve(points);
		}
		else
		{
			curve.updateCurve(points);
		}

		curve.setLabelBuffer(scale * Constants.DEFAULT_LABEL_BUFFER);
	}

	/**
	 * Hook to override creation of the vector that the marker is drawn along
	 * since it may not be the same as the vector between any two control
	 * points
	 * @param points the guide points of the connector
	 * @param source whether the marker is at the source end
	 * @param markerSize the scaled maximum length of the marker
	 * @return a line describing the vector the marker should be drawn along
	 */
	protected Line getMarkerVector(List<Point> points, boolean source,
								   double markerSize)
	{
		double curveLength = curve.getCurveLength(Curve.CORE_CURVE);
		double markerRatio = markerSize / curveLength;
		if (markerRatio >= 1.0)
		{
			markerRatio = 1.0;
		}

		if (source)
		{
			Line sourceVector = curve.getCurveParallel(Curve.CORE_CURVE,
					markerRatio);
			return new Line(sourceVector.getX(), sourceVector.getY(),
					points.get(0));
		}
		else
		{
			Line targetVector = curve.getCurveParallel(Curve.CORE_CURVE,
					1.0 - markerRatio);
			int pointCount = points.size();
			return new Line(targetVector.getX(), targetVector.getY(),
					points.get(pointCount - 1));
		}
	}
}
