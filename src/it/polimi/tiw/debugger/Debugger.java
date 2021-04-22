package it.polimi.tiw.debugger;

public class Debugger {

	private static Boolean debugmode = true;
	
	
	public static void log(String msg)
	{
		if (debugmode) 
		{
			System.out.println("[DEBUGGER] " + msg);
		}
	}
	
}
