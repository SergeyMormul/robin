/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.io;

import java.util.Map;

import org.w3c.dom.Node;

/**
 * Codec for mxChildChanges. This class is created and registered
 * dynamically at load time and used implicitely via Codec
 * and the CodecRegistry.
 */
public class GenericChangeCodec extends ObjectCodec
{
	/**
	 * 
	 */
	protected String fieldname;

	/**
	 * Constructs a new model codec.
	 */
	public GenericChangeCodec(Object template, String fieldname)
	{
		this(template, new String[] { "model", "previous" },
				new String[] { "cell" }, null, fieldname);
	}

	/**
	 * Constructs a new model codec for the given arguments.
	 */
	public GenericChangeCodec(Object template, String[] exclude,
                              String[] idrefs, Map<String, String> mapping, String fieldname)
	{
		super(template, exclude, idrefs, mapping);

		this.fieldname = fieldname;
	}

	/* (non-Javadoc)
	 * @see ObjectCodec#afterDecode(Codec, org.w3c.dom.Node, java.lang.Object)
	 */
	@Override
	public Object afterDecode(Codec dec, Node node, Object obj)
	{
		Object cell = getFieldValue(obj, "cell");

		if (cell instanceof Node)
		{
			setFieldValue(obj, "cell", dec.decodeCell((Node) cell, false));
		}

		setFieldValue(obj, "previous", getFieldValue(obj, fieldname));

		return obj;
	}

}
