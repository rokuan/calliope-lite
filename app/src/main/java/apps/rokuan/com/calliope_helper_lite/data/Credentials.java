package apps.rokuan.com.calliope_helper_lite.data;

/**
 * Created by LEBEAU Christophe on 19/03/2017.
 */

public class Credentials {
    private String login;
    private String password;

    public Credentials(String l, String p){
        login = l;
        password = p;
    }

    public String getLogin(){
        return login;
    }

    public String getPassword(){
        return password;
    }
}
