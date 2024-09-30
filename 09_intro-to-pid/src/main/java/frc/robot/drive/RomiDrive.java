// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.drive;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.sensors.RomiGyro;

/** Add your docs here. */
public class RomiDrive extends DriveBase {
  // The Romi has the left and right motors set to
  // PWM channels 0 and 1 respectively
  private final Spark m_leftMotor = new Spark(Constants.Romi.leftDeviceID);
  private final Spark m_rightMotor = new Spark(Constants.Romi.rightDeviceID);

  // The Romi has onboard encoders that are hardcoded
  // to use DIO pins 4/5 and 6/7 for the left and right
  private final Encoder m_leftEncoder =
      new Encoder(Constants.Romi.leftEncoderChannelA, Constants.Romi.leftEncoderChannelB);
  private final Encoder m_rightEncoder =
      new Encoder(Constants.Romi.rightEncoderChannelA, Constants.Romi.rightEncoderChannelB);
    
  private double m_p = SmartDashboard.getNumber("p value", 0);
  private double m_i = SmartDashboard.getNumber("i value", 0);
  private double m_d = SmartDashboard.getNumber("d value", 0);
  private double m_iLimit = 0.25;

  private double m_rightErrorSum = 0;
  private double m_rightLastTimestamp = 0;
  private double m_rightLastError = 0;
  private double m_leftErrorSum = 0;
  private double m_leftLastTimestamp = 0;
  private double m_leftLastError = 0;
  private int counter = 0;

private final RomiGyro m_gyro = new RomiGyro();
  RomiDrive() {
    init(m_leftMotor, m_rightMotor);

    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_leftMotor.setInverted(true);  
    m_rightMotor.setInverted(true);
    

    // Use inches as unit for encoder distances
    m_leftEncoder.setDistancePerPulse(
        (Math.PI * Constants.Romi.kWheelDiameterInch) / Constants.Romi.kCountsPerRevolution);
    m_rightEncoder.setDistancePerPulse(
        (Math.PI * Constants.Romi.kWheelDiameterInch) / Constants.Romi.kCountsPerRevolution);

    resetEncoders();

    m_p = SmartDashboard.getNumber("p value", 0);
    m_i = SmartDashboard.getNumber("i value", 0);
    m_d = SmartDashboard.getNumber("d value", 0);
  }

  @Override
  public void resetEncoders() {
    m_leftEncoder.reset();
    m_rightEncoder.reset();
  }

  @Override
  public void resetGyroAngle() {
  m_gyro.reset();
  }

  @Override
  public double getLeftEncoderDistance() {
    return m_leftEncoder.getDistance();
  }

  @Override
  public double getRightEncoderDistance() {
    return m_rightEncoder.getDistance();
  }

  @Override
  public double getAverageEncoderDistance() {
    return (getLeftEncoderDistance() + getRightEncoderDistance()) / 2.0;
  }

  @Override
  public void setRampRate(double rate) {
    // Not applicable for Romi
  }

  @Override
  public void moveDistanceWithPID(double distance) throws Exception {
    double timeStamp = Timer.getFPGATimestamp();
    double leftOutputSpeed = getLeftOutputSpeed(distance, timeStamp);
    double rightOutputSpeed = getRightOutputSpeed(distance, timeStamp);

    // output to motors
    m_leftMotor.set(-leftOutputSpeed);
    m_rightMotor.set(-rightOutputSpeed);
    counter++;

    // update last- variables
    m_rightLastTimestamp = m_leftLastTimestamp = Timer.getFPGATimestamp();
  }

  private double getRightOutputSpeed(double distance, double timeStamp) {
    // calculations
    double encoderDistance = getRightEncoderDistance();
    double error = distance - encoderDistance;

    double dt = timeStamp - m_rightLastTimestamp;

    if (Math.abs(error) < m_iLimit) {
      m_rightErrorSum += error * dt;
    }

    double errorRate = (error - m_rightLastError) / dt;

    double outputSpeed = m_p * error + m_i * m_rightErrorSum + m_d * errorRate;
    m_rightLastError = error;

    SmartDashboard.putNumber("right error", error);
    SmartDashboard.putNumber("right encoder", encoderDistance);
    if (counter==0)
      SmartDashboard.putNumber("right speed", outputSpeed);
    
    return outputSpeed;
  }

  private double getLeftOutputSpeed(double distance, double timeStamp) {
    // calculations
    double encoderDistance = getLeftEncoderDistance();
    double error = distance - encoderDistance;

    double dt = timeStamp - m_leftLastTimestamp;

    if (Math.abs(error) < m_iLimit) {
      m_leftErrorSum += error * dt;
    }

    double errorRate = (error - m_leftLastError) / dt;

    double outputSpeed = m_p * error + m_i * m_leftErrorSum + m_d * errorRate;
    m_leftLastError = error;

    SmartDashboard.putNumber("left error", error);
    SmartDashboard.putNumber("left encoder", encoderDistance);
    if (counter==0)
        SmartDashboard.putNumber("left speed", outputSpeed);
     
    return outputSpeed;
  }
  
  @Override
  public void setIdleMode(int idleMode) {
    // Not applicable for Romi
  }

  @Override
  public double getGyroAngle() {
    /* Need to convert distance travelled to degrees. The Standard
       Romi Chassis found here, https://www.pololu.com/category/203/romi-chassis-kits,
       has a wheel placement diameter (149 mm) - width of the wheel (8 mm) = 141 mm
       or 5.551 inches. We then take into consideration the width of the tires.
    */
    //double inchPerDegree = Math.PI * 5.551 / 360;
    //return getAverageTurningDistance() / inchPerDegree;
    return m_gyro.getAngleZ();
  }

  private double getAverageTurningDistance() {
    double leftDistance = Math.abs(m_leftEncoder.getDistance());
    double rightDistance = Math.abs(m_rightEncoder.getDistance());
    double averageTurnDistance = (leftDistance + rightDistance) / 2.0;

    // adjust the value based on the turn direction
    if (m_leftEncoder.getDistance() < 0) averageTurnDistance = averageTurnDistance * 1;

    return averageTurnDistance;
  }
}
