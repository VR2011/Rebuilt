package frc.robot;

import choreo.trajectory.EventMarker;
import choreo.trajectory.SwerveSample;
import choreo.trajectory.Trajectory;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystem.drivetrain.Drivetrain;
import frc.robot.subsystem.drivetrain.control.TrajectoryControl;
import frc.robot.subsystem.drivetrain.control.VelocityFOC;
import frc.robot.subsystem.feeder.FeederStates;
import frc.robot.subsystem.feeder.FeederSubsystem;
import frc.robot.subsystem.intake.IntakeStates;
import frc.robot.subsystem.intake.IntakeSubsystem;
import frc.robot.subsystem.mop.MopStates;
import frc.robot.subsystem.mop.MopSubsystem;

import java.util.List;

public class PathFollower extends Command {
    private final Trajectory<SwerveSample> trajectory;
    private final edu.wpi.first.wpilibj.Timer timer;
    private final List<EventMarker> markers;

    private TrajectoryControl control;

    public PathFollower(Trajectory<SwerveSample> trajectory) {
        timer = new Timer();
        this.trajectory = trajectory;
        markers = trajectory.events();

        control = new TrajectoryControl(trajectory.getInitialSample(false).get());
    }

    @Override
    public void initialize() {
        timer.reset();
        timer.start();

    }

    private void runEvent(String name) {
        switch(name) {
            case "DeployIntake" -> {
                IntakeSubsystem.getInstance().setState(IntakeStates.Deployed);
            }
            case "StoreIntake" -> {
                IntakeSubsystem.getInstance().setState(IntakeStates.StoredOff);
            }
            case "RunShooter" -> {
                MopSubsystem.getInstance().setState(MopStates.FEED);
                FeederSubsystem.getInstance().setState(FeederStates.FEED);
            }
            case "StopShooter" -> {
                MopSubsystem.getInstance().setState(MopStates.OFF);
                FeederSubsystem.getInstance().setState(FeederStates.OFF);
            }
            default -> {
                System.out.println("Unknown event " + name);
            }
        }
    }

    @Override
    public void execute() {
        Drivetrain.getInstance().setControl(new TrajectoryControl(
            trajectory.sampleAt(timer.get(),false).get()
        ));

        for (int i = 0; i <markers.size() ; i++) {
            if (MathUtil.isNear(markers.get(i).timestamp, timer.get(), .020)) {
                runEvent(markers.get(i).event);
            }
        }
    }


    @Override
    public void end(boolean interrupted) {
        Drivetrain.getInstance().setControl(new VelocityFOC(0,0,0));
    }

    @Override
    public boolean isFinished() {
        return
            timer.hasElapsed(trajectory.getTotalTime()) &&
            control.atSetpoint();
    }
}
