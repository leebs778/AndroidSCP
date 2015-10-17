package tothecrunch.com.androidscp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leebs on 9/13/15.
 */
public class Connection {
    private String IP;
    private String Username;
    private String Password;
    private String Nickname;
    private String Targetpath;
    private int id;

    List<Connection> connections;

    public Connection(String IP, String Username, String Password, String Nickname){
        this.IP=IP;
        this.Username=Username;
        this.Password=Password;
        this.Nickname=Nickname;
    }

    public Connection(String IP, String Username, String Password, String Nickname, String targetPath) {
        this.IP = IP;
        this.Username = Username;
        this.Password = Password;
        this.Nickname = Nickname;
        this.Targetpath = targetPath;
    }


    //Setters//
    public void setIP(String IP){this.IP = IP;}
    public void setUsername(String Username){
        this.Username = Username;
    }
    public void setPassword(String Password){this.Password = Password;}
    public void setNickname(String Nickname){this.Nickname = Nickname;}
    public void setTargetPath(String Targetpath){this.Targetpath = Targetpath;}
    public void setID(int id){this.id = id;}


    //Getters//
    public String getIP(){return this.IP;}
    public String getUsername(){
        return this.Username;
    }
    public String getPassword(){
        return this.Password;
    }
    public String getNickname(){
        return this.Nickname;
    }
    public String getTargetPath(){
        return this.Targetpath;
    }
    public int getID(){return this.id;}


    public static List<Connection> addConnection (List<Connection> connections, String[] entry) {
        connections.add(new Connection(entry[0], entry[1], entry[2], entry[3]));
        return connections;
    }
    public static List<Connection> createBlankConnectionList () {
        List<Connection> connections = new ArrayList<>();
        connections.add(new Connection("", "", "Blank", "Add a Connection"));
        return connections;
    }
    public boolean isSameConnection(Connection otherCon){
        if (this.getIP() == otherCon.getIP() &&
                this.getNickname() == otherCon.getNickname() &&
                this.getPassword() == otherCon.getPassword() &&
                this.getUsername() == otherCon.getUsername() &&
                this.getTargetPath() == otherCon.getTargetPath()){
            return true;
        }else {
            return false;
        }
    }
    @Override
    public String toString(){
        return getIP() + "," + getUsername() + "," + getPassword() + ","+ getNickname();
    }

}
