package robot.subsystems;

import static robot.RobotConstants.*;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Deposit {
    private Servo elbowDeposit;
    private Servo linkageDeposit;
    private Servo clawDeposit;
    private HardwareMap hardwareMap;
    public Deposit(HardwareMap hardwareMap){
        this.hardwareMap = hardwareMap;
        clawDeposit = this.hardwareMap.get(Servo.class, "clawDeposit");
        clawDeposit.setDirection(Servo.Direction.FORWARD);
        elbowDeposit = this.hardwareMap.get(Servo.class, "elbowDeposit");
        elbowDeposit.setDirection(Servo.Direction.FORWARD);
        linkageDeposit = this.hardwareMap.get(Servo.class, "linkageDeposit");
        linkageDeposit.setDirection(Servo.Direction.FORWARD);
    }


    public void loop() {}

    public void setElbowDepositPos(double pos){
        elbowDeposit.setPosition(pos);
    }
    public void setClawDepositPos(double pos){
        clawDeposit.setPosition(pos);
    }

    public void closeDepositClaw() {
        clawDeposit.setPosition(DEPOSIT_CLAW_CLOSED);
    }

    public void openDepositClaw() {
        clawDeposit.setPosition(DEPOSIT_CLAW_OPEN);
    }

    public void extendLinkage() {linkageDeposit.setPosition(DEPOSIT_LINKAGE_EXTEND);}

    public void retractLinkage() {linkageDeposit.setPosition(DEPOSIT_LINKAGE_RETRACT);}

    public void setLinkagePos(double pos) {linkageDeposit.setPosition(pos);}

}

