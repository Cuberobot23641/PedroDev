package opmode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import vision.LimelightDetector;


@Autonomous(name = "limelight detector test")
public class LimelightDetectorTest extends OpMode {
    private LimelightDetector detector;

    /** This is the main loop of the OpMode, it will run repeatedly after clicking "Play". **/
    @Override
    public void loop() {
        double[] positions = detector.getPositions();
        telemetry.addData("y distance", positions[0]);
        telemetry.addData("turret angle", positions[1]);
        telemetry.addData("wrist angle", positions[2]);
        telemetry.update();
    }

    /** This method is called once at the init of the OpMode. **/
    @Override
    public void init() {
        detector = new LimelightDetector(hardwareMap, "blue sample");
    }
}
