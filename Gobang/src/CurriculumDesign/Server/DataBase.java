package CurriculumDesign.Server;

import java.security.MessageDigest;
import java.sql.*;
/**
 * @Author: Ddsof_Cai
 * @Email: Ddsof_Cai@163.com
 * @version V1.0
 */
public class DataBase {
    private static final String JDBC_DEIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mysql?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=GMT\n";
    //?useUnicode=true&characterEncoding=utf8&useSSL=false
    private static final String USER = "root";
    private static final String PASS = "123456";
    private static Connection conn = null;
    private static Statement stmt = null;
    private static ResultSet rs = null;
    public static String string2MD5(String inStr){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }

    /**
     * 加密解密算法 执行一次加密，两次解密
     */
    public static String convertMD5(String inStr){

        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++){
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;

    }
    //  连接数据库
    public static void connectDB() {
        try {
            Class.forName(JDBC_DEIVER);
            //STEP 3:建立连接到数据库
//            System.out.println("正在连接数据库");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("连接数据库失败");
            e.printStackTrace();
        }
    }

    public static int checkUser(String name,String password){
        System.out.println("加密后的密码: "+password);
        password= convertMD5((password));//解密
        System.out.println("解密后的密码: "+password);

        int result = 99;
        try {
            String sqlContent = "SELECT COUNT(*) FROM game WHERE name='%s' and password='%s';";
            String sql = String.format(sqlContent, name,password);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    //  查询用户表中是否存在该用户名  返回值:0没有1有
    public static int checkName(String name) {
        int result = 99;

        try {
            String sqlContent = "SELECT COUNT(*) FROM game WHERE name='%s';";
            String sql = String.format(sqlContent, name);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    //  断开数据库
    public static void closeDB() {
        try {
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            System.out.println("关闭数据库失败");
            e.printStackTrace();
        }
        System.out.println("关闭数据库成功");
    }

    //  添加新用户   返回值:0失败1成功
    public static int addUser(String name, String password, String email) {

        try {

            if (checkName(name) == 1) {
                System.out.println("添加用户失败，该用户名已经存在。");
                return 0;
            } else {
                String sqlC = "INSERT INTO game(name,password,email) VALUE('%s','%s','%s');";
                String sql = String.format(sqlC, name, password, email);
                stmt.executeUpdate(sql);
                System.out.println("添加用户成功");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    // 增加经验
    public static void addExc(String name) {

        int exp = 0;
        try {
            String sqlContent = "SELECT * FROM game WHERE name='%s';";
            String sql = String.format(sqlContent, name);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                exp = rs.getInt("exp") + 10;
//                result=rs.getString("exc");
//                result = rs.getInt(1);
            }

            String updata = "update game set exp='%s' where name='%s';";
            String updataSql = String.format(updata, exp, name);
            stmt.executeUpdate(updataSql);
            System.out.println("用户:" + name + "增加10经验成功。");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void delExc(String name){
        int exp=0;
        try {
            String sqlContent = "SELECT * FROM game WHERE name='%s';";
            String sql = String.format(sqlContent, name);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                exp = rs.getInt("exp") - 10;
//                result=rs.getString("exc");
//                result = rs.getInt(1);
            }

            String updata = "update game set exp='%s' where name='%s';";
            String updataSql = String.format(updata, exp, name);
            stmt.executeUpdate(updataSql);
            System.out.println("用户:" + name + "减少10经验成功。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查看用户信息
    public static String selectUser(String name) {
        String password = null;
        String email = null;
//        String grade=null;
        int exp = 0;
        try {
            String sqlContent = "SELECT * FROM game WHERE name='%s';";
            String sql = String.format(sqlContent, name);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {

                password = rs.getString("password");
                exp = rs.getInt("exp");
                email = rs.getString("email");
//                grade=rs.getString("grade");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String grade=grade(exp);
        return "\n用户名："+name + "\n密码：" + password + "\n经验：" + exp + "\n邮箱：" + email+ "\n段位：" + grade;
    }

    public static String grade(int exp){
        String grade="";
        if(exp<50){
            grade="青铜";
        }else if(exp<200){
            grade="白银";
        }else if(exp<500){
            grade="黄金";
        }else if(exp<1000){
            grade="钻石";
        }else if(exp<2500){
            grade="王者";
        }else{
            grade="大神";
        }
        return grade;
    }
}
