package rk.device.launcher.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Xml;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import rk.device.launcher.utils.cache.CacheUtils;

public class SPUtils {

    private static final String XML_PATH = CacheUtils.getBaseCache() + "/";

    private static SharedPreferences sp;
    private static SharedPreferences.Editor sEditor;
    private static final String FILE_NAME = "sp_config";
    private static final String DEFAULT_STRING_VALUE = "";
    private static final int DEFAULT_INT_VALUE = 0;
    private static final long DEFAULT_LONG_VALUE = 0;


    private static SharedPreferences getSp() {
        if (sp == null) {
//            sp = CommonUtils.getContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
            sp = getSharedPreferences(Utils.getContext(), FILE_NAME);
        }
        return sp;
    }

    public static void inviSp() {
        put("version", PackageUtils.getCurrentVersion());
        FileUtils.setPermission(XML_PATH + FILE_NAME + ".xml");
    }


    /**
     * 将SharedPreferences的路径改为自定义路径
     */
    static SharedPreferences getSharedPreferences(Context context, String fileName) {
        try {
            // 获取ContextWrapper对象中的mBase变量。该变量保存了ContextImpl对象
            Field field = ContextWrapper.class.getDeclaredField("mBase");
            field.setAccessible(true);
            // 获取mBase变量
            Object obj = field.get(context);
            // 获取ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径
            field = obj.getClass().getDeclaredField("mPreferencesDir");
            field.setAccessible(true);
            // 创建自定义路径
            File file = new File(XML_PATH);
            // 修改mPreferencesDir变量的值
            field.set(obj, file);
            // 返回修改路径以后的 SharedPreferences :%FILE_PATH%/%fileName%.xml
            return context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static SharedPreferences.Editor getEditor() {
        if (sEditor == null) {
            sEditor = getSp().edit();
        }
        return sEditor;
    }


    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(key, value);
        editor.commit();
    }


    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(key, value);
        editor.commit();
    }


    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(key, value);
        editor.commit();
    }


    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putLong(key, value);
        editor.commit();
    }


    public static long getLong(String key) {
        SharedPreferences sp = getSp();
        return sp.getLong(key, DEFAULT_LONG_VALUE);
    }

    public static long getLong(String key, long defaultValue) {
        SharedPreferences sp = getSp();
        return sp.getLong(key, defaultValue);
    }


    public static String getString(String key) {
        SharedPreferences sp = getSp();
        return sp.getString(key, DEFAULT_STRING_VALUE);
    }


    public static int getInt(String key) {
        SharedPreferences sp = getSp();
        return sp.getInt(key, DEFAULT_INT_VALUE);
    }

    public static int getInt(String key, int defaultValue) {
        SharedPreferences sp = getSp();
        return sp.getInt(key, defaultValue);
    }


    public static boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences sp = getSp();
        return sp.getBoolean(key, defaultValue);
    }


    public static void put(String key, Object object) {
        SharedPreferences.Editor editor = getEditor();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }


    public static Object get(String key, Object defaultObject) {
        SharedPreferences sp = getSp();
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }


    public static void remove(String key) {
        SharedPreferences.Editor editor = getEditor();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }


    public static void clear() {
        SharedPreferences.Editor editor = getEditor();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }


    public static boolean contains(String key) {
        SharedPreferences sp = getSp();
        return sp.contains(key);
    }


    public static Map<String, ?> getAll() {
        SharedPreferences sp = getSp();
        return sp.getAll();
    }


    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();


        /**
         * 反射查找apply的方法
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }


        /**
         * 如果找到则使用apply执行，否则使用commit
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            editor.commit();
        }
    }

}