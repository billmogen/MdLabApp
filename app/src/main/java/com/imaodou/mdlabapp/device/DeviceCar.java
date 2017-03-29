package com.imaodou.mdlabapp.device;

/**
 * Created by billmogen on 2017/3/14.
 */

public class DeviceCar extends Devices{

    public static final String FORWARDCMD = "FF03010242020001FF000000FF001100000000000000"; //前进 全速 不鸣笛 不开喇叭
    public static final String BACKWARDCMD = "FF03010242020001FF000000FF001100000000000000"; //后退 全速 不鸣笛 不开喇叭
    // 0: freeMode 1: searchMode 2: avoidMode 3: gravityMode 4: irrockerMode
    public enum CarMode {
        FREEMODE,
        SEARCHMODE,
        AVOIDMODE,
        GRAVITYMODE,
        IRROCKERMODE
    }
    // 0: stopState 1: runningState 2: turnleftstate 3: turnrightstate 4: backstate
    public enum CarState {
        STOP,
        RUNNING,
        TURNLEFT,
        TURNRIGHT,
        BACKFORWARD
    }
    public int carState;
    public int carMode;
    public  int motorSpeed;
    public int steerUpAngle;
    public int steerDownAngle;
    public int buzzerState;
    public int speakerState;
    public int carLevel;
//    public boolean buzzerRecord;
    public DeviceCar() {
        //默认自由模式，停止状态，速度最大
        carState =  0;
        carMode = 0;
        motorSpeed = 255;
        buzzerState = 0;
        speakerState = 0;
        carLevel = 0;
//        buzzerRecord = true;
    }

//    public byte[] encodeMsg() {
//
//        return 1;
//    }
}
