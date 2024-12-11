package soundaura;

public class MySQL {
    private static String url = "jdbc:mysql://jo04k.h.filess.io:3307/reprodutordemusica_foxflewcap";
    private static String user = "reprodutordemusica_foxflewcap";
    private static String password = "e8c5b282c4eddd69296e886dbd4a18343fce833e";

    public static String getUrl() {
        return url;
    }
    public static String setUrl() {
        return MySQL.url;
    }

    public static String getUser() {
        return user;
    }
    public static void setUser(String user) {
        MySQL.user = user;
    }


    public static String getPassword() {
        return password;
    }
    public static void setPassword(String password) {
        MySQL.password = password;
    }

}