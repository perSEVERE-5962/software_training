// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DriveTrain;

public class Drive extends Command {
  DriveTrain driveTrain;
  DoubleSupplier axis1;
  DoubleSupplier axis2;
  DoubleSupplier axis3;
  BooleanSupplier buttonOn;
  /** Creates a new Drive. */
  public Drive(DriveTrain driveTrain, DoubleSupplier axis1, DoubleSupplier axis2, DoubleSupplier axis3, BooleanSupplier buttonOn) {
    this.driveTrain = driveTrain;
    this.axis1 = axis1;
    this.axis2 = axis2;
    this.axis3 = axis3;
    this.buttonOn = buttonOn;
    addRequirements(driveTrain);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double multiplier = Math.abs((axis3.getAsDouble() - 1) * 0.5);
    driveTrain.arcadeDrive(axis1.getAsDouble() * multiplier * (buttonOn.getAsBoolean() ? 1 : 0), axis2.getAsDouble() * multiplier * (buttonOn.getAsBoolean() ? 1 : 0));
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    driveTrain.arcadeDrive(0, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}