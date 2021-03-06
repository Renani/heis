package com.renani.heis.modell;

import java.util.ArrayList;

public class ElevatorController {
	ArrayList<Button> orderedButton = new ArrayList<Button>();

	public ElevatorController(Elevator elevator) {
		orderedButton.add(new Button("G", new Floor("Garasje", 10)));
		orderedButton.add(new Button("K", new Floor("Kjeller", 5)));
		orderedButton.add(new Button("L", new Floor("Lobby", 10)));
		orderedButton.add(new Button("1", new Floor("Første", 3)));
		orderedButton.add(new Button("2", new Floor("Andre", 3)));
		orderedButton.add(new Button("3", new Floor("Tredje", 3)));
		orderedButton.add(new Button("4", new Floor("Fjerde", 3)));
		orderedButton.add(new Button("5", new Floor("Femte", 3)));
		orderedButton.add(new Button("T", new Floor("Taket", 3)));
	}

}
