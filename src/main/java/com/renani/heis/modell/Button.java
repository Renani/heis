package com.renani.heis.modell;

import static org.apache.logging.log4j.LogManager.getLogger;

import org.apache.logging.log4j.Logger;

public class Button {

	private String name;
	private Floor floor;
	private static Logger LOG = getLogger(Button.class);
	
	public Button(String name, Floor floor) {
		   
		this.name = name;
		this.setFloor(floor);

	}

	public Floor getFloor() {
		return floor;
	}

	public void setFloor(Floor floor) {
		this.floor = floor;
	}
}
