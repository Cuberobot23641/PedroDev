package vision;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LimelightDetector {

    // TODO: we want to change the sorting algorithm so that it grabs a sample that isn't close to others
    // Limelight and claw configuration
    public static double height = 11; // Camera height in inches
    public static double turretLength = 6.5;
    public static double degreesFromVertical = 45; // Camera angle (90° = down, 0° = forward)
    public static double lateralOffset = 4;
    private Limelight3A limelight;
    private String targetColor = "blue sample";
    private double distX = 0;
    private double distY = 0;

    private double angle = 0;
    private long staleness = 0;

    public LimelightDetector(HardwareMap hardwareMap, String targetColor) {
        this.targetColor = targetColor;
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(3);
        limelight.start();
    }

    private int findMinIndex(ArrayList<Double> arr) {
        int minIdx = 0;
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) < arr.get(minIdx)) {
                minIdx = i;
            }
        }
        return minIdx;
    }

    private boolean isReachable(double x, double y) {
        if (Math.abs(x) > turretLength) return false;
        double turretExtension = Math.sqrt(Math.abs(Math.pow(turretLength, 2) - Math.pow(x, 2)));
        return y - turretExtension >= 0 && y - turretExtension <= 14;
    }

    private double calculateScore(ArrayList<Double> distances) {
        return Math.abs(distances.get(0)*3) + distances.get(1);
    }

    private double calculateAngle(List<List<Double>> corners) {
        // estimate angle based on aspect ratio: ex, 1 to 1 = 45 deg
        // oh wait this wouldnt work since you don't know which direction it is
        double x0 = corners.get(0).get(0); // top-left x
        double y0 = corners.get(0).get(1); // top-left y
        double x1 = corners.get(1).get(0); // top-right x
        double y1 = corners.get(1).get(1); // top-right y

        double dx = Math.abs(x1 - x0);
        double dy = Math.abs(y1 - y0);



        if (dy > dx) {
            return 0;
        }
        return 90;
    }

    public void update() {
        LLResult result = limelight.getLatestResult();
        ArrayList<ArrayList<Double>> distances = new ArrayList<>();
        ArrayList<Double> scores = new ArrayList<>();

        if (result != null) {
            if (result.isValid()) {
                staleness = result.getStaleness();
                // double[] pythonOutput = result.getPythonOutput();
                List<LLResultTypes.DetectorResult> detectorResults = result.getDetectorResults();
                for (LLResultTypes.DetectorResult dr : detectorResults) {
                    String className = dr.getClassName(); // What was detected
                    if (Objects.equals(className, targetColor)) {
                        double yD = height * Math.tan(Math.toRadians(90 - degreesFromVertical + dr.getTargetYDegrees()));
                        double xD = Math.tan(Math.toRadians(dr.getTargetXDegrees())) * yD - lateralOffset;
                        ArrayList<Double> dists = new ArrayList<>();
                        dists.add(xD);
                        dists.add(yD);
                        dists.add(calculateAngle(dr.getTargetCorners()));

                        // do the processing step of isreachable
                        if (isReachable(xD, yD)) {
                            distances.add(dists);
                            scores.add(calculateScore(dists));
                        }
                    }
                }

            }
        }
        if (!distances.isEmpty()) {
            int minIndex = findMinIndex(scores);
            distX = distances.get(minIndex).get(0);
            distY = distances.get(minIndex).get(1);
            angle = distances.get(minIndex).get(2);
        }
    }
    public double[] getPositions() {
        if (distX == 0 && distY == 0 && angle == 0) {
            return new double[] {0, 0, 0};
        }
        double turretAngle = Math.toDegrees(Math.asin(distX / turretLength));
        double turretExtension = Math.sqrt(Math.abs(Math.pow(turretLength, 2) - Math.pow(distX, 2)));
        double wristAngle = -turretAngle + angle > 90 ? -turretAngle - angle : -turretAngle + angle;

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

