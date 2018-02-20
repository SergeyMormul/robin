/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.io;

import java.util.Map;

import com.sciforce.robin.graph.model.GraphModel;
import org.w3c.dom.Node;

/**
 * Codec for mxChildChanges. This class is created and registered
 * dynamically at load time and used implicitely via Codec
 * and the CodecRegistry.
 */
public class TerminalChangeCodec extends ObjectCodec
{

	/**
	 * Constructs a new model codec.
	 */
	public TerminalChangeCodec()
	{
		this(new GraphModel.mxTerminalChange(), new String[] { "model", "previous" },
				new String[] { "cell", "terminal" }, null);
	}

	/**
	 * Constructs a new model codec for the given arguments.
	 */
	public TerminalChangeCodec(Object template, String[] exclude,
                               String[] idrefs, Map<String, String> mapping)
	{
		super(template, exclude, idrefs, mapping);
	}

	/* (non-Javadoc)
	 * @see ObjectCodec#afterDecode(Codec, org.w3c.dom.Node, java.lang.Object)
	 */
	@Override
	public Object afterDecode(Codec dec, Node node, Object obj)
	{
		if (obj instanceof GraphModel.mxTerminalChange)
		{
			GraphModel.mxTerminalChange change = (GraphModel.mxTerminalChange) obj;

			change.setPrevious(change.getTerminal());
		}

		return obj;
	}

}
