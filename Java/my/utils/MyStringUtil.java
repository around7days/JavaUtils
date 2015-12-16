package my.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * 文字列操作の共通クラス<br>
 * @author 7days
 */
public class MyStringUtil {

    /** Logger */
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(MyStringUtil.class);

    /** 文字コード シフトJIS */
    public static final String ENCODE_TYPE_SJIS = "Shift_JIS";
    /** 文字コード UTF-8 */
    public static final String ENCODE_TYPE_UTF8 = "UTF8";
    /** 文字コード EUC */
    public static final String ENCODE_TYPE_EUC = "EUC-JP";
    /** 文字コード Windows-31J */
    public static final String ENCODE_TYPE_WIN31J = "Windows-31J";

    /**
     * バイト数の取得<br>
     * @param str 文字列
     * @return int バイト数
     */
    public static int getByte(String str) {
        if (str == null || "".equals(str)) return 0;

        byte b[] = str.getBytes();
        return b.length;
    }

    /**
     * 数字（半角）のチェックを行う。<br>
     * @param str 対象文字列
     * @return boolean [true:数字 false:数字以外]
     */
    public static boolean isInteger(String str) {
        if (str == null || "".equals(str)) return false;

        try {
            Long.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 日付の妥当性チェックを行います。<br>
     * （yyyyMMdd or yyyy/MM/dd or yyyy-MM-dd）
     * @param strDate チェック対象の文字列
     * @return boolean [true:日付 false:日付以外]
     */
    public static boolean isDate(String strDate) {
        if (strDate == null || "".equals(strDate)) return false;

        strDate = strDate.replace('-', '/');
        DateFormat format = DateFormat.getDateInstance();
        format.setLenient(false);
        try {
            format.parse(strDate);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 文字列の比較を行う。<br>
     * （可変長）
     * @param str 文字列
     * @param checkStr 比較対象文字列（配列）
     * @return boolean [true:一致 false:全て不一致]
     */
    public static boolean equalsIn(String str,
                                   String... checkStr) {
        if (str == null || "".equals(str)) return false;

        boolean b = false;
        for (String check : checkStr) {
            if (str.equals(check)) {
                b = true;
                break;
            }
        }

        return b;
    }

    /**
     * 空白(null含む)チェック<br>
     * @param str 文字列
     * @return boolean [true:空白(null含む) false:空白でない]
     */
    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str));
    }

    /**
     * Listの空白(null含む)チェック<br>
     * @param list リスト
     * @return boolean [true:空白(null含む) false:空白でない]
     */
    public static boolean isEmpty(List<?> list) {
        return (list == null || list.isEmpty());
    }

    /**
     * 空白(null含む)チェック<br>
     * @param str 文字列
     * @return boolean [true:空白でない false:空白(null含む)]
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Listの空白(null含む)チェック<br>
     * @param list リスト
     * @return boolean [true:空白でない false:空白(null含む)]
     */
    public static boolean isNotEmpty(List<?> list) {
        return !isEmpty(list);
    }

    /**
     * 文字列がnullの時、空白("")を返す。<br>
     * @param str 文字列
     * @return String 文字列
     */
    public static String nullNvl(String str) {
        return (str == null ? "" : str);
    }

    /**
     * ファイルパスからファイル名のみを取り出す。<br>
     * 出力例）hoge.txt
     * @param root ファイルパス
     * @return ファイル名
     */
    public static String getFileNm(String root) {
        if (root == null || "".equals(root)) return "";

        return new File(root).getName();
    }

    /**
     * ファイルパスから親ディレクトリパスを取り出す。<br>
     * 出力例）C:\hoge
     * @param root ファイルパス
     * @return ファイル名
     */
    public static String getFileParent(String root) {
        if (root == null || "".equals(root)) return "";

        return new File(root).getParent();
    }

    /**
     * 拡張子を取り出す。<br>
     * 出力例）txt
     * @param root ファイルパスorファイル名
     * @return 拡張子
     */
    public static String getFileFormat(String root) {
        if (root == null || "".equals(root)) return "";

        int lastIndex = root.lastIndexOf(".");
        if (lastIndex == -1) return "";

        return root.substring(lastIndex + 1);
    }

    /**
     * 指定桁数（文字数）まで、指定文字列埋めを行う<br>
     * 出力例）[12 ]
     * @param str 文字列埋めを行う対象文字列
     * @param length この桁数まで文字埋めを行う。
     * @return String
     */
    public static String paddingCharString(String str,
                                           int length) {
        if (str == null) return "";

        int size = length - str.length();
        StringBuffer value = new StringBuffer();
        for (int i = 0; i < size; i++) {
            value.append(" ");
        }
        return str + value.toString();
    }

    /**
     * Map領域(テーブルデータ)から指定されたカラムの値を取得する。<br>
     * (null文字の場合、空白を返す。)
     * @param mapDate Map領域
     * @param column カラム名
     * @return String
     */
    public static String getMapStrColume(Map<?, ?> mapDate,
                                         Object column) {
        if (mapDate == null) return "";

        return nullNvl(String.valueOf(mapDate.get(column)));
    }

    /**
     * Exceptionの内容を文字列で返却する。
     * @param e
     * @return String
     */
    public static String getPrintStackTrace(Exception e) {

        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));

        return e.toString();
    }

    /**
     * String型の文字列（数字）に＋１する。<br>
     * （桁数を保持）
     * @param strNumber
     * @return strNumber+1
     */
    public static String countUp(String strNumber) {

        // 元の桁数を保持
        int digit = strNumber.length();

        // int型に変換して+1
        int newNumber = Integer.valueOf(strNumber);
        newNumber++;

        // String型に変換し、元の桁数に戻す
        strNumber = String.valueOf(newNumber);
        while (strNumber.length() < digit) {
            strNumber = "0" + strNumber;
        }

        return strNumber;
    }

    /**
     * マップメッセージの中身を表示する
     * @param map
     */
    public static void printMapMessage(Map<String, Object> map) {
        StringBuffer bf = new StringBuffer();

        Iterator<Entry<String, Object>> ite = map.entrySet().iterator();
        while (ite.hasNext()) {
            Entry<String, Object> entry = ite.next();
            bf.append(entry.toString() + "\n");
        }

        System.out.println(bf.toString());
    }

    /**
     * リストの中身を表示する
     * @param list
     */
    public static void printList(List<?> list) {
        for (Object obj : list) {
            System.out.println(obj.toString());
        }
    }

    /**
     * メモリ使用量を表示する
     */
    public static void printMemory() {
        DecimalFormat f1 = new DecimalFormat("#,###KB");
        DecimalFormat f2 = new DecimalFormat("##.#");
        long free = Runtime.getRuntime().freeMemory() / 1024;
        long total = Runtime.getRuntime().totalMemory() / 1024;
        long max = Runtime.getRuntime().maxMemory() / 1024;
        long used = total - free;
        double ratio = (used * 100 / (double) total);

        StringBuffer bf = new StringBuffer();
        bf.append("[Java メモリ情報] \n");
        bf.append("合計=" + f1.format(total) + "\n");
        bf.append("使用量=" + f1.format(used) + " (" + f2.format(ratio) + "%)\n");
        bf.append("使用可能最大=" + f1.format(max));

        System.out.println(bf.toString());
    }

    /**
     * クリップボードの値を受け取る
     * @return 文字列
     */
    public static String getClipboard() {

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable object = clipboard.getContents(null);
        String str = "";
        try {
            str = (String) object.getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }

        return str;
    }

    /**
     * クリップボードに値を設定する
     * @param str
     */
    public static void setClipboard(String str) {
        if (str == null) return;

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(str);
        clipboard.setContents(selection, null);
    }

    /**
     * デスクトップのパスを取得する
     * @return デスクトップパス
     */
    public static String getDesktopPath() {
        File file = new File(System.getProperty("user.home"), "デスクトップ");
        return file.getPath();
    }

    /**
     * カレントディレクトリのパスを取得する
     * @return カレントディレクトリ
     */
    public static String getCurrentDirPath() {
        return System.getProperty("user.dir");
    }

    /**
     * キャメルケース化変換（先頭大文字）<br>
     * 例）CAMEL_CASE ⇒ CamelCase
     * @param inputValue 文字列
     * @return キャメルケース項目
     */
    public static String camelCaseUpper(String inputValue) {
        String value = camelCaseLower(inputValue);
        return Character.toUpperCase(value.charAt(0)) + value.substring(1, value.length());
    }

    /**
     * キャメルケース化変換（先頭小文字）<br>
     * 例）CAMEL_CASE ⇒ camelCase
     * @param inputValue 文字列
     * @return キャメルケース項目
     */
    public static String camelCaseLower(String inputValue) {
        StringBuffer bf = new StringBuffer();
        for (int i = 0; i < inputValue.length(); i++) {
            char token = inputValue.charAt(i);
            if ('_' == token || ' ' == token) {
                // 「_」or「 」の時、次の文字を大文字に変換
                token = inputValue.charAt(++i);
                bf.append(Character.toUpperCase(token));
            } else {
                // 上記以外の時、小文字に変換
                bf.append(Character.toLowerCase(token));
            }
        }
        return bf.toString();
    }

    /**
     * アンキャメルケース化変換）<br>
     * 例）camelCase ⇒ CAMEL_CASE
     * @param inputValue 文字列
     * @return アンキャメルケース項目
     */
    public static String unCamelCase(String inputValue) {
        StringBuffer bf = new StringBuffer();
        for (int i = 0; i < inputValue.length(); i++) {
            char token = inputValue.charAt(i);
            if (Character.isUpperCase(token)) {
                // 大文字の時、「_」を追加
                bf.append('_').append(token);
            } else {
                // 小文字の時、大文字に変換
                bf.append(Character.toUpperCase(token));
            }
        }
        return bf.toString();
    }

}
