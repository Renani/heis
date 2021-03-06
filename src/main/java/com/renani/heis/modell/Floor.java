package com.renani.heis.modell;

import static org.apache.logging.log4j.LogManager.getLogger;

import org.apache.logging.log4j.Logger;

public class Floor {
	
	private static Logger LOG = getLogger(Elevator.class);
	
	//TODO: Use javax-measure or similar instead
	//Meters
	
	private String name;
	public Floor (String name, double height ) {
		this.name = name;
	}
	
	public String toString () {
		return name;
	}

}
