package sk.stuba.fiit.perconik.ivda.util.serialize;

import java.io.*;

/**
 * Created by Seky on 5. 11. 2014.
 */
public final class IterateOutputStream implements ObjectOutput {
    private final ObjectOutputStream out;

    public IterateOutputStream(OutputStream out) throws IOException {
        this.out = new ObjectOutputStream(out);
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        out.writeObject(obj);
    }

    @Override
    public void write(int b) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(byte[] b) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeByte(int v) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeShort(int v) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeChar(int v) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeInt(int v) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeLong(long v) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeFloat(float v) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeDouble(double v) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeBytes(String s) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeChars(String s) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeUTF(String s) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.writeObject(new EOF());
        out.close();
    }

    protected static class EOF implements Serializable {
        private static final long serialVersionUID = 8516000951966779652L;
    }
}
