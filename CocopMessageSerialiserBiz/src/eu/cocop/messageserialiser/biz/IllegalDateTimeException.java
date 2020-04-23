//
// Please make sure to read and understand the files README.md and LICENSE.txt.
// 
// This file was prepared in the research project COCOP (Coordinating
// Optimisation of Complex Industrial Processes).
// https://cocop-spire.eu/
//
// Author: Petri Kannisto, Tampere University, Finland
// File created: 4/2020
// Last modified: 4/2020

package eu.cocop.messageserialiser.biz;

/**
 * Thrown when an illegal date-time value is received as an argument.
 * @author Petri Kannisto
 */
public class IllegalDateTimeException extends IllegalArgumentException
{
	private static final long serialVersionUID = 2172955108460341650L;

	
	/**
	 * Constructor.
	 * @param msg Related message.
	 */
	IllegalDateTimeException(String msg)
	{
		super(msg);
		
		// Otherwise, empty ctor body
	}
}
