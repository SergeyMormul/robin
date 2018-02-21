/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.io;

import java.util.Map;

import com.sciforce.robin.graph.model.GraphModel;
import com.sciforce.robin.graph.model.ICell;
import org.w3c.dom.Node;

/**
 * Codec for mxChildChanges. This class is created and registered
 * dynamically at load time and used implicitly via Codec
 * and the CodecRegistry.
 */
public class RootChangeCodec extends ObjectCodec
{

	/**
	 * Constructs a new model codec.
	 */
	public RootChangeCodec()
	{
		this(new GraphModel.RrootChange(), new String[] { "model", "previous", "root" },
				null, null);
	}

	/**
	 * Constructs a new model codec for the given arguments.
	 */
	public RootChangeCodec(Object template, String[] exclude,
                           String[] idrefs, Map<String, String> mapping)
	{
		super(template, exclude, idrefs, mapping);
	}

	/* (non-Javadoc)
	 * @see ObjectCodec#afterEncode(Codec, java.lang.Object, org.w3c.dom.Node)
	 */
	@Override
	public Node afterEncode(Codec enc, Object obj, Node node)
	{
		if (obj instanceof GraphModel.RrootChange)
		{
			enc.encodeCell((ICell) ((GraphModel.RrootChange) obj).getRoot(), node, true);
		}

		return node;
	}

	/**
	 * Reads the cells into the graph model. All cells are children of the root
	 * element in the node.
	 */
	public Node beforeDecode(Codec dec, Node node, Object into)
	{
		if (into instanceof GraphModel.RrootChange)
		{
			GraphModel.RrootChange change = (GraphModel.RrootChange) into;

			if (node.getFirstChild() != null
					&& node.getFirstChild().getNodeType() == Node.ELEMENT_NODE)
			{
				// Makes sure the original node isn't modified
				node = node.cloneNode(true);

				Node tmp = node.getFirstChild();
				change.setRoot(dec.decodeCell(tmp, false));

				Node tmp2 = tmp.getNextSibling();
				tmp.getParentNode().removeChild(tmp);
				tmp = tmp2;

				while (tmp != null)
				{
					tmp2 = tmp.getNextSibling();

					if (tmp.getNodeType() == Node.ELEMENT_NODE)
					{
						dec.decodeCell(tmp, true);
					}

					tmp.getParentNode().removeChild(tmp);
					tmp = tmp2;
				}
			}
		}

		return node;
	}

	/* (non-Javadoc)
	 * @see ObjectCodec#afterDecode(Codec, org.w3c.dom.Node, java.lang.Object)
	 */
	@Override
	public Object afterDecode(Codec dec, Node node, Object obj)
	{
		if (obj instanceof GraphModel.RrootChange)
		{
			GraphModel.RrootChange change = (GraphModel.RrootChange) obj;
			change.setPrevious(change.getRoot());
		}

		return obj;
	}

}
