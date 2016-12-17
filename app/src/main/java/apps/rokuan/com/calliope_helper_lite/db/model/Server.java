package apps.rokuan.com.calliope_helper_lite.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by LEBEAU Christophe on 16/12/2016.
 */

@DatabaseTable(tableName = "servers")
public class Server {
    @DatabaseField(generatedId = true)
    private int id;
    private String name;
    private String host;
    private int port = 7980;

    public Server(){

    }

    public Server(String n, String a, int p){
        name = n;
        host = a;
        port = p;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
