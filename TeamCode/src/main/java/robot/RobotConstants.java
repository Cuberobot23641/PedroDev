package robot;

public final class RobotConstants {
    private RobotConstants() {}
    // TODO: put all constants here instead of in the subsystems

    // lift
    public static final int LIFT_SPEC_GRAB = 450;
    public static final int LIFT_SPEC_SCORE = 1150;
    public static final int LIFT_SAMPLE_HIGH = 2200;
    public static final int LIFT_SAMPLE_LOW = 1000;
    public static final int LIFT_TRANSFER = 0;

    // extension
    public static final int EXTENSION_MAX = 440;
    public static final int EXTENSION_MID = 220;
    public static final int EXTENSION_MIN = 0;
    public static final int EXTENSION_TRANSFER = 180;

    // deposit
    public static final double DEPOSIT_CLAW_OPEN = 0.05;
    public static final double DEPOSIT_CLAW_CLOSED = 0.35;
    public static final double DEPOSIT_ELBOW_TRANSFER = 0.91;
    public static final double DEPOSIT_ELBOW_SPEC_GRAB = 0.1;
    public static final double DEPOSIT_ELBOW_SPEC_SCORE = 0.79;
    public static final double DEPOSIT_ELBOW_SAMPLE_SCORE = 0.4;
    public static final double DEPOSIT_LINKAGE_EXTEND = 0.39;
    public static final double DEPOSIT_LINKAGE_RETRACT = 0.9;

    // intake
    public static final double INTAKE_CLAW_OPEN = 0.24;
    public static final double INTAKE_CLAW_CLOSED = 0.58;
    public static final double INTAKE_ELBOW_DEFAULT = 0.25;
    public static final double INTAKE_ELBOW_TRANSFER = 0.84;
    public static final double INTAKE_ELBOW_DOWN = 0.091;
    public static final double INTAKE_ELBOW_DROP_OFF = 0.25;
    public static final double INTAKE_ELBOW_IN = 0.6;
    public static final double INTAKE_WRIST_DEFAULT = 0.5;
    public static final double INTAKE_TURRET_DEFAULT = 0.5;
    public static final double INTAKE_TURRET_DROP_OFF = 0.0;
    public static final double INTAKE_WRIST_DROP_OFF = 0.2;
    public static final double INTAKE_TURRET_TRANSFER = 0.52;
}
