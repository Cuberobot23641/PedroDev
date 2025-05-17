package robot.robots;

import com.pedropathing.util.Timer;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;
import robot.subsystems.Intake;
import robot.subsystems.Deposit;
import robot.subsystems.Lift;
import robot.subsystems.Extension;
import java.util.List;
import static robot.RobotConstants.*;

public class AutoRobot {
    List<LynxModule> allHubs;
    private HardwareMap hardwareMap;
    public Lift lift;
    public Extension extension;
    public Deposit deposit;
    public Intake intake;
    public Timer releaseSpecTimer, retractTimer, grabSpecTimer, scoreTimer, transferTimer, prepareGrabTimer;
    public int releaseSpecState, retractState, grabSpecState, scoreState, transferState, prepareGrabState;
    public double extensionInches;
    public double turretAngle;
    public double wristAngle;

    public AutoRobot(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;

        releaseSpecTimer = new Timer();
        retractTimer = new Timer();
        grabSpecTimer = new Timer();
        scoreTimer = new Timer();
        transferTimer = new Timer();
        prepareGrabTimer = new Timer();

        intake = new Intake(this.hardwareMap);
        deposit = new Deposit(this.hardwareMap);
        lift = new Lift(this.hardwareMap, true);
        extension = new Extension(this.hardwareMap, true);

        // init positions
        intake.setTurretAngle(INTAKE_TURRET_DROP_OFF);
        intake.setWristPos(INTAKE_WRIST_DROP_OFF);
        intake.setElbowIntakePos(INTAKE_ELBOW_DROP_OFF);
        intake.openIntakeClaw();

        deposit.closeDepositClaw();
        deposit.setElbowDepositPos(DEPOSIT_ELBOW_SPEC_GRAB);
        deposit.retractLinkage();

        lift.setTargetPos(LIFT_TRANSFER);
        extension.setTargetPos(EXTENSION_MIN);

        allHubs = this.hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }
    }

    public void loop() {
        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }

        releaseSpecGrabSample();
        retract();
        grabSpecReleaseSample();
        score();
        transfer();
        prepareGrab();

        extension.loop();
        lift.loop();
    }
    public void releaseSpecGrabSample() {
        switch (releaseSpecState) {
            case 1:
                deposit.openDepositClaw();
                intake.setWristAngle(wristAngle);
                intake.setTurretAngle(turretAngle);
                extension.setTargetInches(extensionInches);
                setReleaseSpecState(2);
                break;
            case 2:
                if (releaseSpecTimer.getElapsedTimeSeconds() > 0.3) {
                    intake.setElbowIntakePos(INTAKE_ELBOW_DOWN);
                    deposit.setElbowDepositPos(DEPOSIT_ELBOW_SPEC_GRAB);
                    setReleaseSpecState(3);
                }
                break;
            case 3:
                if (releaseSpecTimer.getElapsedTimeSeconds() > 0.3) {
                    intake.closeIntakeClaw();
                    deposit.retractLinkage();
                    lift.setTargetPos(LIFT_SPEC_GRAB);
                    setReleaseSpecState(4);
                }
                break;
            case 4:
                if (releaseSpecTimer.getElapsedTimeSeconds() > 0.2) {
                    setReleaseSpecState(-1);
                }
                break;
        }
    }
    public void setReleaseSpecState(int x) {
        releaseSpecState = x;
        releaseSpecTimer.resetTimer();
    }
    public void startReleaseSpecGrabSample() {
        setReleaseSpecState(1);
    }

    public boolean isReleaseSpecFinished() {
        return releaseSpecState == -1;
    }

    public void retract() {
        switch (retractState) {
            case 1:
                intake.setElbowIntakePos(INTAKE_ELBOW_DROP_OFF);
                extension.setTargetPos(EXTENSION_MIN);
                setRetractState(2);
                break;
            case 2:
                if (retractTimer.getElapsedTimeSeconds() > 0.4) {
                    intake.setWristPos(INTAKE_WRIST_DEFAULT);
                    intake.setTurretPos(INTAKE_TURRET_DROP_OFF);
                    setRetractState(-1);
                }
                break;
        }
    }
    public void setRetractState(int x) {
        retractState = x;
        retractTimer.resetTimer();
    }
    public void startRetract() {
        setRetractState(1);
    }
    public boolean isRetractFinished() {
        return retractState == -1;
    }

    public void grabSpecReleaseSample() {
        switch (grabSpecState) {
            case 1:
                deposit.closeDepositClaw();
                intake.openIntakeClaw();
                setGrabSpecState(2);
                break;
            case 2:
                if (grabSpecTimer.getElapsedTimeSeconds() > 0.2) {
                    setGrabSpecState(-1);
                }
                break;
        }
    }
    public void setGrabSpecState(int x) {
        grabSpecState = x;
        grabSpecTimer.resetTimer();
    }
    public void startGrabSpecReleaseSample() {
        setGrabSpecState(1);
    }

    public boolean isGrabSpecFinished() {
        return grabSpecState == -1;
    }

    public void score() {
        switch (scoreState) {
            case 1:
                lift.setTargetPos(LIFT_SPEC_SCORE);
                deposit.extendLinkage();
                setScoreState(2);
                break;
            case 2:
                if (scoreTimer.getElapsedTimeSeconds() > 0.5) {
                    deposit.setElbowDepositPos(DEPOSIT_ELBOW_SPEC_SCORE);
                    setScoreState(-1);
                }
                break;
        }
    }
    public void setScoreState(int x) {
        scoreState = x;
        scoreTimer.resetTimer();
    }
    public void startScore() {
        setScoreState(1);
    }
    public boolean isScoreFinished() {
        return scoreState == -1;
    }

    public void transfer() {
        switch (transferState) {
            case 1:
                intake.closeIntakeClaw();
                deposit.setElbowDepositPos(DEPOSIT_ELBOW_TRANSFER);
                setTransferState(2);
                break;
            case 2:
                if (transferTimer.getElapsedTimeSeconds() > 0.3) {
                    intake.setWristPos(INTAKE_WRIST_DEFAULT);
                    intake.setElbowIntakePos(INTAKE_ELBOW_TRANSFER);
                    intake.setTurretPos(INTAKE_TURRET_TRANSFER);
                    setTransferState(3);
                }
                break;
            case 3:
                if (transferTimer.getElapsedTimeSeconds() > 0.2) {
                    extension.setTargetPos(EXTENSION_TRANSFER);
                    setTransferState(4);
                }
                break;
            case 4:
                if (transferTimer.getElapsedTimeSeconds() > 0.3) {
                    deposit.closeDepositClaw();
                    setTransferState(5);
                }
                break;
            case 5:
                if (transferTimer.getElapsedTimeSeconds() > 0.1) {
                    intake.openIntakeClaw();
                    setTransferState(6);
                }
                break;
            case 6:
                if (transferTimer.getElapsedTimeSeconds() > 0.1) {
                    deposit.setElbowDepositPos(DEPOSIT_ELBOW_SPEC_GRAB+0.1);
                    setTransferState(7);
                }
                break;
            case 7:
                if (transferTimer.getElapsedTimeSeconds() > 0.3) {
                    extension.setTargetPos(EXTENSION_MAX);
                    intake.setElbowIntakePos(INTAKE_ELBOW_DOWN);
                    // TODO: set to custom positions (this changes on third grab)
                    intake.setTurretPos(turretAngle);
                    intake.setWristAngle(wristAngle);
                    setTransferState(8);
                }
                break;
            case 8:
                if (transferTimer.getElapsedTimeSeconds() > 0.3) {
                    deposit.openDepositClaw();
                    setTransferState(9);
                }
                break;
            case 9:
                if (transferTimer.getElapsedTimeSeconds() > 0.1) {
                    setTransferState(-1);
                }
                break;
        }
    }
    public void setTransferState(int x) {
        transferState= x;
        transferTimer.resetTimer();
    }
    public void startTransfer() {
        setTransferState(1);
    }

    public boolean isTransferFinished() {
        return transferState == -1;
    }

    public boolean isTransferGrabFinished() {
        return transferState > 2;
    }

    public void prepareGrab() {
        switch (prepareGrabState) {
            case 1:
                deposit.openDepositClaw();
                setPrepareGrabState(2);
                break;
            case 2:
                if (prepareGrabTimer.getElapsedTimeSeconds() > 0.1) {
                    deposit.setElbowDepositPos(DEPOSIT_ELBOW_SPEC_GRAB);
                    deposit.retractLinkage();
                    setPrepareGrabState(3);
                }
                break;
            case 3:
                if (prepareGrabTimer.getElapsedTimeSeconds() > 0.3) {
                    lift.setTargetPos(LIFT_SPEC_GRAB);
                    setPrepareGrabState(-1);
                }
                break;
        }
    }
    public void setPrepareGrabState(int x) {
        prepareGrabState = x;
        prepareGrabTimer.resetTimer();
    }
    public void startPrepareGrab() {
        setPrepareGrabState(1);
    }
    public boolean isReleaseFinished() {
        return prepareGrabState == -1;
    }


    public void setTurretAngle(double x) {
        turretAngle = x;
    }

    public void setWristAngle(double x) {
        wristAngle = x;
    }

    public void setExtensionInches(double x) {
        extensionInches = x;
    }
}

