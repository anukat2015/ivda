package sk.stuba.fiit.perconik.ivda.server.filestats;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Seky on 23. 10. 2014.
 * Trieda, ktora reprezentuje zaznam  o operacii nad suborom.
 */
public class FileOperationRecord implements Serializable, Comparator<FileOperationRecord> {
    private static final long serialVersionUID = 3017840679078607108L;

    private final Date operated;
    private final int changeLines;  // Pocet zmenenych riadkov nad suborom

    public FileOperationRecord(Date operated, int changeLines) {
        this.operated = operated;
        this.changeLines = changeLines;
    }

    public Date getOperated() {
        return operated;
    }

    public int getChangeLines() {
        return changeLines;
    }

    @Override
    public int compare(FileOperationRecord o1, FileOperationRecord o2) {
        return o1.operated.compareTo(o2.operated);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileOperationRecord that = (FileOperationRecord) o;

        if (operated != null ? !operated.equals(that.operated) : that.operated != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return operated != null ? operated.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FileOperationStat{");
        sb.append("changeLines=").append(changeLines);
        sb.append(", operated=").append(operated);
        sb.append('}');
        return sb.toString();
    }
}
