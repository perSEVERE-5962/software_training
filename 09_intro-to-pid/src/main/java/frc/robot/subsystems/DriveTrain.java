/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ChassisType;
import frc.robot.drive.DriveFactory;
import frc.robot.drive.DriveInterface;

public class DriveTrain extends SubsystemBase {
  private DriveInterface m_drive;
  private boolean driveTrainSet = false;

  public DriveTrain() {
    setMotorControllerType(ChassisType.kRomi);
  }

  public void setMotorControllerType(int motorControllerType) {
    if (driveTrainSet == false) {
      DriveFactory driveFactory = new DriveFactory();
      m_drive = driveFactory.createDrive(motorControllerType);
      resetEncoders();
      driveTrainSet = true;
    }
  }

  public void resetGyro() {
    m_drive.resetGyroAngle();
  }

  public double getGyroAngle() {
    return m_drive.getGyroAngle();
  }

  public void resetEncoders() {
    m_drive.resetEncoders();
  }

  public double getAverageEncoderDistance() {
    double distance = 0;
    if (m_drive != null) {
      distance = m_drive.getAverageEncoderDistance();
    }
    return distance;
  }

  @Override
  public void periodic() {
    if (m_drive != null) {
      m_drive.periodic();
    }
  }

  public void arcadeDrive(double speed, double rotation) {
    m_drive.arcadeDrive(speed, rotation);
  }

  public void stopDrive() {
    m_drive.stopDrive();
  }

  public void moveDistanceWithPID(double position) {
    try {
      m_drive.moveDistanceWithPID(position);
    } catch (Exception e) {
      stopDrive();
      SmartDashboard.putString("ERROR MESSAGE", e.getMessage());
    }
  }
}
