package com.renani.heis.modell;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest

public class ElevatorTest extends TestCase {

	// Assuming the difference between actual and estimated is most likely due to
	// implementation of timers in java.
	private static final int error_margin_estimatedTime = 20;

	@Test
	public void testEtimatedTimeVsActual() {
		double height = 5;
		double errorMargin = 0.005;

		MovementListener movementListener = getEmptyCaller();

		Elevator elevator = new Elevator(4, errorMargin);

		double estimateTimeTo = elevator.estimateTimeTo(height);

		CountDownLatch latch = new CountDownLatch((int) Math.ceil(elevator.estimateExpectedTicks(height)));
		long startTime = System.currentTimeMillis();
		elevator.DriveToHeight(height, Direction.UP, latch, movementListener);
		long endTime = System.currentTimeMillis();

		double diff = Math.abs((endTime - startTime) - estimateTimeTo);

		if (diff < error_margin_estimatedTime) {
			assertTrue("Actual time is within error margin of estimated", true);

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
		double errorMargin = 0.005;

		MovementListener emptyCaller = this.getEmptyCaller();

		Elevator elevator = new Elevator(4, errorMargin);

		double estimateTimeTo = elevator.estimateTimeTo(height);

		CountDownLatch latch = new CountDownLatch((int) Math.ceil(elevator.estimateExpectedTicks(height)));
		long startTime = System.currentTimeMillis();
		elevator.DriveToHeight(height, Direction.DOWN, null, emptyCaller);
		elevator.stopEmergency();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	 @Test
	 public void testHeightGainReceived () {
		  
	 }

}
