/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sciforce.robin.graph.model.GraphModel;

/**
 * Singleton class that acts as a global registry for codecs. See
 * {@link Codec} for an example.
 */
public class CodecRegistry
{

	private static final Logger log = Logger.getLogger(CodecRegistry.class.getName());

	/**
	 * Maps from constructor names to codecs.
	 */
	protected static Hashtable<String, ObjectCodec> codecs = new Hashtable<String, ObjectCodec>();

	/**
	 * Maps from classnames to codecnames.
	 */
	protected static Hashtable<String, String> aliases = new Hashtable<String, String>();

	/**
	 * Holds the list of known packages. Packages are used to prefix short
	 * class names (eg. Cell) in XML markup.
	 */
	protected static List<String> packages = new ArrayList<String>();

	// Registers the known codecs and package names
	static
	{
		addPackage("com.sciforce.robin.mx.mxgraph");
		addPackage("com.sciforce.robin.mx.mxgraph.util");
		addPackage("com.sciforce.robin.mx.mxgraph.model");
		addPackage("com.sciforce.robin.mx.mxgraph.view");
		addPackage("java.lang");
		addPackage("java.util");

		register(new ObjectCodec(new ArrayList<Object>()));
		register(new ModelCodec());
		register(new CellCodec());
		register(new StylesheetCodec());

		register(new RootChangeCodec());
		register(new ChildChangeCodec());
		register(new TerminalChangeCodec());
		register(new GenericChangeCodec(new GraphModel.mxValueChange(), "value"));
		register(new GenericChangeCodec(new GraphModel.mxStyleChange(), "style"));
		register(new GenericChangeCodec(new GraphModel.mxGeometryChange(), "geometry"));
		register(new GenericChangeCodec(new GraphModel.mxCollapseChange(), "collapsed"));
		register(new GenericChangeCodec(new GraphModel.mxVisibleChange(), "visible"));
	}

	/**
	 * Registers a new codec and associates the name of the template constructor
	 * in the codec with the codec object. Automatically creates an alias if the
	 * codename and the classname are not equal.
	 */
	public static ObjectCodec register(ObjectCodec codec)
	{
		if (codec != null)
		{
			String name = codec.getName();
			codecs.put(name, codec);

			String classname = getName(codec.getTemplate());

			if (!classname.equals(name))
			{
				addAlias(classname, name);
			}
		}

		return codec;
	}

	/**
	 * Adds an alias for mapping a classname to a codecname.
	 */
	public static void addAlias(String classname, String codecname)
	{
		aliases.put(classname, codecname);
	}

	/**
	 * Returns a codec that handles the given object, which can be an object
	 * instance or an XML node.
	 * 
	 * @param name Java class name.
	 */
	public static ObjectCodec getCodec(String name)
	{
		String tmp = aliases.get(name);

		if (tmp != null)
		{
			name = tmp;
		}

		ObjectCodec codec = codecs.get(name);

		// Registers a new default codec for the given name
		// if no codec has been previously defined.
		if (codec == null)
		{
			Object instance = getInstanceForName(name);

			if (instance != null)
			{
				try
				{
					codec = new ObjectCodec(instance);
					register(codec);
				}
				catch (Exception e)
				{
					log.log(Level.SEVERE, "Failed to create and register a codec for the name: " + name, e);
				}
			}
			else
			{
				log.severe("Failed to create codec for " + name);
			}
		}

		return codec;
	}

	/**
	 * Adds the given package name to the list of known package names.
	 * 
	 * @param packagename Name of the package to be added.
	 */
	public static void addPackage(String packagename)
	{
		packages.add(packagename);
	}

	/**
	 * Creates and returns a new instance for the given class name.
	 * 
	 * @param name Name of the class to be instantiated.
	 * @return Returns a new instance of the given class.
	 */
	public static Object getInstanceForName(String name)
	{
		Class<?> clazz = getClassForName(name);

		if (clazz != null)
		{
			if (clazz.isEnum())
			{
				// For an enum, use the first constant as the default instance
				return clazz.getEnumConstants()[0];
			}
			else
			{
				try
				{
					return clazz.newInstance();
				}
				catch (Exception e)
				{
					log.log(Level.SEVERE, "Failed to construct class instance for " + name, e);
				}
			}
		}

		log.severe("Failed to construct instance for " + name);
		return null;
	}

	/**
	 * Returns a class that corresponds to the given name.
	 * 
	 * @param name
	 * @return Returns the class for the given name.
	 */
	public static Class<?> getClassForName(String name)
	{
		try
		{
			return Class.forName(name);
		}
		catch (Exception e)
		{
			log.log(Level.FINEST, "Failed to get a class object for " + name, e);
		}

		for (int i = 0; i < packages.size(); i++)
		{
			String s = packages.get(i);
			String nameWithPackage = s + "." + name;

			try
			{
				return Class.forName(nameWithPackage);
			}
			catch (Exception e)
			{
				log.log(Level.FINEST, "Failed to get a class object for " + nameWithPackage, e);
			}
		}

		log.severe("Class " + name + " not found");
		return null;
	}

	/**
	 * Returns the name that identifies the codec associated
	 * with the given instance..
	 *
	 * The I/O system uses unqualified classnames, eg. for a
	 * <code>Cell</code> this returns
	 * <code>Cell</code>.
	 * 
	 * @param instance Instance whose node name should be returned.
	 * @return Returns a string that identifies the codec.
	 */
	public static String getName(Object instance)
	{
		Class<? extends Object> type = instance.getClass();

		if (type.isArray() || Collection.class.isAssignableFrom(type)
				|| Map.class.isAssignableFrom(type))
		{
			return "Array";
		}
		else
		{
			if (packages.contains(type.getPackage().getName()))
			{
				return type.getSimpleName();
			}
			else
			{
				return type.getName();
			}
		}
	}

}
