package my.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * データ格納操作の共通クラス<br>
 * @author 7days
 */
public class MyBeansUtil {

    /** Getterメソッド */
    protected static final String GET = "get";
    /** Isメソッド */
    protected static final String IS = "is";
    /** Setterメソッド */
    protected static final String SET = "set";

    /** Null対象有無 */
    public static enum NullEnum {
        /** コピー除外 */
        Exclude,
        /** コピー対象 */
        Target
    }

    /** 空白対象有無 */
    public static enum EmptyEnum {
        /** コピー除外 */
        Exclude,
        /** コピー対象 */
        Target
    }

    /**
     * データ格納オブジェクトから別のデータ格納オブジェクトへ値をコピー<br>
     * （継承クラスはコピーしない）
     * @param <T>
     * @param passingBean 値を渡す格納オブジェクト
     * @param receiveClass 値を受け取る格納クラス
     * @return receiveBean 新規格納Bean
     * @throws Exception
     */
    public static <T> T copyAndCreate(Object passingBean,
                                      Class<T> receiveClass) throws Exception {
        return copyAndCreate(passingBean, receiveClass, NullEnum.Target, EmptyEnum.Target);
    }

    /**
     * データ格納オブジェクトから別のデータ格納オブジェクトへ値をコピー<br>
     * （継承クラスはコピーしない）
     * @param <T>
     * @param passingBean 値を渡す格納オブジェクト
     * @param receiveClass 値を受け取る格納クラス
     * @param nullEnum nullのコピー有無
     * @param emptyEnum 空白のコピー有無
     * @return receiveBean 新規格納Bean
     * @throws Exception
     */
    public static <T> T copyAndCreate(Object passingBean,
                                      Class<T> receiveClass,
                                      NullEnum nullEnum,
                                      EmptyEnum emptyEnum) throws Exception {
        T receiveBean = receiveClass.newInstance();
        copy(passingBean, receiveBean, nullEnum, emptyEnum);
        return receiveBean;
    }

    /**
     * データ格納オブジェクトから別のデータ格納オブジェクトへ値をコピー<br>
     * （継承クラスはコピーしない）
     * @param passingBean 値を渡す格納オブジェクト
     * @param receiveBean 値を受け取る格納オブジェクト
     * @throws Exception
     */
    public static void copy(Object passingBean,
                            Object receiveBean) throws Exception {
        copy(passingBean, receiveBean, NullEnum.Target, EmptyEnum.Target);
    }

    /**
     * データ格納オブジェクトから別のデータ格納オブジェクトへ値をコピー<br>
     * （継承クラスはコピーしない）
     * @param passingBean 値を渡す格納オブジェクト
     * @param receiveBean 値を受け取る格納オブジェクト
     * @param nullEnum nullのコピー有無
     * @param emptyEnum 空白のコピー有無
     * @throws Exception
     */
    public static void copy(Object passingBean,
                            Object receiveBean,
                            NullEnum nullEnum,
                            EmptyEnum emptyEnum) throws Exception {

        // 値を受け取るbeanの全てのメソッドを取得（継承クラスのメソッドを除く）
        Map<String, Method> receiveMethodMap = new HashMap<String, Method>();
        for (Method method : receiveBean.getClass().getDeclaredMethods()) {
            receiveMethodMap.put(method.getName(), method);
        }

        // 値を渡すbeanの全てのメソッドを取得（継承クラスのメソッドを除く）
        Method[] passingMethodList = passingBean.getClass().getDeclaredMethods();

        // get/is ⇒ set に対応して値を設定
        for (Method passingMethod : passingMethodList) {
            String passingMethodNm = passingMethod.getName();
            if (passingMethodNm.startsWith(GET) || passingMethodNm.startsWith(IS)) {
                String passingBody = headRemove(passingMethodNm);
                String receiveMethodNm = SET + passingBody;
                if (receiveMethodMap.containsKey(receiveMethodNm)) {
                    Method receiveMethod = receiveMethodMap.get(receiveMethodNm);

                    // 値を渡すbeanから値を取得
                    Object value = passingMethod.invoke(passingBean);
                    if (isSetValue(value, nullEnum, emptyEnum)) {
                        // 値を受け取るbeanに値を渡す
                        receiveMethod.invoke(receiveBean, value);
                    }
                }
            }
        }
    }

    /**
     * Mapから別のデータ格納オブジェクトへ値をコピー<br>
     * （継承クラスはコピーしない）
     * @param passingMap 値を渡すMap
     * @param receiveBean 値を受け取る格納オブジェクト
     * @throws Exception
     */
    public static void copy(Map<String, Object> passingMap,
                            Object receiveBean) throws Exception {
        copy(passingMap, receiveBean, NullEnum.Target, EmptyEnum.Target);
    }

    /**
     * Mapから別のデータ格納オブジェクトへ値をコピー<br>
     * （継承クラスはコピーしない）
     * @param passingMap 値を渡すMap
     * @param receiveBean 値を受け取る格納オブジェクト
     * @param nullEnum nullのコピー有無
     * @param emptyEnum 空白のコピー有無
     * @throws Exception
     */
    public static void copy(Map<String, Object> passingMap,
                            Object receiveBean,
                            NullEnum nullEnum,
                            EmptyEnum emptyEnum) throws Exception {

        // 値を渡すbeanの全てのメソッドを取得（継承クラスのメソッドを除く）
        Method[] receiveMethodList = receiveBean.getClass().getDeclaredMethods();

        for (Method receiveMethod : receiveMethodList) {
            String receiveMethodNm = receiveMethod.getName();
            if (receiveMethodNm.startsWith(SET)) {
                String key = topLower(headRemove(receiveMethodNm));
                if (passingMap.containsKey(key)) {
                    Object value = passingMap.get(key);
                    if (isSetValue(value, nullEnum, emptyEnum)) {
                        receiveMethod.invoke(receiveBean, value);
                    }
                }
            }
        }
    }

    /**
     * BeanからMap情報を取得する。<br>
     * （継承クラスの値は取得しない）
     * @param bean
     * @return Map [key：get/isを除いたメソッド名 / value：値]
     * @throws Exception
     */
    public static Map<String, Object> getBeanMap(Object bean) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        // 値を受け取るbeanの全てのメソッドを取得（継承クラスのメソッドを除く）
        Method[] methodList = bean.getClass().getDeclaredMethods();
        for (Method method : methodList) {
            String methodNm = method.getName();
            if (methodNm.startsWith(GET) || methodNm.startsWith(IS)) {
                String key = topLower(headRemove(methodNm));
                Object value = method.invoke(bean);
                map.put(key, value);
            }
        }
        return map;
    }

    // /**
    // * requestからDataへ自動設定
    // * @param req 値を渡すリクエスト
    // * @param bean 値を受け取る格納オブジェクト
    // * @throws Exception
    // */
    // public static void copy(HttpServletRequest req,
    // Object bean) throws Exception {
    //
    // // requestパラメータmapの取得
    // Map<String, Object> requestMap = new HashMap();
    // Iterator<Entry> ite = req.getParameterMap().entrySet().iterator();
    // while (ite.hasNext()) {
    // Entry entry = ite.next();
    // String key = (String) entry.getKey();
    // Object value = entry.getValue();
    // requestMap.put(key, value);
    // }
    //
    // // 値を受け取るDataオブジェクトのメソッドを取得
    // Method[] receiveMethodList = bean.getClass().getDeclaredMethods();
    // for (Method receiveMethod : receiveMethodList) {
    // boolean isSet = receiveMethod.getName().startsWith(SET);
    // if (isSet) {
    // String key = topLower(headRemove(receiveMethod.getName()));
    // if (requestMap.containsKey(key)) {
    // Object value = requestMap.get(key);
    // receiveMethod.invoke(bean, value);
    // }
    // }
    // }
    // }

    /**
     * ヘッダを除去（get/set/is)
     * @param methodNm
     * @return
     */
    protected static String headRemove(String methodNm) {
        if (methodNm.startsWith(GET)) {
            methodNm = methodNm.replaceFirst(GET, "");
        } else if (methodNm.startsWith(SET)) {
            methodNm = methodNm.replaceFirst(SET, "");
        } else if (methodNm.startsWith(IS)) {
            methodNm = methodNm.replaceFirst(IS, "");
        }
        return methodNm;
    }

    /**
     * 先頭文字を小文字化
     * @param key
     * @return
     */
    protected static String topLower(String key) {
        return key.substring(0, 1).toLowerCase() + key.substring(1, key.length());
    }

    /**
     * 値の受け渡し有無を判定する。
     * @param value
     * @param nullEnum
     * @param emptyEnum
     * @return 結果 [true：受け渡す false：受け渡さない]
     */
    protected static boolean isSetValue(Object value,
                                        NullEnum nullEnum,
                                        EmptyEnum emptyEnum) {
        boolean ret = true;
        if (value == null) {
            if (NullEnum.Exclude == nullEnum) {
                ret = false;
            }
        } else if ("".equals(value.toString())) {
            if (EmptyEnum.Exclude == emptyEnum) {
                ret = false;
            }
        }
        return ret;
    }
}
