package sk.stuba.fiit.perconik.ivda.util;

import org.apache.commons.lang.SerializationUtils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Seky on 30. 9. 2014.
 * Trieda zoskupuje staticke metody pre pracu s GZIP subormy.
 */
public final class  GZIP {

    private static GZIPOutputStream outputStream(File file) throws IOException {
        return new GZIPOutputStream(new FileOutputStream(file));
    }

    private static GZIPInputStream inputStream(File file) throws IOException {
        return new GZIPInputStream(new FileInputStream(file));
    }

    public static BufferedWriter write(File name) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(outputStream(name), "UTF-8"));
    }

    public static BufferedReader read(File name) throws IOException {
        return new BufferedReader(new InputStreamReader(inputStream(name), "UTF-8"));
    }

    public static void serialize(Serializable object, File file) throws IOException {
        SerializationUtils.serialize(object, new BufferedOutputStream(outputStream(file)));
    }

    public static Object deserialize(File file) throws IOException {
        return SerializationUtils.deserialize(new BufferedInputStream(inputStream(file)));
    }
}
