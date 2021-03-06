package org.firstinspires.ftc.teamcode.DiagnosticOps;

import org.firstinspires.ftc.teamcode.subsystems.Sensing.I2CXL;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by nonba on 1/7/2018.
 */


@TeleOp
public class UltrasonicInitPositioning extends OpMode {

    I2CXL Front;
    I2CXL Back;
    I2CXL Left;
    I2CXL Right;

    @Override
    public void init() {

        Front = hardwareMap.get(I2CXL.class, "ultsonFront");
        Back = hardwareMap.get(I2CXL.class, "ultsonBack");
        Left = hardwareMap.get(I2CXL.class, "ultsonLeft");
        Right = hardwareMap.get(I2CXL.class, "ultsonRight");

        Front.initialize();
        Back.initialize();
        Left.initialize();
        Right.initialize();

    }

    @Override
    public void loop() {

        Front = hardwareMap.get(I2CXL.class, "ultsonFront");
        Back = hardwareMap.get(I2CXL.class, "ultsonBack");
        Left = hardwareMap.get(I2CXL.class, "ultsonLeft");
        Right = hardwareMap.get(I2CXL.class, "ultsonRight");

        Front.initialize();
        Back.initialize();
        Left.initialize();
        Right.initialize();

        telemetry.addData("Front: ", Front.getDistance());
        telemetry.addData("",Front.getConnectionInfo());

        telemetry.addData("Back: ", Back.getDistance());
        telemetry.addData("",Back.getConnectionInfo());

        telemetry.addData("Left: ", Left.getDistance());
        telemetry.addData("",Left.getConnectionInfo());

        telemetry.addData("Right: ", Right.getDistance());
        telemetry.addData("",Right.getConnectionInfo());

        //gource

    }
}
