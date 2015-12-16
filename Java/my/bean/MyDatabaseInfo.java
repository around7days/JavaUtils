package my.bean;

import org.apache.log4j.Logger;

/**
 * DB接続情報格納クラス
 * @author 7days
 */
public class MyDatabaseInfo implements Cloneable {

    /** Logger */
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(MyDatabaseInfo.class);

    /** 設定KeyConst データベース名 */
    public static final String KEY_DB_NAME = "dbName";
    /** 設定KeyConst データベース接続先 */
    public static final String KEY_DB_URL = "dbUrl";
    /** 設定KeyConst ユーザー名 */
    public static final String KEY_DB_USER = "dbUser";
    /** 設定KeyConst パスワード */
    public static final String KEY_DB_PASS = "dbPass";
    /** 設定KeyConst 接続モード（true:コミットあり　false:コミット無し） */
    public static final String KEY_DB_MODE = "dbMode";

    /** データベース名 */
    private String dbName = "";
    /** データベース接続先 */
    private String dbUrl = "";
    /** ユーザー名 */
    private String dbUser = "";
    /** パスワード */
    private String dbPass = "";
    /** 接続モード（true:コミットあり　false:コミット無し） */
    private boolean mode = false;

    /**
     * クローンオブジェクトの生成
     */
    public Object clone() {
        try {
            return (super.clone());
        } catch (CloneNotSupportedException e) {
            throw (new InternalError(e.getMessage()));
        }
    }

    /**
     * データベース名を取得します。
     * @return データベース名
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * データベース名を設定します。
     * @param dbName データベース名
     * @return thisClassObj
     */
    public MyDatabaseInfo setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    /**
     * DB_URLを取得します。
     * @return DB_URL
     */
    public String getDbUrl() {
        return dbUrl;
    }

    /**
     * DB_URLを設定します。
     * @param dbUrl DB_URL
     * @return thisClassObj
     */
    public MyDatabaseInfo setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
        return this;
    }

    /**
     * ユーザー名を取得します。
     * @return ユーザー名
     */
    public String getDbUser() {
        return dbUser;
    }

    /**
     * ユーザー名を設定します。
     * @param dbUser ユーザー名
     * @return thisClassObj
     */
    public MyDatabaseInfo setDbUser(String dbUser) {
        this.dbUser = dbUser;
        return this;
    }

    /**
     * パスワードを取得します。
     * @return パスワード
     */
    public String getDbPass() {
        return dbPass;
    }

    /**
     * パスワードを設定します。
     * @param dbPass パスワード
     * @return thisClassObj
     */
    public MyDatabaseInfo setDbPass(String dbPass) {
        this.dbPass = dbPass;
        return this;
    }

    /**
     * 接続モード（true:コミットあり　false:コミット無し）を取得します。
     * @return 接続モード（true:コミットあり　false:コミット無し）
     */
    public boolean isMode() {
        return mode;
    }

    /**
     * 接続モード（true:コミットあり　false:コミット無し）を設定します。
     * @param mode 接続モード（true:コミットあり　false:コミット無し）
     * @return thisClassObj
     */
    public MyDatabaseInfo setMode(boolean mode) {
        this.mode = mode;
        return this;
    }

}
