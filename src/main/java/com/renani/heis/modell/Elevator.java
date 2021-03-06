package com.renani.heis.modell;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.util.Timer;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.Logger;

public class Elevator {
	private static Logger LOG = getLogger(Elevator.class);
	// having it as a field for status info
	private double speed;
	private ElevatorStaus currentStatus;

	private Timer elevatorDriveTimer;
	int timerDelay = 10;
	final int LOWEST_POSSIBLE_TIMERDELAY = 5;
	private double speedPrTick;
	double errorMargin = 0.05;
	private ElevatorTimerTask elevatorTimerTask;

	//
	// TODO: Use javax-measure or similar instead
	/***
	 * 
	 * @param speed       given in m/s
	 * @param errorMargin lowest possible
	 */

	public Elevator(double speed, double errorMargin) {
		this.speed = speed;
		// timerDelay = some function of speed
		try {
			timerDelay = (int) Math.floor(errorMargin * (speed * 1000));
			LOG.debug("TimerDelay " + timerDelay);
			if (timerDelay < LOWEST_POSSIBLE_TIMERDELAY) {
				throw new IllegalArgumentException(
						"This elevator cannot support this speed with the error margin given. Decrease speed or increase errormargin");
			}
			this.speedPrTick = errorMargin;

		} catch (Exception e) {
			LOG.debug("Possible timerDelay is set to 0", e);
		}

	}

	/***
	 * Drives the elevator in a given direction.
	 * 
	 * @param height    Nr of meters you want the elevator to drive
	 * @param direction The direction you want the elevator to drive in
	 * @param latch     This is mainly for JUnit but it could be interesting
	 *                  security feature
	 */
	public void DriveToHeight(final double height, final Direction direction, final CountDownLatch latch,
			MovementListener movementListener) {
		LOG.info("Requested to run in direction " + direction);

		elevatorTimerTask = new ElevatorTimerTask(this, height, speedPrTick, movementListener, latch);
		elevatorDriveTimer = new Timer();
		elevatorDriveTimer.scheduleAtFixedRate(elevatorTimerTask, 0, timerDelay);

		if (latch != null) {
			try {
				latch.await();
			} catch (InterruptedException e) {
				LOG.debug("Concurrency issue with Junit issue ", e);

			}
		}

	}

	public double estimateExpectedTicks(final double height) {
		return height / speedPrTick;
	}

	protected void stopElevator() {
		if (elevatorDriveTimer != null) {
			elevatorDriveTimer.cancel();
			elevatorTimerTask.cancel();
			elevatorDriveTimer.purge();
		}

	}

	/**
	 * Calculates estimated time for arrival
	 * 
	 * @param height
	 * @return
	 */
	public double estimateTimeTo(double height) {
		return this.estimateExpectedTicks(height) * this.timerDelay;
	}

	public void stopEmergency() {
		this.setCurrentStatus(ElevatorStaus.emergencyStopped);
		this.stopElevator();
		LOG.info("Elevator has done an emergency stop");

	}

	public ElevatorStaus getElevatorStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(ElevatorStaus currentStatus) {
		this.currentStatus = currentStatus;
	}

}
