import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;

public class Downloader {

    public static File download(URL url, String name) throws IOException {
        AppDirs.dataDir("phoenixbat").resolve("downloads").toFile().mkdirs();
        File file = AppDirs.dataDir("phoenixbat").resolve("downloads").resolve(name).toFile();
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        return file;
    }

}
