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

				if (latch != null) {
					latch.countDown();
				}
				heightGain = heightGain - speedPrTick;
				
				if (heightGain < 0) {
					el.stopElevator();
					movementListener.registerMovementStopped(heightGain);
			 
				}
				LOG.info("Elevator is still running.  Nr meters left " + heightGain
						+ " driving speed " + speedPrTick);
				

			} catch (Exception e) {
				LOG.info("ElevatorTimer driving failed", e);
				el.stopEmergency();
				this.movementListener.registerMovementStopped(heightGain);
				handleCriticalException(e);
			 
			}catch(Throwable t) {
				LOG.info("ElevatorTimer driving failed", t);
				el.stopEmergency();
				this.movementListener.registerMovementStopped(heightGain);
				handleCriticalException(t);
 
			}
		}
		
		@Override
		public boolean cancel() {
			movementListener.registerMovementStopped(heightGain);
			return super.cancel();
		}

		private void handleCriticalException(Throwable t) {
			LOG.debug("running debug ...");
			LOG.debug("Calling for repair");
	 
		}

		public   ElevatorTimerTask(Elevator elevator, double height, double speedPrTick, MovementListener movementListener,  CountDownLatch latch) {
			this.movementListener = movementListener;
			LOG.debug("Preparing for running elevator");
			this.el=elevator;
			this.heightGain = height;
 			this.latch = latch;
			this.speedPrTick = speedPrTick;
		}

 
}
