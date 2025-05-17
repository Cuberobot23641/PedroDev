package opmode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import robot.robots.AutoRobot;
import vision.LimelightDetector;


@Autonomous(name = "limelight detector move test")
public class LimelightDetectorMove extends OpMode {
    private LimelightDetector detector;
    private AutoRobot robot;
    private Gamepad gp1;
    private Gamepad cgp1;
    private Gamepad pgp1;

    /** This is the main loop of the OpMode, it will run repeatedly after clicking "Play". **/
    @Override
    public void loop() {
        pgp1.copy(cgp1);
        cgp1.copy(gp1);

        if (gamepad1.a && !pgp1.a) {
            double[] positions = detector.getPositions();
            robot.setExtensionInches(positions[0]);
            robot.setTurretAngle(positions[1]);
            robot.setWristAngle(positions[2]);
            robot.startReleaseSpecGrabSample();
        }

        if (gamepad1.b && !pgp1.b) {
            robot.startRetract();
        }


//        telemetry.addData("y distance", positions[0]);
//        telemetry.addData("turret angle", positions[1]);
//        telemetry.addData("wrist angle", positions[2]);

        robot.loop();
        telemetry.update();
    }

    /** This method is called once at the init of the OpMode. **/
    @Override
    public void init() {
        detector = new LimelightDetector(hardwareMap, "blue sample");
        robot = new AutoRobot(hardwareMap);
        gp1 = gamepad1;
        cgp1 = new Gamepad();
        pgp1 = new Gamepad();
    }
}