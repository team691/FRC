package Team691.Util.camera;
import edu.wpi.first.wpilibj.camera.AxisCamera;

/**
 *
 * @author Gerard
 */
public class CameraImageInitializer
{
    
    public static AxisCamera cam;
    /**
     * Sets up the variables required for use by the camera system.
     * Call only once during robotInit. Once this method is run, an image
     * should be displayed on the driver station.
     */
    public static void initializeCamera()
    {
        cam = AxisCamera.getInstance();
        cam.writeCompression(0);
        cam.writeBrightness(10);
        cam.writeResolution(AxisCamera.ResolutionT.k160x120);
        cam.writeColorLevel(50);
    }
}
