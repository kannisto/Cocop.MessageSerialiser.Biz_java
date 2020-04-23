//
// Please make sure to read and understand the files README.md and LICENSE.txt.
// 
// This file was prepared in the research project COCOP (Coordinating
// Optimisation of Complex Industrial Processes).
// https://cocop-spire.eu/
//
// Author: Petri Kannisto, Tampere University, Finland
// File created: 2/2018
// Last modified: 4/2020

package eu.cocop.messageserialiser.biz;

/**
 * Thrown when a message-related error has been encountered.
 * @author Petri Kannisto
 */
public final class InvalidMessageException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor.
	 * @param msg Error message.
	 */
	InvalidMessageException(String msg)
	{
		super(msg);
		
		// Otherwise, empty ctor body
	}
	
	/**
	 * Constructor.
	 * @param msg Error message.
	 * @param inner Inner exception.
	 */
	InvalidMessageException(String msg, Exception inner)
	{
		super(msg, inner);
		
		// Otherwise, empty ctor body
	}
}
