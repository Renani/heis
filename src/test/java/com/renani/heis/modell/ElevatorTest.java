package com.renani.heis.modell;



import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;



@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ElevatorTest {

	// Assuming the difference between actual and estimated is most likely due to
	// implementation of timers in java.
	private static final int error_margin_estimatedTime = 20;

	@Test
	public void testEtimatedTimeVsActual() {
		double height = 5;
		int speed = 4;

		MovementListener movementListener = getEmptyCaller();

		Elevator elevator = new Elevator(speed);

		double estimateTimeTo = elevator.estimateTimeTo(height);

		CountDownLatch latch = new CountDownLatch((int) Math.ceil(elevator.estimateExpectedTicks(height)));
		long startTime = System.currentTimeMillis();
		elevator.DriveToHeight(height, Direction.UP, latch, movementListener);
		long endTime = System.currentTimeMillis();

		double diff = Math.abs((endTime - startTime) - estimateTimeTo);

		if (diff < error_margin_estimatedTime) {
			assertTrue(true, "Actual time is within error margin of estimated");
			

		}
	}

	private MovementListener getEmptyCaller() {
		MovementListener movementListener = new MovementListener() {

			@Override
			public void registerMovementStopped(double gain) {
				// TODO Auto-generated method stub

			}

		};
		return movementListener;
	}

	@Test
	public void testEmergencyStop() {
		double height = 5;
		double speed = 4;

		MovementListener emptyCaller = this.getEmptyCaller();

		Elevator elevator = new Elevator(speed);

		double estimateTimeTo = elevator.estimateTimeTo(height);

		long startTime = System.currentTimeMillis();
		elevator.DriveToHeight(height, Direction.DOWN, null, emptyCaller);
		elevator.stopEmergency();

		// TODO: Finn en bedre måte å gjøre dette på
		makeStupidWaitFunction();

	}

	private void makeStupidWaitFunction() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testHeightGainReceived() {
		double height = 5;
		double speed = 4;

		MovementListener movementListener = new MovementListener() {

			@Override
			public void registerMovementStopped(double gain) {
				assertTrue(true, "We have registered gain at end of movement " + gain);
			}
		};

		Elevator elevator = new Elevator(speed);
		double estimateTimeTo = elevator.estimateTimeTo(height);
		CountDownLatch latch = null;
		elevator.DriveToHeight(height, Direction.UP, latch, movementListener);
		elevator.stopEmergency();

		makeStupidWaitFunction();
	}

}
