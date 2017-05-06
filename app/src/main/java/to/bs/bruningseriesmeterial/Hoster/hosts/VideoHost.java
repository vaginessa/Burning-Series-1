package to.bs.bruningseriesmeterial.Hoster.hosts;

import java.io.IOException;

/**
 * Created by Phillipp on 12.04.2017.
 */

public abstract class VideoHost {
    public abstract String getStream(String link) throws IOException, Exception;
}
