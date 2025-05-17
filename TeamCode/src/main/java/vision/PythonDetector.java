package vision;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PythonDetector {

    // TODO: we want to change the sorting algorithm so that it grabs a sample that isn't close to others
    // Limelight and claw configuration
    public static double turretLength = 6.5;
    private Limelight3A limelight;
    private String targetColor = "blue sample";
    private double distX = 0;
    private double distY = 0;

    private double angle = 0;
    private long staleness = 0;

    public PythonDetector(HardwareMap hardwareMap, String targetColor) {
        this.targetColor = targetColor;
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        if (targetColor.equals("blue sample")) {
            limelight.pipelineSwitch(4);
        } else if (targetColor.equals("red sample")){
            limelight.pipelineSwitch(5);
        } else {
            limelight.pipelineSwitch(6);
        }
        limelight.start();
    }
    private boolean isReachable(double x, double y) {
        if (Math.abs(x) > turretLength) return false;
        double turretExtension = Math.sqrt(Math.abs(Math.pow(turretLength, 2) - Math.pow(x, 2)));
        return y - turretExtension >= 0 && y - turretExtension <= 14;
    }

    public void update() {
        LLResult result = limelight.getLatestResult();

        if (result != null) {
            if (result.isValid()) {
                staleness = result.getStaleness();
                double[] pythonOutput = result.getPythonOutput();
                distX = pythonOutput[0];
                distY = pythonOutput[1];
                angle = pythonOutput[2];
            }
        }
    }
    public double[] getPositions() {
        if (distX == 0 && distY == 0 && angle == 0) {
            return new double[] {0, 0, 0};
        }
        double turretAngle = Math.toDegrees(Math.asin(distX / turretLength));
        double turretExtension = Math.sqrt(Math.abs(Math.pow(turretLength, 2) - Math.pow(distX, 2)));
        // TODO: logic for if its > 90 or < -90
        double wristAngle = -turretAngle + angle;

        double extensionDistance = distY - turretExtension;
        return new double[] {extensionDistance, turretAngle, wristAngle};
    }

    public boolean isFresh() {
        return staleness < 100;
    }

    public void off() {
        limelight.stop();
    }

    public void on() {
        limelight.start();
    }
}


