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

/**
 * A class to process languages/locales and resource strings. <br>
 *  All languages are stored in a files like
 *  <br><b>/resources/lang/</b>[lang_id]<b>.properties</b>.<br>
 *  Where <i>[lang_id]</i> is a three-letters lowercase language identifier, corresponding ISO 639-2.
 *  All language files must have EVERY string resource to avoid crashing the application.
 */
public class ResStrings {

    /** A current locale bundle  */
    private static ResourceBundle bundle;

    /**
     * Currently defined languages.
     * @see #fillLangsArray
     */
    private static final String[][] languages = {
        {"eng", ""},
        {"rus", ""},
        {"ukr", ""},
        {"blg", ""}
    };

    /** Creates resource bundle with English as the default. */
    public ResStrings() {
        this("eng");
    }

    /** Creates resource bundle with the specified language. */
    public ResStrings(String langCode) {
        fillLangsArray();
        setBundle(langCode);
    }

    /** Fills language array with Languages names from resources.  */
    private static void fillLangsArray() {
        for (String[] lang : languages) {
            ResourceBundle b = java.util.ResourceBundle.getBundle("lang/" + lang[0]);
            lang[1] = b.getString("LangID"); 
        }
    }

    /** Check if language array is not filled.  */
    private static void checkLangsArray() {
        if ("".equals(languages[0][1]))
            fillLangsArray();
    }

    /** Get count of available languages.  */
    public static int getLangsCount() {
        return languages.length;
    }

    /**
     * Get language ID (3-letters) by the language number.
     * @param number - language number in the array
     */
    public static String getLangCode(int number) {
        if (number >= 0 && number <= languages.length) {
            return languages[number][0];
        }
        return "";
    }

    /**
     * Get language name by the language number.
     * @param number language number in the array
     * @return full name of the language
     */
    public static String getLangName(int number) {
        checkLangsArray();
        if (number >= 0 && number <= languages.length) {
            return languages[number][1];
        }
        return "";
    }

    /**
     * Get language number by the language number.
     * @param langCode language ID, the three-letters ISO 639-2 language code
     * @return number of this language in the array
     */
    public static int getLangNumber(String langCode) {
        checkLangsArray();
        for (int i = 0; i < languages.length; i++) {
            if (langCode.equalsIgnoreCase(languages[i][0])) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Get full language name by the language number.
     * @param langCode - language ID, the three-letters ISO 639-2 language code
     * @return full name of the language
     */
    public static String getLangName(String langCode) {
        checkLangsArray();
        for (String[] lang : languages) {
            if (langCode.equalsIgnoreCase(lang[0])) {
                return lang[1];
            }
        }
        return "";
    }

    /**
     * Sets the current language bundle.
     * @param langCode - language ID, the three-letters ISO 639-2 language code
     */
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

    /**
     * Gets the resource string from the current bundle.
     * Be careful with the parameter, that ID must be in the resource!
     * @param resID - the resource identifier.
     * @return the resource string from the desired language.
     */
    public static String getString(String resID) {
        if (bundle == null) {
            setBundle("eng");
        }
        try {
            return bundle.getString(resID);
        } catch (MissingResourceException e) {
            System.err.println("Missing resource: " + resID);
            return resID;
        }
    }
    
}
