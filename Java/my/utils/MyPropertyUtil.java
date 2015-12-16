package my.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * プロパティファイル操作の共通クラス
 * @author 7days
 */
public class MyPropertyUtil {

    /** Logger */
    private static final Logger logger = Logger.getLogger(MyPropertyUtil.class);

    /**
     * プロパティファイルの読み込み
     * @param propertyFilePath プロパティファイル
     * @return Map プロパティリスト
     */
    public static Map<String, String> propertyLoad(String propertyFilePath) {

        Map<String, String> map = new HashMap<String, String>();
        try (FileInputStream fis = new FileInputStream(propertyFilePath)) {
            Properties prop = new java.util.Properties();
            prop.load(fis);

            Iterator<Entry<Object, Object>> ite = prop.entrySet().iterator();
            while (ite.hasNext()) {
                Entry<Object, Object> entry = ite.next();
                map.put(entry.getKey().toString(), entry.getValue().toString());
            }

        } catch (IOException e) {
            logger.error("property load error", e);
            return null;
        }

        return map;
    }

    /**
     * プロパティファイルの書き込み
     * @param map
     * @param propertyFilePath プロパティファイル
     * @return 結果 true:成功 false:失敗
     */
    public static boolean propertySave(Map<String, String> map,
                                       String propertyFilePath) {

        try (FileOutputStream fos = new FileOutputStream(propertyFilePath)) {
            Properties prop = new Properties();

            // map情報をプロパティにセット
            Iterator<Entry<String, String>> ite = map.entrySet().iterator();
            while (ite.hasNext()) {
                Entry<String, String> entry = ite.next();
                prop.setProperty(entry.getKey(), entry.getValue());
            }

            // 設定ファイルの書込
            prop.store(fos, null);

        } catch (IOException e) {
            logger.error("property save error", e);
            return false;
        }

        return true;
    }
}
