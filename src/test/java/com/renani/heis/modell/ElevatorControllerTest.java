package com.renani.heis.modell;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import static org.apache.logging.log4j.LogManager.getLogger;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ElevatorControllerTest {
	private static Logger LOG = getLogger(ElevatorControllerTest.class);

	@Test
	void testClickingOnButton() throws InterruptedException {
		ArrayList<Button> buttons = this.createFloorsAndButtons();
		Elevator el = new Elevator(5);
		Floor currentFloor = buttons.get(0).getFloor();
		ElevatorController ec = new ElevatorController(el, buttons, currentFloor);
		// Second click on the button
		final int nextFloor_1 = 2;
		final int startIndex_1 = 3;

		// third click on the button
		final int nextFloor_2 = buttons.size() - 1;
		final int startIndex_2 = 0;

		double click_1 = ec.calculateHeight(startIndex_1, nextFloor_1);
		double click_2 = ec.calculateHeight(startIndex_2, nextFloor_2);

		int nrOfTics_1 = (int) Math.ceil(el.estimateExpectedTicks(click_1));
		int nrOfTics_2 = (int) Math.ceil(el.estimateExpectedTicks(click_2));
		int totalTicks = nrOfTics_1 + nrOfTics_2;
		LOG.info("nrOfTics " + nrOfTics_1 + ": " + nrOfTics_2);
		CountDownLatch latch = new CountDownLatch(totalTicks);

		el.registerElevatorStatusListener(new ElevatorStatusListener() {
			int count = 2;

			@Override
			public void HandleStatusHasChanged(ElevatorStatus elevatorStatus) {
				LOG.info("COUNT NR " + count + "Status " + elevatorStatus + " latch count left " + latch.getCount());
				if (elevatorStatus.available.equals(elevatorStatus)) {

					if (count == 2) {
						count--;
						// SECOND CLICK

						Direction direction = ec.registerButtonClick(buttons.get(nextFloor_1), latch);
						assertEquals(Direction.DOWN, direction);

					} else if (count == 1) {
						count--;
						// THIRD CLICK
						Direction direction = ec.registerButtonClick(buttons.get(nextFloor_2), latch);
						assertEquals(Direction.UP, direction);

					}

				}
			}

		});

		// FIRST CLICK
		Direction direction = ec.registerButtonClick(buttons.get(3));
		assertEquals(Direction.UP, direction);
		latch.await();

	}

	public ArrayList<Button> createFloorsAndButtons() {
		ArrayList<Button> orderedButton = new ArrayList<Button>();
		orderedButton.add(new Button("G", new Floor("Garasje", 10)));// 10
		orderedButton.add(new Button("K", new Floor("Kjeller", 5)));// 15
		orderedButton.add(new Button("L", new Floor("Lobby", 10)));// 25
		orderedButton.add(new Button("1", new Floor("Første", 3)));// 28
		orderedButton.add(new Button("2", new Floor("Andre", 3)));// 31
		orderedButton.add(new Button("3", new Floor("Tredje", 3)));// 34
		orderedButton.add(new Button("4", new Floor("Fjerde", 3)));// 37
		orderedButton.add(new Button("5", new Floor("Femte", 3)));// 40
		orderedButton.add(new Button("T", new Floor("Taket", 3)));// 43
		return orderedButton;
	}

}
