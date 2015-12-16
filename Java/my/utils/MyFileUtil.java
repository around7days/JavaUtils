package my.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

/**
 * ファイル操作の共通クラス<br>
 * @author 7days
 */
public class MyFileUtil {

    /** Logger */
    private static final Logger logger = Logger.getLogger(MyFileUtil.class);

    /**
     * 文字コードタイプ
     */
    //@formatter:off
    public enum EncodeType {
        DEFAULT(System.getProperty("file.encoding")),
        MS932("MS932"),
        SJIS("Shift_JIS"),
        UTF8("UTF-8"),
        EUC("EUC-JP"),
        ;

        private Charset cs;
        EncodeType(String name) { this.cs = Charset.forName(name);}
        public Charset getCharset() { return cs; }
    }
    //@formatter:on

    /**
     * 改行コードタイプ
     */
    //@formatter:off
    public enum LineFeedType {
        /** デフォルト */
        DEFAULT(System.lineSeparator()),
        /** \n */
        LF("\n"),
        /** \r\n */
        CRLF("\r\n"),
        ;

        private String name;
        LineFeedType(String name) { this.name = name; }
        public String getValue() { return name; }
    }
    //@formatter:on

    /**
     * 選択されたパスに存在するファイル一覧を取得<br>
     * @param searchDir 検索対象フォルダ
     * @return 検索結果List
     */
    public static List<String> getFileList(String searchDir) {
        String regex = ".*";
        int maxDepth = Integer.MAX_VALUE;
        return getFileList(searchDir, regex, maxDepth);
    }

    /**
     * 選択されたパスに存在するファイル一覧を取得<br>
     * @param searchDir 検索対象フォルダ
     * @param regex ファイル名（正規表現）
     * @return 検索結果List
     */
    public static List<String> getFileList(String searchDir,
                                           String regex) {
        int maxDepth = Integer.MAX_VALUE;
        return getFileList(searchDir, regex, maxDepth);
    }

    /**
     * 選択されたパスに存在するファイル一覧を取得<br>
     * @param searchDir 検索対象フォルダ
     * @param maxDepth 最大階層
     * @return 検索結果List
     */
    public static List<String> getFileList(String searchDir,
                                           int maxDepth) {
        String regex = ".*";
        return getFileList(searchDir, regex, maxDepth);
    }

    /**
     * 選択されたパスに存在するファイル一覧を取得<br>
     * @param searchDir 検索対象フォルダ
     * @param regex ファイル名（正規表現）
     * @param maxDepth 最大階層
     * @return 検索結果List
     */
    public static List<String> getFileList(String searchDir,
                                           String regex,
                                           int maxDepth) {

        // 検索結果の格納List
        List<String> list = new ArrayList<String>();

        // 検索処理
        try (Stream<Path> stream = Files.walk(Paths.get(searchDir), maxDepth)) {
            stream.forEach(p -> {
                if (p.toFile().isFile() && p.getFileName().toString().matches(regex)) {
                    // ファイル、且つ、正規表現に一致
                    list.add(p.toString());
                }
            });
        } catch (IOException e) {
            logger.warn("file search error", e);
            return null;
        }

        return list;
    }

    /* JDK6以前の再帰処理によるファイル一覧の取得 */
    // private static void getFileListX(String searchDir,
    // List<String> list) {
    // // ファイルリストの取得
    // File[] listFile = new File(searchDir).listFiles();
    //
    // for (File file : listFile) {
    // if (file.isDirectory()) {
    // // ディレクトリの時、再帰的に検索を行う
    // getFileListX(file.getPath(), list);
    //
    // } else if (file.isFile()) {
    // // ファイルの時、Listに追加
    // list.add(file.getPath());
    // }
    // }
    // }

    /**
     * ファイルのコピー処理
     * @param copyFrom コピー元ファイルパス
     * @param copyTo コピー先ファイルパス
     * @return 結果 true:成功 false:失敗
     */
    public static boolean fileCopy(String copyFrom,
                                   String copyTo) {

        // 出力先フォルダの生成
        File file = new File(copyTo);
        file.getParentFile().mkdirs();

        try {
            // ファイルコピー（上書き、属性コピー）
            Files.copy(Paths.get(copyFrom),
                       Paths.get(copyTo),
                       StandardCopyOption.REPLACE_EXISTING,
                       StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            logger.error("file copy error", e);
            return false;
        }

        return true;
    }

    /**
     * ファイルの出力処理
     * @param list 出力内容
     * @param outputPath 出力先パス
     * @param appendFlg 出力設定[true:追記 false:上書]
     * @return 結果 true:成功 false:失敗
     */
    public static boolean fileOutput(List<Object> list,
                                     String outputPath,
                                     boolean appendFlg) {
        return fileOutput(list, outputPath, appendFlg, EncodeType.DEFAULT.getCharset(), LineFeedType.DEFAULT.getValue());
    }

    /**
     * ファイルの出力処理
     * @param list 出力内容
     * @param outputPath 出力先パス
     * @param appendFlg 出力設定[true:追記 false:上書]
     * @param encode 文字コード
     * @param lineFeed 改行コード
     * @return 結果 true:成功 false:失敗
     */
    public static boolean fileOutput(List<Object> list,
                                     String outputPath,
                                     boolean appendFlg,
                                     Charset encode,
                                     String lineFeed) {
        // 出力先フォルダの生成
        File outputFile = new File(outputPath);
        outputFile.getParentFile().mkdirs();

        // 出力オプションの設定
        List<OpenOption> options = new ArrayList<OpenOption>();
        options.add(StandardOpenOption.CREATE);
        options.add(StandardOpenOption.WRITE);
        if (appendFlg) {
            // 追記
            options.add(StandardOpenOption.APPEND);
        } else {
            // 上書き
            options.add(StandardOpenOption.TRUNCATE_EXISTING);
        }

        // 出力
        try (BufferedWriter bw = Files.newBufferedWriter(outputFile.toPath(),
                                                         encode,
                                                         options.toArray(new OpenOption[options.size()]))) {
            for (Object line : list) {
                bw.write(line.toString());
                bw.write(lineFeed);
            }
        } catch (IOException e) {
            logger.error("file output error", e);
            return false;
        }
        return true;
    }

    /**
     * ファイルの出力処理
     * @param obj 出力内容
     * @param outputPath 出力先パス
     * @param appendFlg 出力設定[true:追記 false:上書]
     * @return 結果 true:成功 false:失敗
     */
    public static boolean fileOutput(Object obj,
                                     String outputPath,
                                     boolean appendFlg) {
        return fileOutput(obj, outputPath, appendFlg, EncodeType.DEFAULT.getCharset());
    }

    /**
     * ファイルの出力処理
     * @param obj 出力内容
     * @param outputPath 出力先パス
     * @param appendFlg 出力設定[true:追記 false:上書]
     * @param encode 文字コード
     * @return 結果 true:成功 false:失敗
     */
    public static boolean fileOutput(Object obj,
                                     String outputPath,
                                     boolean appendFlg,
                                     Charset encode) {
        // 出力先フォルダの生成
        File outputFile = new File(outputPath);
        outputFile.getParentFile().mkdirs();

        // 出力オプションの設定
        List<OpenOption> options = new ArrayList<OpenOption>();
        options.add(StandardOpenOption.CREATE);
        options.add(StandardOpenOption.WRITE);
        if (appendFlg) {
            // 追記
            options.add(StandardOpenOption.APPEND);
        } else {
            // 上書き
            options.add(StandardOpenOption.TRUNCATE_EXISTING);
        }

        // 出力
        try (BufferedWriter bw = Files.newBufferedWriter(outputFile.toPath(),
                                                         encode,
                                                         options.toArray(new OpenOption[options.size()]))) {
            bw.write(obj.toString());
        } catch (IOException e) {
            logger.error("file output error", e);
            return false;
        }
        return true;
    }

    /**
     * ファイルの内容をListに格納<br>
     * @param root ファイルパス
     * @return list ファイル内容
     */
    public static List<String> fileReader(String root) {
        return fileReader(root, EncodeType.DEFAULT.getCharset());
    }

    /**
     * ファイルの内容をListに格納
     * @param root ファイルパス
     * @param encode 文字コード
     * @return list ファイル内容
     */
    public static List<String> fileReader(String root,
                                          Charset encode) {
        // 格納Listの生成
        List<String> list = new ArrayList<String>();

        try {
            list = Files.readAllLines(Paths.get(root), encode);
        } catch (IOException e) {
            logger.error("file read error", e);
            return null;
        }

        return list;
    }

    /**
     * ファイルの内容を文字列に格納<br>
     * @param root ファイルパス
     * @return str ファイル内容
     */
    public static String fileReaderStr(String root) {
        return fileReaderStr(root, EncodeType.DEFAULT.getCharset());
    }

    /**
     * ファイルの内容を文字列に格納
     * @param root ファイルパス
     * @param encode 文字コード
     * @return str ファイル内容
     */
    public static String fileReaderStr(String root,
                                       Charset encode) {
        List<String> list = fileReader(root, encode);
        if (list == null) {
            return null;
        }

        StringBuilder bf = new StringBuilder();
        for (String str : list) {
            bf.append(str);
            bf.append(LineFeedType.DEFAULT.getValue());
        }

        return bf.toString();
    }

    /**
     * ファイルの移動処理
     * @param moveFrom 移動元ファイルパス
     * @param moveTo 移動先ファイルパス
     * @return 結果 true:成功 false:失敗
     */
    public static boolean fileMove(String moveFrom,
                                   String moveTo) {

        try {
            // ファイル移動（上書き）
            Files.move(Paths.get(moveFrom),
                       Paths.get(moveTo),
                       StandardCopyOption.REPLACE_EXISTING,
                       StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            logger.error("file move error", e);
            return false;
        }
        return true;
    }

    /**
     * ファイル/ディレクトリの削除処理<br>
     * （単一ファイル・フォルダのみ）
     * @param root ファイルパス
     * @return 結果 true:成功 false:失敗
     */
    public static boolean fileDelete(String root) {
        // ファイルオブジェクトの生成
        File file = new File(root);
        if (!file.exists()) return false;

        // 削除
        return file.delete();
    }

    /**
     * ファイル/ディレクトリの削除処理<br>
     * （配下ファイルも削除）
     * @param root ファイルパス
     * @return 結果 true:成功 false:失敗
     */
    public static boolean fileDeleteRepeat(String root) {
        // ファイルオブジェクトの生成
        File file = new File(root);
        if (!file.exists()) return false;

        if (file.isDirectory()) {
            // rootパスがディレクトリの時、再帰的処理を実行
            File[] listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                fileDeleteRepeat(listFile[i].getPath());
            }
        }

        // 削除
        return file.delete();
    }

    /**
     * ファイルの存在チェック
     * @param root ファイルパス
     * @return 結果 true:存在する false:存在しない
     */
    public static boolean isFile(String root) {
        if (root == null) return false;

        File file = new File(root);
        return file.exists() && file.isFile();
    }

    /**
     * ディレクトリの存在チェック
     * @param root ディレクトリパス
     * @return 結果 true:存在する false:存在しない
     */
    public static boolean isDirectory(String root) {
        if (root == null) return false;

        File file = new File(root);
        return file.exists() && file.isDirectory();
    }

}
