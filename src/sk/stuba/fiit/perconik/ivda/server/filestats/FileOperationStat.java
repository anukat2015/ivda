package sk.stuba.fiit.perconik.ivda.server.filestats;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Seky on 23. 10. 2014.
 * Trieda, ktora nam hovori o operacii nad suborom.
 */
public class FileOperationStat implements Serializable {
    private Date operated;
    private int changeLines;  // Pocet zmenenych riadkov nad suborom

    public Date getOperated() {
        return operated;
    }

    public void setOperated(Date operated) {
        this.operated = operated;
    }

    public int getChangeLines() {
        return changeLines;
    }

    public void setChangeLines(int changeLines) {
        this.changeLines = changeLines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileOperationStat that = (FileOperationStat) o;

        if (changeLines != that.changeLines) return false;
        if (operated != null ? !operated.equals(that.operated) : that.operated != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = operated != null ? operated.hashCode() : 0;
        result = 31 * result + changeLines;
        return result;
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
