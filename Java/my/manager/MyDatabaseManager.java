package my.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import my.bean.MyDatabaseInfo;

import org.apache.log4j.Logger;

/**
 * DatabaseManagerクラス<br>
 * （シングルトン）
 * @author 7days
 */
public enum MyDatabaseManager {
    /** 唯一のインスタンス */
    INSTANCE;

    /** Logger */
    private static final Logger logger = Logger.getLogger(MyDatabaseManager.class);

    /** DB接続情報 */
    private MyDatabaseInfo dbInfo = null;
    /** コネクション */
    private Connection conn = null;

    /**
     * DB接続情報の設定
     * @param dbinfo DB接続情報
     */
    public void setProperty(MyDatabaseInfo dbinfo) {
        // DB接続の設定を行う
        dbInfo = (MyDatabaseInfo) dbinfo.clone();
        logger.info("------------------ 接続情報 ------------------");
        logger.info("【DB接続情報】NAME   : " + dbInfo.getDbName());
        logger.info("【DB接続情報】URL    : " + dbInfo.getDbUrl());
        logger.info("【DB接続情報】USER   : " + dbInfo.getDbUser());
        logger.info("【DB接続情報】PASS   : " + dbInfo.getDbPass());
        if (dbInfo.isMode() == true) logger.debug("【DB接続モード】本番（コミットあり）");
        if (dbInfo.isMode() == false) logger.debug("【DB接続モード】テスト（コミット無し）");

        if (dbInfo.isMode() == true) {
            int time = 10;
            logger.debug("本番モード開始 待機残り" + time + "秒");
            try {
                do {
                    // 処理停止
                    Thread.sleep(1000);
                    time--;
                    logger.debug(" " + time + "秒");
                } while (time > 0);
                logger.debug("");
            } catch (InterruptedException e) {
                // 握りつぶす
            }
        }
    }

    /**
     * SELECT文の実行
     * @param sql SQL文
     * @return listMap 実行結果
     * @throws SQLException
     */
    public List<Map<String, String>> doSelect(String sql) throws SQLException {

        // 結果格納リスト
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();

        try (Statement state = conn.createStatement(); ResultSet result = state.executeQuery(sql);) {
            // Log出力
            logger.debug(sql);

            ResultSetMetaData metaData = result.getMetaData();
            while (result.next()) {
                // 結果格納Map
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    map.put(metaData.getColumnName(i), result.getString(i));
                }
                mapList.add(map);
            }
        } catch (SQLException e) {
            throw e;
        }

        return mapList;
    }

    /**
     * SELECT文の実行<br>
     * (１行)
     * @param sql SQL文
     * @return Map 実行結果
     * @throws SQLException
     */
    public Map<String, String> doSelectUnique(String sql) throws SQLException {

        // SQL実行結果の取得し、先頭行を返却
        List<Map<String, String>> mapList = doSelect(sql);
        return mapList.isEmpty() ? null : mapList.get(0);
    }

    /**
     * SELECT文の実行結果件数
     * @param sql SQL文
     * @return int 結果件数
     * @throws SQLException
     */
    public int doSelectCount(String sql) throws SQLException {
        // SQL実行結果件数の取得
        sql = "select count(*) as cnt from ( " + sql + " )";
        Map<String, String> map = doSelectUnique(sql);
        return Integer.parseInt(map.get("cnt"));
    }

    /**
     * SELECT文の実行
     * @param sql SQL文
     * @return listMap 実行結果
     * @throws SQLException
     */
    public DefaultTableModel doSelectInTableModel(String sql) throws SQLException {

        // 結果格納テーブルモデルの生成
        DefaultTableModel tableModel = new DefaultTableModel();

        // Log出力
        logger.debug(sql);

        try (Statement state = conn.createStatement(); ResultSet result = state.executeQuery(sql);) {

            // 項目名の取得
            ResultSetMetaData metaData = result.getMetaData();
            for (int i = 1; metaData != null && i <= metaData.getColumnCount(); i++) {
                String key = metaData.getColumnName(i);
                tableModel.addColumn(key);
            }

            while (result.next()) {
                // 結果格納Vector
                Vector<String> vector = new Vector<String>();
                // 値の取得
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    vector.add(result.getString(i));
                }

                // データの追加
                tableModel.addRow(vector);
            }

        } catch (SQLException e) {
            throw e;
        }

        return tableModel;
    }

    /**
     * Insert,Update,Delete文の実行
     * @param sql SQL文
     * @return count 実行件数
     * @throws SQLException
     */
    public int doUpdate(String sql) throws SQLException {

        // 実行件数
        int count = 0;

        // Log出力
        logger.debug(sql);

        try (Statement state = conn.createStatement();) {
            count = state.executeUpdate(sql);

            // Log出力
            logger.debug("実行結果 : " + count + "件");

        } catch (SQLException e) {
            // ロールバック
            rollback();
            throw e;
        }

        return count;
    }

    /**
     * コミット
     * @throws SQLException
     */
    public void commit() throws SQLException {
        if (dbInfo.isMode() == false) {
            // 接続モードがテストの時、常にロールバック
            logger.debug("test mode does not commit");
            return;
        }
        if (conn != null && !conn.isClosed()) {
            conn.commit();
            logger.info("commit");
        }
    }

    /**
     * ロールバック
     * @throws SQLException
     */
    public void rollback() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.rollback();
            logger.info("rollback");
        }
    }

    /**
     * DB接続
     * @return 結果 [true:成功 false:失敗]
     */
    public boolean DBOpen() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(dbInfo.getDbUrl(), dbInfo.getDbUser(), dbInfo.getDbPass());

            // 自動コミットOFF
            conn.setAutoCommit(false);

        } catch (SQLException | ClassNotFoundException e) {
            logger.error("database open error");
            return false;
        }

        logger.info("database open");
        return true;
    }

    /**
     * DB切断
     */
    public void DBClose() {
        try {
            if (conn != null && !conn.isClosed()) {
                if (dbInfo.isMode() == false) {
                    // 接続モードがテストの時、常にロールバック
                    rollback();
                }
                conn.close();
            }
            logger.info("database close");
        } catch (SQLException e) {
            // 握りつぶす
            logger.error("database close error", e);
        }
    }
}
