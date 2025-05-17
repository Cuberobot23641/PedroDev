package opmode.autonomous;


import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import vision.LimelightDetector;
import static robot.RobotConstants.*;
import robot.robots.AutoRobot;
import pedroPathing.Constants;

@Autonomous(name = "spec auto blue v1")
public class SpecimenBlueV1 extends OpMode {
    public AutoRobot robot;
    private Follower follower;
    // private LimelightDetector detector;
    private int pathState;
    private Timer pathTimer;
    private final Pose startPose = new Pose(9, 66, Math.toRadians(0));
    private final Pose grabPose = new Pose(9, 34, Math.toRadians(0));
    private final Pose scorePreloadPose = new Pose(38, 66, Math.toRadians(0));

    private PathChain scorePreload, grabSample1, moveSample1, grabSample2, moveSample2, grabSample3, grabSpec1;
    public void buildPaths() {
        // TODO: set custom zpams
        scorePreload = follower.pathBuilder()
                .addPath(new BezierLine(startPose, scorePreloadPose))
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .build();

        grabSample1 = follower.pathBuilder()
                .addPath(
                        // Line 2
                        new BezierCurve(
                                new Pose(38.000, 66.000),
                                new Pose(27.000, 67.000),
                                new Pose(29.000, 41.000)
                        )
                )
                // oh yeah baby
                //.setCustomHeadingInterpolation(t -> )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-45))
                .addParametricCallback(0.6, () -> robot.extension.setTargetInches(12))
                .addParametricCallback(0.6, () -> robot.intake.setTurretPos(INTAKE_TURRET_DEFAULT))
                .addParametricCallback(0.6, () -> robot.intake.setWristAngle(45))
                .addParametricCallback(0.7, () -> robot.intake.setElbowIntakePos(INTAKE_ELBOW_DOWN))
                //.setZeroPowerAccelerationMultiplier()
                .setPathEndTimeoutConstraint(0.99)
                .setNoDeceleration()

                .build();
        moveSample1 = follower.pathBuilder()
                .addPath(
                        // Line 3
                        new BezierLine(
                                new Pose(29.000, 41.000),
                                new Pose(29.000, 36.000)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(-45), Math.toRadians(-130))
                .setPathEndTimeoutConstraint(0.99)
                .setNoDeceleration()
                .build();
        // TODO: test this, it could be more accurate with p2p
//        align = new PathBuilder()
//                .addPath(new BezierPose(new Pose(31, 72-distX*1.03)))
//                .setConstantHeadingInterpolation(Math.toRadians(0))
//                .build();
        grabSample2 = follower.pathBuilder()
                .addPath(
                        // Line 4
                        new BezierLine(
                                new Pose(29.000, 36.000),
                                new Pose(29.000, 31.000)
                        )
                )
                .addParametricCallback(0.3, () -> robot.intake.setElbowIntakePos(INTAKE_ELBOW_DOWN))
                .setLinearHeadingInterpolation(Math.toRadians(-130), Math.toRadians(-45))
                .setPathEndTimeoutConstraint(0.99)
                .setNoDeceleration()
                .build();
        moveSample2 = follower.pathBuilder()
                .addPath(
                        // Line 5
                        new BezierLine(
                                new Pose(29.000, 31.000),
                                new Pose(29.000, 26.000)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(-45), Math.toRadians(-130))
                .build();
        grabSample3 = follower.pathBuilder()
                .addPath(
                        // Line 6
                        new BezierLine(
                                new Pose(29.000, 26.000),
                                new Pose(29.000, 21.000)
                        )
                )
                .addParametricCallback(0.3, () -> robot.intake.setElbowIntakePos(INTAKE_ELBOW_DOWN))
                .setLinearHeadingInterpolation(Math.toRadians(-130), Math.toRadians(-45))
                .setPathEndTimeoutConstraint(0.99)
                .setNoDeceleration()
                .build();
        grabSpec1 = follower.pathBuilder()
                .addPath(
                        // Line 7
                        new BezierCurve(
                                new Pose(29.000, 21.000),
                                new Pose(29.000, 34.000),
                                new Pose(8.000, 34.000)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(-45), Math.toRadians(0))
                .build();
    }
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(scorePreload, true);
                robot.startScore();
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy() && robot.isScoreFinished()) {
                    robot.startPrepareGrab();
                    setPathState(2);
                }
                break;
            case 2:
                if (robot.isReleaseFinished()) {
                    robot.startRetract();
                    follower.setMaxPower(0.6);
                    follower.followPath(grabSample1, true);
                    setPathState(3);
                }
                break;
            case 3:
                if (!follower.isBusy()) {
                    robot.intake.closeIntakeClaw();
                    setPathState(4);
                }
                break;
            case 4:
                if (pathTimer.getElapsedTimeSeconds() > 0.4) {
                    robot.intake.setElbowIntakePos(INTAKE_ELBOW_DOWN+0.15);
                    follower.followPath(moveSample1, true);
                    setPathState(5);
                }
                break;
            case 5:
                if (!follower.isBusy()) {
                    robot.intake.openIntakeClaw();
                    setPathState(6);
                }
                break;
            case 6:
                if (pathTimer.getElapsedTimeSeconds() > 0.4) {
                    follower.followPath(grabSample2, true);
                    setPathState(7);
                }
                break;
            case 7:
                if (!follower.isBusy()) {
                    robot.intake.closeIntakeClaw();
                    setPathState(8);
                }
                break;
            case 8:
                if (pathTimer.getElapsedTimeSeconds() > 0.4) {
                    robot.intake.setElbowIntakePos(INTAKE_ELBOW_DOWN+0.15);
                    follower.followPath(moveSample2, true);
                    setPathState(9);
                }
                break;
            case 9:
                if (!follower.isBusy()) {
                    robot.intake.openIntakeClaw();
                    setPathState(10);
                }
                break;
            case 10:
                if (pathTimer.getElapsedTimeSeconds() > 0.4) {
                    follower.followPath(grabSample3, true);
                    setPathState(11);
                }
                break;
            case 11:
                if (!follower.isBusy()) {
                    robot.intake.closeIntakeClaw();
                    setPathState(12);
                }
                break;
            case 12:
                if (pathTimer.getElapsedTimeSeconds() > 0.4) {
                    robot.startRetract();
                    follower.followPath(grabSpec1, true);
                    setPathState(12);
                }
                break;

            // TODO: the very boring and repeated process of scoring can be done in a loop for 5 times
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    @Override
    public void loop() {
        robot.loop();
        // detector.update();
        follower.update();
        autonomousPathUpdate();
    }

    @Override
    public void init() {
        pathTimer = new Timer();
        robot = new AutoRobot(hardwareMap);
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);
        // detector = new LimelightDetector(hardwareMap, "blue sample");
    }

    @Override
    public void init_loop() {}

    @Override
    public void start() {
        buildPaths();
        setPathState(0);
    }
}



