package sensor.compass;

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.addon.CompassHTSensor;
import lejos.util.Delay;

public class DirectionHolder {

	private int normalMotorSpeed;
	private int reducedMotorSpeed;
	private float direction;
	private float deltaDirection = 1;
	private boolean activated = false;
	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;
	private CompassHTSensor compassSensor;
	private static final int DELAY_TIME = 250;
	
	/**
	 * Initialises a DirectionHolder and sets standard values for motor speed and direction.
	 * The sensor and motors still have to be set in order to let the DirectionHolder work properly.
	 */
	public DirectionHolder() {
		normalMotorSpeed = 250;
		reducedMotorSpeed = (int) (normalMotorSpeed * 0.6);
		direction = 180;
	}
	
	/**
	 * Initialises a DirectionHolder with given values for motor
	 * speed and direction.
	 * 
	 * @param normalMotorSpeed the normal speed a motor has
	 * @param direction the direction the robot should hold
	 * @param leftMotor the robots left motor
	 * @param rightMotor the robots right motor
	 * @param compassSensor the compass sensor to check the direction
	 */
	public DirectionHolder(int normalMotorSpeed, float direction, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, CompassHTSensor compassSensor) {
		this.normalMotorSpeed = normalMotorSpeed;
		this.reducedMotorSpeed = (int) (normalMotorSpeed * 0.6);
		this.direction = direction;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.compassSensor = compassSensor;
	}
	
	public float getDirection() {
		return direction;
	}

	public void setDirection(float direction) {
		this.direction = direction;
	}

	public void setNormalMotorSpeed(int normalMotorSpeed) {
		this.normalMotorSpeed = normalMotorSpeed;
	}

	public void setLeftMotor(NXTRegulatedMotor leftMotor) {
		this.leftMotor = leftMotor;
	}

	public void setRightMotor(NXTRegulatedMotor rightMotor) {
		this.rightMotor = rightMotor;
	}

	public void setCompassSensor(CompassHTSensor compassSensor) {
		this.compassSensor = compassSensor;
	}

	/**
	 * Tells the DirectionHolder to do it's work. Note, that both motors and the compass sensor have to be initialised.
	 */
	public void start() {
		if (activated) 
			throw new IllegalStateException("DirectionHolder is already running");
		
		if (leftMotor != null && rightMotor != null && compassSensor != null) {
			activated = true;
			new Thread(new Runnable() {
	
				@Override
				public void run() {
					while (activated) {
						float currentDirection = compassSensor.getDegrees();
						double difference = Math.abs(direction - currentDirection);
						if (difference > deltaDirection) {
							//direction changed too much, correct it
							if (currentDirection > direction) {
								//turn left
								leftMotor.setSpeed(reducedMotorSpeed);
								Delay.msDelay(DELAY_TIME);
								leftMotor.setSpeed(normalMotorSpeed);
							} else {
								//turn right
								rightMotor.setSpeed(reducedMotorSpeed);
								Delay.msDelay(DELAY_TIME);
								rightMotor.setSpeed(normalMotorSpeed);
							}
						}
						Delay.msDelay(DELAY_TIME);
					}
				}
				
			}).start();
		} else {
			throw new IllegalStateException("motors and sensor have to be initialised first");
		}
	}
	
	/**
	 * Tells the DirectionHolder to stop. It can easily restarted by calling start again.
	 */
	public void stop() {
		activated = false;
	}
		
}
