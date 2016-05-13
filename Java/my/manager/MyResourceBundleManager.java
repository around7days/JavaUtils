package my.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * PropertyManagerクラス<br>
 * （シングルトン）
 * @author 7days
 */
public enum MyResourceBundleManager {
    INSTANCE;

    /** Logger */
    private static final Logger logger = Logger.getLogger(MyResourceBundleManager.class);

    /** プロパティ名(xxxxxx.properties) */
    private static final String PROPERTY_NM = "resource";

    /** プロパティ */
    private static final ResourceBundle rb = ResourceBundle.getBundle(PROPERTY_NM);
    // private static final ResourceBundle rb = ResourceBundle.getBundle(PROPERTY_NM, new ControlUtf8());

    /** プロパティ一覧出力（debug用） */
    static {
        TreeSet<String> keys = new TreeSet<String>(rb.keySet());
        for (String key : keys) {
            String val = String.format("%-35s", key) + " : " + rb.getString(key);
            logger.debug(val);
        }
    }

    /**
     * プロパティから値を取得
     * @param key
     * @return keyに対応する値
     */
    public int getInt(String key) {
        String val = getString(key);
        return Integer.valueOf(val);
    }

    /**
     * プロパティから値を取得
     * @param key
     * @return keyに対応する値
     */
    public String getString(String key) {
        if (!rb.containsKey(key)) {
            logger.warn("not contains key : " + key);
            return null;
        }
        return rb.getString(key);
    }
}

/**
 * UTF-8 エンコーディングされたプロパティファイルを {@link ResourceBundle} クラスで取り扱う。
 */
class ControlUtf8 extends Control {

    /**
     * UTF-8 エンコーディングのプロパティファイルから<br>
     * ResourceBundle オブジェクトを生成します。
     */
    @Override
    public ResourceBundle newBundle(String baseName,
                                    Locale locale,
                                    String format,
                                    ClassLoader loader,
                                    boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");

        try (InputStream is = loader.getResourceAsStream(resourceName);
             InputStreamReader isr = new InputStreamReader(is, "UTF-8");
             BufferedReader reader = new BufferedReader(isr)) {
            return new PropertyResourceBundle(reader);
        }
    }
}
