package sk.stuba.fiit.perconik.ivda.server.filestats;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Seky on 23. 10. 2014.
 * <p/>
 * Trieda umoznuje pridavat statistiky o uprave nad subormy.
 * Nasledne umoznujevyhladavat v tychto statistikach.
 */
@NotThreadSafe
public class FilesOperationsRepository implements Serializable {
    private final Map<String, PerUserRecords> map;

    public FilesOperationsRepository() {
        map = new HashMap<>();
    }

    private PerUserRecords putIfAbsent(String user) {
        PerUserRecords found = map.get(user);
        if (found == null) {
            found = new PerUserRecords();
            map.put(user, found);
        }
        return found;
    }

    public void add(String user, String file, FileOperationRecord record) {
        PerUserRecords records = putIfAbsent(user);
        records.put(file, record);
    }

    /**
     * Spocitaj, kolko krat bol subor upraveny po danom datume.
     *
     * @param userName
     * @param file
     * @param date
     * @return
     */
    public int countOperationsAfter(String file, Date date) {
        /*
             PerUserRecords user = map.get(userName);  // time complexy ~O(1)
            if (user == null) {
                return count;
            }
         */
        int count = 0;
        Set<Map.Entry<String, PerUserRecords>> set = map.entrySet();
        for (Map.Entry<String, PerUserRecords> entry : set) {      // time complexy O(n)
            PerUserRecords user = entry.getValue();
            List<FileOperationRecord> operations = user.map.get(file); // time complexy ~O(1)
            if (operations == null) {
                continue;
            }
            for (FileOperationRecord op : operations) {  // time complexy O(n)
                if (op.getOperated().after(date)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Zbierka zaznamov pre pouzivatela.
     */
    private static class PerUserRecords implements Serializable {
        private final Map<String, List<FileOperationRecord>> map;  // file -> list of records

        private PerUserRecords() {
            this.map = new HashMap<>();
        }

        public void put(String file, FileOperationRecord record) {
            List<FileOperationRecord> found = this.map.get(file);
            if (found == null) {
                found = new ArrayList<>();
                this.map.put(file, found);
            }
            found.add(record);
        }
    }
}
