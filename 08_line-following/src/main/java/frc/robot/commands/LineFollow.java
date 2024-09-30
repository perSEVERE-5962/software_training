// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveTrain;

public class LineFollow extends Command {
    private DriveTrain m_driveTrain;
    // Left and Right IR sensors
    private DigitalInput leftIR = new DigitalInput(9);
    private DigitalInput rightIR = new DigitalInput(8);
    private static boolean started = false;

    /** Creates a new LineFollow. */
    public LineFollow() {
        // Use addRequirements() here to declare subsystem dependencies.
        m_driveTrain = RobotContainer.getInstance().getDriveTrain();
        addRequirements(m_driveTrain);

    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        boolean isleftIR = leftIR.get();
        boolean isrightIR = rightIR.get();

        // If the left IR sensor sees a black line, the romi will turn left
        if (isleftIR == true) {
            this.m_driveTrain.arcadeDrive(0, -0.4);
            started = true;

        }
        // If the right IR sensor sees a black line, the romi will turn right
        if (isrightIR == true) {
            m_driveTrain.arcadeDrive(0, 0.4);
            started = true;

            // If the left IR sensor sees a white line, the romi will move straight
        } else if (isleftIR == false && isrightIR == false) {
            m_driveTrain.arcadeDrive(-1, 0);
        }

    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
    }

    // Returns true when the command should end.
    // If both the left and right IR sensors see a black line, the romi will stop
    @Override
    public boolean isFinished() {
        boolean isleftIR = leftIR.get();
        boolean isrightIR = rightIR.get();
        boolean isFinished = false;
        if (started == true && isleftIR == true && isrightIR == true) {
                m_driveTrain.arcadeDrive(0, 0);
        } else {
            m_driveTrain.arcadeDrive(-1, 0);
        }

        return isFinished;
    }
}
