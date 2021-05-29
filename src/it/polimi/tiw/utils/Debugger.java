package it.polimi.tiw.utils;

public class Debugger {

	private static Boolean debugmode = true;
	
	
	public static void log(String msg)
	{
		if (debugmode) 
		{
			System.out.println("[DEBUGGER] " + msg);
		}
	}


	public static void log(int msg) {
		if (debugmode) 
		{
			System.out.println("[DEBUGGER] " + msg);
		}
		
	}
	
}
