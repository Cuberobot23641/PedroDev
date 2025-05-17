package pedroPathing;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerBuilder;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.drivetrain.MecanumConstants;
import com.pedropathing.localization.GoBildaPinpointDriver;
import com.pedropathing.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class Constants {

    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(12.88202)
            .drivePIDFFeedForward(0.01)
            .translationalPIDFFeedForward(0.02)
            .headingPIDFFeedForward(0.01)
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.01, 0, 0.0001, 0.6, 0))
            .translationalPIDFCoefficients(new PIDFCoefficients(0.1, 0, 0.01, 0))
            .headingPIDFCoefficients(new PIDFCoefficients(5, 0, 0.5, 0))
            .secondaryDrivePIDFCoefficients(new FilteredPIDFCoefficients(0.02, 0, 0.0005, 0.6, 0))
            .secondaryTranslationalPIDFCoefficients(new PIDFCoefficients(0.075, 0, 0.05, 0))
            .secondaryHeadingPIDFCoefficients(new PIDFCoefficients(1.5, 0, 0.1, 0))
            .secondaryTranslationalPIDFFeedForward(0.0005)
            .secondaryDrivePIDFFeedForward(0.01)
            .secondaryHeadingPIDFFeedForward(0.0005)
            .useSecondaryDrivePIDF(true)
            .useSecondaryHeadingPIDF(true)
            .useSecondaryTranslationalPIDF(true)
            .centripetalScaling(0.005)
            .forwardZeroPowerAcceleration(-50)
            .lateralZeroPowerAcceleration(-60);

    public static MecanumConstants driveConstants = new MecanumConstants()
            .xMovement(81.34056)
            .yMovement(65.43028)
            .maxPower(1)
            .leftFrontMotorName("front_left_drive")
            .leftRearMotorName("back_left_drive")
            .rightFrontMotorName("front_right_drive")
            .rightRearMotorName("back_right_drive")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .motorCachingThreshold(0.01)
            .useBrakeModeInTeleOp(false);

    public static PinpointConstants pinpointConstants = new PinpointConstants()
            .strafeX(2.95276)
            .forwardY(-3.14961)
            .hardwareMapName("pinpoint")
            .customEncoderResolution(2000)
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD)
            .distanceUnit(DistanceUnit.INCH)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 50, 3, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(pinpointConstants)
                .pathConstraints(pathConstraints)
                .build();
    }
}