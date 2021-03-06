package com.renani.heis.modell;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.Logger;

public class ElevatorTimerTask extends TimerTask {
	private static Logger LOG = getLogger(Elevator.class);
		private Elevator el;
		private double heightGain;
		private CountDownLatch latch;
		private double speedPrTick;
		private MovementListener movementListener;
		

		@Override
		public void run() {
			try {

				heightGain = heightGain - speedPrTick;

				if (heightGain < 0) {
					el.stopElevator();
					movementListener.registerMovementStopped(heightGain);
			 
				}
				LOG.info("Elevator is still running.  Nr meters left " + heightGain
						+ " driving speed " + speedPrTick);
				if (latch != null) {
					latch.countDown();
				}
			} catch (Exception e) {
				LOG.info("ElevatorTimer driving failed", e);
				handleCriticalException(e);

			}
		}
		
		@Override
		public boolean cancel() {
			movementListener.registerMovementStopped(heightGain);
			return super.cancel();
		}

		private void handleCriticalException(Exception e) {
			LOG.debug("running debug ...");
			LOG.debug("Calling for repair");
			this.el.setCurrentStatus(ElevatorStaus.unavailable);

		}

		public   ElevatorTimerTask(Elevator elevator, double height, double speedPrTick, MovementListener movementListener,  CountDownLatch latch) {
			this.movementListener = movementListener;
			LOG.debug("Preparing for running elevator");
			this.el = el;
			this.heightGain = height;
 			this.latch = latch;
			this.speedPrTick = speedPrTick;
		}

 
}
