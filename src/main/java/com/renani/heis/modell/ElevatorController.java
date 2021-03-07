package com.renani.heis.modell;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;

public class ElevatorController {
	private static Logger LOG = getLogger(ElevatorController.class);
	Floor currentFloor = null;
	private ArrayList<Button> buttons;
	private double currentHeight;
	private Elevator elevator;
	private double heightGain = 0;

	/***
	 * 
	 * @param elevator
	 * @param buttons
	 */

	// TODO: Change to immutable list
	public ElevatorController(Elevator elevator, final ArrayList<Button> buttons, Floor currentFloor) {
		this.elevator = elevator;
		this.buttons = buttons;
		this.currentFloor = currentFloor;
		int indexOfCurrent = this.buttons.stream().map(o -> o.getFloor()).collect(Collectors.toList())
				.indexOf(currentFloor);

		calculateHeight(0, indexOfCurrent);
		this.currentHeight = calculateHeight(0, indexOfCurrent);
	}

	/**
	 * 
	 * @param button
	 * @return
	 */
	public double estimateTimeToFloor(Button button) {
		final int targetIndex = this.buttons.indexOf(button);
		final int indexOfCurrent = this.buttons.stream().map(o -> o.getFloor()).collect(Collectors.toList())
				.indexOf(currentFloor);
		double height = this.calculateHeight(indexOfCurrent, targetIndex);

		return this.elevator.estimateTimeTo(height);
	}

	public double calculateHeight(int startIndex, int targetIndex) {
		double currentHeight = 0;
		if (targetIndex < startIndex) {
			for (int i = startIndex - 1; i >= targetIndex; i--) {
				double height = this.buttons.get(i).getFloor().getHeight();
				currentHeight -= height;
			}
			return currentHeight * -1;
		} else {
			for (int i = 0; i < targetIndex; i++) {
				double height = this.buttons.get(i).getFloor().getHeight();
				currentHeight += height;
			}
			return currentHeight;
		}
	}
	
	public Direction registerButtonClick(final Button button) {
		double height = calculateHeightBetween(this.currentFloor, button.getFloor());
		CountDownLatch latch = new CountDownLatch((int) Math.ceil(elevator.estimateExpectedTicks(height+this.heightGain)));
		return this.registerButtonClick(button, latch);
	}

	/**
	 * Method for reacting to button clicks.
	 * 
	 * @param button
	 * @return 
	 */
	
	//TODO: BUG: In case of emergency stops I need to cancel the countdown.
	protected  Direction registerButtonClick(final Button button, CountDownLatch latch) {
		if (!ElevatorStatus.available.equals(this.elevator.getElevatorStatus())) {
			LOG.info("Elevator is not available yet");
			return null;
		}
	 

		LOG.info("Registering click on button for floor " + button.getFloor() + ". We are at  " + currentFloor);
		final int targetIndex = this.buttons.indexOf(button);
		final int indexOfCurrent = this.buttons.stream().map(o -> o.getFloor()).collect(Collectors.toList())
				.indexOf(currentFloor);

		double height = this.calculateHeight(indexOfCurrent, targetIndex);
		LOG.info("Adjusting for GAIN height + gain " + height + ":" + heightGain);

		MovementListener movementListener = new MovementListener() {

			@Override
			public void registerMovementStopped(double gain) {
								
				heightGain = gain;
				currentFloor = button.getFloor();
				LOG.info("Registering GAIN of " + gain);
				if (Math.abs(gain) < 0.5) {
					
					elevator.setCurrentStatus(ElevatorStatus.available);
				}else {
					sendEmergencyAlert();
					elevator.setCurrentStatus(ElevatorStatus.emergencyStopped);
				}

			}
		};

		Direction direction = Direction.UP;
		if (targetIndex < indexOfCurrent) {
			direction = direction.DOWN;
		}

		height += heightGain;
		heightGain = 0;
	
		this.elevator.DriveToHeight(height, direction, latch, movementListener);
		return direction;

	}

	public Floor getCurrentFloor() {
		return this.getCurrentFloor();
	}
	
	private double calculateHeightBetween (Floor start, Floor end) {
		final int targetIndex =this.buttons.stream().map(o -> o.getFloor()).collect(Collectors.toList())
				.indexOf(end);
		final int startIndex = this.buttons.stream().map(o -> o.getFloor()).collect(Collectors.toList())
				.indexOf(start);
		return  calculateHeight(startIndex, targetIndex);
	}
	
	public void sendEmergencyAlert() {
		LOG.error("FARE FOR LIV!!");
	}

}
