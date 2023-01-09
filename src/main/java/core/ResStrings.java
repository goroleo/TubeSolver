/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 * 
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here: 
 * https://choosealicense.com/licenses/mit/
 * 
 * Use this as you want! ))
 */
package core;

import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResStrings {

    private static ResourceBundle bundle;

    private static final String[][] langs = {
        {"eng", ""},
        {"rus", ""},
        {"ukr", ""},
        {"blg", ""}
    };

    public ResStrings() {
        this("eng");
    }

    public ResStrings(String langCode) {
        fillLangsArray();
        setBundle(langCode);
    }

    public static void fillLangsArray() {
        for (String[] lang : langs) {
            ResourceBundle b = java.util.ResourceBundle.getBundle("lang/" + lang[0]);
            lang[1] = b.getString("LangID"); 
        }
    }

    public static void checkLangsArray() {
        if ("".equals(langs[0][1]))
            fillLangsArray();
    }

    public static int getLangsCount() {
        return langs.length;
    }

    public static String getLangCode(int number) {
        if (number >= 0 && number <= langs.length) {
            return langs[number][0];
        }
        return "";
    }

    public static String getLangName(int number) {
        checkLangsArray();
        if (number >= 0 && number <= langs.length) {
            return langs[number][1];
        }
        return "";
    }

    public static int getLangNumber(String langCode) {
        checkLangsArray();
        for (int i = 0; i < langs.length; i++) {
            if (langCode.equalsIgnoreCase(langs[i][0])) {
                return i;
            }
        }
        return 0;
    }

    public static String getLangName(String langCode) {
        checkLangsArray();
        for (String[] lang : langs) {
            if (langCode.equalsIgnoreCase(lang[0])) {
                return lang[1];
            }
        }
        return "";
    }

    public static void setBundle(int number) {
        String code = getLangCode(number);
        setBundle(code);
    }

    public static void setBundle(String langCode) {
        checkLangsArray();
        if (langCode == null || "".equals(langCode)) {
            langCode = Locale.getDefault().getISO3Language();
        }
        URL u = ResStrings.class.getResource("/lang/" + langCode + ".properties");
        if (u == null) {
            langCode = "eng";
        }

        try {
            bundle = java.util.ResourceBundle.getBundle("lang/" + langCode);
            Options.langCode = langCode;
        } catch (MissingResourceException e) {
            System.err.println("Missing resource: " + "/lang/" + langCode + ".properties");
            bundle = null;
        }
    }

    public static String getString(String resName) {
        if (bundle == null) {
            setBundle("eng");
        }
        try {
            return bundle.getString(resName);
        } catch (MissingResourceException e) {
            System.err.println("Missing resource: " + resName);
            return resName;
        }
    }
    
}
