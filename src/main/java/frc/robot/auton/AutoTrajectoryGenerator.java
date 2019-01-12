package frc.robot.auton;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class AutoTrajectoryGenerator {
    private static final double TIME_STEP = 0.02;

    /**
     * Creates a {@link TrajectoryPair} that contains the left and right trajectories of a motion profile
     * @param velocity
     * @param acceleration
     * @param jerk
     * @param wheelbase
     * @param points
     * 
     * @return The trajectory pair from the generated motion profile
     */
    private static TrajectoryPair generatePair(double velocity, double acceleration, double jerk, double wheelbase, Waypoint[] points) {
        Trajectory trajectory = Pathfinder.generate(
                points,
                new Trajectory.Config(
                    Trajectory.FitMethod.HERMITE_CUBIC, 
                    Trajectory.Config.SAMPLES_HIGH, 
                    TIME_STEP, 
                    velocity, 
                    acceleration, 
                    jerk)
        );

        TankModifier modifier = new TankModifier(trajectory);
        modifier.modify(wheelbase);
        return new TrajectoryPair(modifier.getLeftTrajectory(), modifier.getRightTrajectory());
    }

    /**
     * Class that represents a pairing of left and right trajectories.
     * <p>
     * Good for storing {@link Trajectory Jaci trajectories} that represent
     * left and right paths.
     */
    public static class TrajectoryPair {
        private Trajectory leftTrajectory, rightTrajectory;

        public TrajectoryPair(Trajectory left, Trajectory right) {
            this.leftTrajectory = left;
            this.rightTrajectory = right;
        }

        public Trajectory getLeft() {
            return leftTrajectory;
        }

        public Trajectory getRight() {
            return rightTrajectory;
        }
    }
}