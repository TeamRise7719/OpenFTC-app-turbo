package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.Transitioning.AutoTransitioner;
import org.firstinspires.ftc.teamcode.subsystems.Driving.FestusLift;
import org.firstinspires.ftc.teamcode.subsystems.Driving.SeansEncLibrary;
import org.firstinspires.ftc.teamcode.subsystems.Sensing.I2CXL;
import org.firstinspires.ftc.teamcode.subsystems.Sensing.RobotVision;
import org.firstinspires.ftc.teamcode.subsystems.Driving.ServoManagementV2;

/**
 * Created by seancardosi on 12/5/17.
 */

@Autonomous(name = "Festus_Auto_Red_2_PID", group = "Festus")
public class Festus_Auto_Red_2_PID extends LinearOpMode {
    ColorSensor color;
    I2CXL ultrasonicBack;

    RobotVision vMod;
    ServoManagementV2 srvo;
    SeansEncLibrary enc;
    FestusLift lift;

    ElapsedTime etime = new ElapsedTime();

    int position = 0;

    public void waitFor(int time){
        time = time/1000;
        etime.reset();
        while ((etime.time() < time)&&(opModeIsActive())) {
            idle();
        }
    }

    public void runOpMode() throws InterruptedException {

        //-----------------------------------------=+(Hardware Map)+=-----------------------------------------\\
        srvo = new ServoManagementV2(hardwareMap);
        srvo.init();

        vMod = new RobotVision(hardwareMap, telemetry);
        vMod.init();

        enc = new SeansEncLibrary(hardwareMap, telemetry,this);
        enc.init();

        lift = new FestusLift(hardwareMap, telemetry);
        lift.init();
        lift.resetGlyphRotateMotor();

        ultrasonicBack = hardwareMap.get(I2CXL.class, "ultsonBack");
        ultrasonicBack.initialize();

        color = hardwareMap.colorSensor.get("color");
        color.enableLed(true);

        //-----------------------------------------=+(Hardware Map)+=-----------------------------------------\\


        //-------------------------------------=+(Initialization Config)+=------------------------------------\\
        srvo.raiseJewel();
        lift.rotateGlyphDown();

        while (!isStarted()){
            vMod.getVuMark();
            telemetry.addData(">", "Robot Ready!");
            if (vMod.vuMark == RelicRecoveryVuMark.LEFT) {
                telemetry.addData("VuMark Status - ", "Left");
                position = 2;
            } else if (vMod.vuMark == RelicRecoveryVuMark.CENTER) {
                telemetry.addData("VuMark Status - ", "Center");
                position = 1;
            } else if (vMod.vuMark == RelicRecoveryVuMark.RIGHT) {
                telemetry.addData("VuMark Status - ", "Right");
                position = 0;
            }
            if (vMod.vuMark == RelicRecoveryVuMark.UNKNOWN) {
                telemetry.addData("VuMark Status - ", "Unknown");
                position = 1;
            }
            //Display Position
            telemetry.addData("Position:", position);
            telemetry.update();
            idle();
        }

        //-------------------------------------=+(Initialization Config)+=------------------------------------\\

        // Note we use opModeIsActive() as our loop condition because it is an interruptible method.
        while (opModeIsActive()) {
            //-----------------------------------------=+(Autonomous)+=-------------------------------------------\\

            vMod.closeCamera();

            //Step 1: Close The Claw
            srvo.closeClaw();
            waitFor(1000);

            //Step 2: Lift Cube
            lift.moveLiftTime(-0.4,1.25,this);

            //Step 3: Lower Jewel Arm
            srvo.lowerJewel();
            waitFor(1500);

            //Reset time for Jewel method
            etime.reset();
            while ((etime.time() < 5)&&(opModeIsActive())) {
                //Step 4: Jewel Knock Method
                if (color.red() > color.blue()) {//if red
                    //Knock off Blue
                    srvo.knockJewel(0.5);
                    waitFor(1000);
                    srvo.knockJewel(0);

                    //Bring up Arm
                    srvo.raiseJewel();
                    waitFor(500);
                    break;
                } else if (color.red() < color.blue()) {//if blue
                    //Knock off Blue
                    srvo.knockJewel(-0.5);
                    waitFor(1000);
                    srvo.knockJewel(0);


                    //Bring up Arm
                    srvo.raiseJewel();
                    waitFor(500);
                    break;
                }
                telemetry.addData("RED",color.red());
                telemetry.addData("GREEN",color.green());
                telemetry.addData("BLUE",color.blue());
                telemetry.update();
            }
            //Bring up Arm
            srvo.raiseJewel();
            //waitFor(500);

            //Step 5: Drive off Balancing Stone
            enc.gyroDrive(enc.DRIVE_SPEED_SLOW, 23, 0,false);
            waitFor(1500);


            //Step 6: Turn Towards Columns
            enc.gyroTurn(enc.TURN_SPEED, 90);
            waitFor(1000);

            //Step 7: Drive to Appropriate Column
            double distance;

            if(ultrasonicBack.getDistance()>1) {
                double centerPosition = 26;
                double offset = 0;
                if (position == 0) { //Right
                    offset = -7;
                }else if (position == 2) { //Left
                    offset = 7;
                }
                distance = centerPosition+offset;

                enc.UltrasonicGyroDrive(distance, 90,false, 0.5, true, 5);
                waitFor(2000);
            }
            else{
                double centerPosition = 12.5;
                double offset = 0;

                if (position == 0) { //Right
                    offset = -7;
                } else if (position == 2) { //Left
                    offset = 7;
                }
                distance = centerPosition + offset;

                enc.gyroDrive(enc.DRIVE_SPEED_SLOW, distance, 90,false);
                waitFor(2000);
            }

            //Step 8: Turn back to 0 Degrees
            enc.gyroTurn(enc.TURN_SPEED, 0);
            waitFor(500);

            //Step 9: Lift
            lift.moveLiftTime(0.25,0.5,this);

            //Step 10: Push Glyph into Column
            waitFor(500);
            enc.gyroDrive(enc.DRIVE_SPEED, 10, 0,false);
            waitFor(1000);
            srvo.openClaw();
            waitFor(500);
            enc.gyroDrive(enc.DRIVE_SPEED, -8, 0,false);
            waitFor(1000);

            //Step 11: Turn around towards field
            enc.gyroTurn(enc.TURN_SPEED, 90);

            //Step 12: Open Claw
            srvo.openClaw();

            //End While Loop
            break;
        }

        srvo.clawStop1();
        srvo.clawStop2();
        AutoTransitioner.transitionOnStop(this, "EvanTeleOp");
    }
}
