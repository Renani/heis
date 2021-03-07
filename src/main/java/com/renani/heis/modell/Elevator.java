package com.renani.heis.modell;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.util.Timer;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.Logger;

public class Elevator {
	private static Logger LOG = getLogger(Elevator.class);
	// having it as a field for status info
	private double speed;
	private ElevatorStatus currentStatus = ElevatorStatus.available;

	private Timer elevatorDriveTimer;
	int timerDelay = 75;
	final int LOWEST_POSSIBLE_TIMERDELAY = 75;
	private double speedPrTick=0.5;
	double errorMargin = 0.05;
	private ElevatorTimerTask elevatorTimerTask;
	private ElevatorStatusListener elevatorStatusListener;

	//
	// TODO: Use javax-measure or similar instead
	/***
	 * 
	 * @param speed       given in m/s
	 * 
	 */

	// TODO: Safety evaluation of speed and errorMargin
	public Elevator(double speed) {
		this.speed = speed;
		
		// timerDelay = some function of speed
		try {
			//TODO: Remmember double dividing on integer is apparently always 0 (??)
			this.speedPrTick = (timerDelay/1000.0)*speed;
			
			LOG.info("TimerDelay " + timerDelay + " speedPrTicket " + speedPrTick + " speed " + speed);
			if (timerDelay < LOWEST_POSSIBLE_TIMERDELAY) {
				throw new IllegalArgumentException(
						"This elevator cannot support this speed with the error margin given. Decrease speed or increase errormargin");
			}
 

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
		this.setCurrentStatus(ElevatorStatus.onroute);
		if (latch != null) {
			try {
				latch.await();
			} catch (InterruptedException e) {
				LOG.debug("Concurrency issue with Junit issue ", e);

			}
		}

	}

	//TODO: Consider to do it a static method as it could be useful utility method
	public double estimateExpectedTicks(final double height) {
		return height / speedPrTick;
	}

	protected void stopElevator() {
		if (elevatorDriveTimer != null) {
			elevatorDriveTimer.cancel();
			elevatorDriveTimer.purge();
			LOG.info("Stopping elevator!!");
			elevatorDriveTimer=null;

		}
		if (elevatorTimerTask != null) {
			elevatorTimerTask.cancel();
			elevatorTimerTask = null;
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
		this.setCurrentStatus(ElevatorStatus.emergencyStopped);
		this.stopElevator();
		LOG.info("Elevator has done an emergency stop");

	}

	public ElevatorStatus getElevatorStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(ElevatorStatus newState) {
		this.currentStatus = newState;
		if (this.elevatorStatusListener != null)
			this.elevatorStatusListener.HandleStatusHasChanged(newState);
	}

	public void registerElevatorStatusListener(ElevatorStatusListener listener) {
		this.elevatorStatusListener = listener;
	}

}
