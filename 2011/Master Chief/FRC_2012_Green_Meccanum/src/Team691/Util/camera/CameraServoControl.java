/**
 * @TEAM: 691 Hart Burn
 * @AUTHOR: Akira "Ninja" H., Casey G., Gerard "B-Bo" B.,
 *          Bryan "Brandon" S. (Don't call him Brandon), Robert "Sir Sayer" G.,
 *          Shoma H.
 * @SEASON: 2011
 * @CONTACT: mheid2011@gmail.com (Akira "Ninja" H.)
 */

package Team691.Util.camera;

import edu.wpi.first.wpilibj.Servo;

class CameraServoControl {
    Servo vertical, horizontal;

    double nowTime;
    double thenTimeH;
    double thenTimeV;
    double speed;
    double degree = .02;
    /**
     * Constructor for the handling a vertically moving servo and a horizontally
     * moving servo for the camera.
     * @param CameraVert The servo controlling vertical movement.
     * @param CameraHoriz The servo controlling horizontal movement.
     */
    public CameraServoControl(Servo CameraVert, Servo CameraHoriz) {
        vertical = CameraVert;
        horizontal = CameraHoriz;

        //nowTime = System.currentTimeMillis();
        //thenTimeH = nowTime;
        //thenTimeV = nowTime;
    }

    /**
     * Moves the servos on the camera relative to it's current position. Call
     * this once every cycle of the main loop.
     * @param X The value of the axis bound to camera pan left-right.
     * -1.0 is full left, and 1.0 is full right.
     * @param Y The value of the axis bound to camera pan up-down.
     * -1.0 is full down, and 1.0 is full up.
     */
    public void update(double X, double Y) {

        nowTime = System.currentTimeMillis();

        setHorizontal(X);
        setVertical(Y);
    }

    /**
     * Set the horizontal servo position. Servo values range from 0.0 to 1.0
     * corresponding to the range of full left to full right.
     * @param X The horizontal servo's value.
     */
    public void setHorizontal(double X) {
      //1 degree  = 22 mili
        if (X != 0) {
            if(X<.1 && X>-.1){
                return;
            }
            speed = 22/X;
            if(horizontal.get() <= 1 || horizontal.get() >= 0)
            {
                if (nowTime - thenTimeH >= Math.abs(speed))
                {
                    if (X < 0)
                        degree *= -1;
                    horizontal.set(horizontal.get() + degree);
                    thenTimeH = nowTime;
                    degree = Math.abs(degree);
                }
            }
        }
    }

    /**
     * Set the vertical servo position. Servo values range from 0.0 to 1.0
     * corresponding to the range of full left to full right.
     * @param Y The vertical servo's value.
     */
    public void setVertical(double Y) {
      //1 degree   = 22 mili
        if (Y != 0) {
            if(Y<.1 && Y>-.1){
                return;
            }
            speed = 22/Y;
            if(vertical.get() <= 1 || vertical.get() >= 0)
            {
                if (nowTime - thenTimeV >= Math.abs(speed))
                {
                    if (Y < 0)
                        degree *= -1;
                        vertical.set(vertical.get() + degree);

                    thenTimeV = nowTime;
                    degree = Math.abs(degree);
                }
            }
        }
    }
}/**/