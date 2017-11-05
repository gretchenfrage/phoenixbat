import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Downloader {

    private static boolean isRedirected( Map<String, List<String>> header ) {
        for( String hv : header.get( null )) {
            if(   hv.contains( " 301 " )
                    || hv.contains( " 302 " )) return true;
        }
        return false;
    }

    public static File download(URL url, String name) throws IOException {
        AppDirs.dataDir("phoenixbat").resolve("downloads").toFile().mkdirs();
        AppDirs.dataDir("phoenixbat").resolve("downloads").resolve(name).toFile().createNewFile();
        File file = AppDirs.dataDir("phoenixbat").resolve("downloads").resolve(name).toFile();

        /*
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        Map<String, List<String>> header = http.getHeaderFields();
        while (isRedirected(header)) {
            url = new URL(header.get("Location").get(0));
            http = (HttpURLConnection) url.openConnection();
            header = http.getHeaderFields();
        }
        */

        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.flush();
        fos.close();
        rbc.close();
        return file;
    }

}
