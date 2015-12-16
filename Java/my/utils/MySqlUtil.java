package my.utils;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * SQL関連の共通クラス<br>
 * @author 7days
 */
public class MySqlUtil {

    /** Logger */
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(MySqlUtil.class);

    /**
     * SQLのIN形式で返却<br>
     * 返却例）'aaa','bbb','ccc'
     * @param list
     * @return 文字列
     */
    public static String createSqlIn(List<String> list) {
        if (list == null || list.isEmpty()) return null;

        StringBuffer bf = new StringBuffer();
        for (String str : list) {
            bf.append("'").append(str).append("',");
        }
        // 最後のカンマを削除
        bf.deleteCharAt(bf.length() - 1);

        return bf.toString();
    }

    /**
     * SQLのIN形式で返却<br>
     * 返却例）'aaa','bbb','ccc'
     * @param paramList
     * @param key
     * @return 文字列
     */
    public static String createSqlIn(List<Map<String, String>> paramList,
                                     String key) {

        StringBuffer bf = new StringBuffer();

        for (Map<String, String> map : paramList) {
            bf.append("'").append(map.get(key)).append("',");
        }
        // 最後のカンマを削除
        bf.deleteCharAt(bf.length() - 1);

        return bf.toString();
    }

}
