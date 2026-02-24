package frc.robot.subystem.shooter;


import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Distance;
import org.littletonrobotics.junction.Logger;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.RPM;

public class ShooterSubsystem extends ShooterTalonFX {
    private static ShooterSubsystem INSTANCE;

    private Distance distanceToTarget;

    private AngularVelocity targetVelocity;

    private final ShooterIO shooter;

    private ShooterSubsystem()
    {
        shooter = new ShooterTalonFX();
    }

    @Override
    public void readPeriodic() {
        shooter.readPeriodic();
    }

    @Override
    public void writePeriodic() {
        Logger.recordOutput("Shooter/Distance", distanceToTarget);
        Logger.recordOutput("Shooter/Velocity", targetVelocity);

        shooter.writePeriodic();
    }

    public AngularVelocity calculateVelocity(Pose2d currentPose, Pose2d targetPose) {
        distanceToTarget = Distance.ofBaseUnits(
            Math.sqrt((Math.pow(currentPose.getX(), 2) - Math.pow(targetPose.getX(), 2)) + (Math.pow(currentPose.getY(), 2)-Math.pow(targetPose.getY(), 2))),
            Units.Meters
        );
        targetVelocity = AngularVelocity.ofBaseUnits(1510 + (217 * distanceToTarget.in(Meters)) - (3.81 * distanceToTarget.in(Meters) * distanceToTarget.in(Meters)), RPM.getBaseUnit());
        return targetVelocity;
    }

    public static ShooterSubsystem getInstance() {return INSTANCE;}
}

