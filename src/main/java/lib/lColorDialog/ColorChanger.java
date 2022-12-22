/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 * 
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here: 
 * https://choosealicense.com/licenses/mit/
 * 
 * Use this as you want! ))
 */

package lib.lColorDialog;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/////////////////////////////////////////////////////////
//         
//         lnColorChanger
//         
/////////////////////////////////////////////////////////
// 
// The most interesting class. It changes the current color, calculates new
// values for all color components and then broadcast new values to all other
// conlrols.
//


/* --------------------------------------------------
 *  TODO: Support to LAB conversion
 */
public class ColorChanger {

    private final List<ColorListener> changeListeners = new ArrayList<>();

    private Object initiator = null;

// The current color as an integer value 
    private int clr = -1;
 // RGB color model 
    private int rgbR = 0;
    private int rgbG = 0;
    private int rgbB = 0;
 // HSB (HSV) color model 
    private float hsbH = 0.0f;
    private float hsbS = 0.0f;
    private float hsbB = 0.0f;
 // HSL color model 
    private float hslH = 0.0f;
    private float hslS = 0.0f;
    private float hslL = 0.0f;

/* (reserved for the future use)
 // XYZ color model (not used)
    private float xyzX = 0.0f;
    private float xyzY = 0.0f;
    private float xyzZ = 0.0f;
 // L*a*b* color model (not used)
    private float labL = 0.0f;
    private float labA = 0.0f;
    private float labB = 0.0f;
 */ 
    
/////////////////////////////////////////////////////////
//  Components setters
//
    public void setHSBhue(Object obj, float h) {
        setHSB(obj, h, hsbS, hsbB);
    }

    public void setHSBsat(Object obj, float s) {
        setHSB(obj, hsbH, s, hsbB);
    }

    public void setHSBbri(Object obj, float b) {
        setHSB(obj, hsbH, hsbS, b);
    }

    public void setHSLhue(Object obj, float h) {
        setHSL(obj, h, hslS, hslL);
    }

    public void setHSLsat(Object obj, float s) {
        setHSL(obj, hslH, s, hslL);
    }

    public void setHSLlight(Object obj, float l) {
        setHSL(obj, hslH, hslS, l);
    }

    public void setRed(Object obj, int r) {
        setRGB(obj, r, rgbG, rgbB);
    }

    public void setGreen(Object obj, int g) {
        setRGB(obj, rgbR, g, rgbB);
    }

    public void setBlue(Object obj, int b) {
        setRGB(obj, rgbR, rgbG, b);
    }

    public void setHSB(Object obj, float h, float s, float b) {
        boolean isChanged = false;
        if (hsbH != h) {
            hsbH = h;
            isChanged = true;
        }
        if (hsbS != s) {
            hsbS = s;
            isChanged = true;
        }
        if (hsbB != b) {
            hsbB = b;
            isChanged = true;
        }
        if (isChanged) {
            calculateRGBfromHSB();
            calculateHSLfromRGB();
//          calculateLABfromRGB(); // reserved for the future use
            clr = 0xff000000 | ((rgbR & 0xff) << 16) | ((rgbG & 0xff) << 8) | (rgbB & 0xff);
            initiator = obj;
            updateColor(clr);
            initiator = null;
        }
    }

    public void setHSL(Object obj, float h, float s, float l) {
        boolean isChanged = false;
        if (hslH != h) {
            hslH = h;
            isChanged = true;
        }
        if (hslS != s) {
            hslS = s;
            isChanged = true;
        }
        if (hslL != l) {
            hslL = l;
            isChanged = true;
        }
        if (isChanged) {
            calculateRGBfromHSL();
            calculateHSBfromRGB();
//          calculateLABfromRGB(); // reserved for the future use
            clr = 0xff000000 | ((rgbR & 0xff) << 16) | ((rgbG & 0xff) << 8) | (rgbB & 0xff);
            initiator = obj;
            updateColor(clr);
            initiator = null;
        }
    }

    public void setRGB(Object obj, int r, int g, int b) {
        boolean isChanged = false;
        if (rgbR != r) {
            rgbR = r;
            isChanged = true;
        }
        if (rgbG != g) {
            rgbG = g;
            isChanged = true;
        }
        if (rgbB != b) {
            rgbB = b;
            isChanged = true;
        }
        if (isChanged) {

            calculateHSBfromRGB();
            calculateHSLfromRGB();
//          calculateLABfromRGB(); // reserved for the future use
            clr = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);

            initiator = obj;
            updateColor(clr);
            initiator = null;
        }
    }

    public void setRGB(Object obj, int rgb) {
        setRGB(obj,
                (rgb >> 16) & 0xff,
                (rgb >> 8) & 0xff,
                (rgb) & 0xff);
    }

/////////////////////////////////////////////////////////
//  Components getters
//
    public float getHSBhue() {
        return hsbH;
    }

    public float getHSBsat() {
        return hsbS;
    }

    public float getHSBbri() {
        return hsbB;
    }

    public float getHSLhue() {
        return hslH;
    }

    public float getHSLsat() {
        return hslS;
    }

    public float getHSLlight() {
        return hslL;
    }

    public int getRed() {
        return rgbR;
    }

    public int getGreen() {
        return rgbG;
    }

    public int getBlue() {
        return rgbB;
    }

    public Color getColor() {
        return new Color(clr);
    }

    public String getHexColor() {
        String s = Integer.toHexString(clr & 0xffffff);
        while (s.length() < 6) {
            s = "0" + s;
        }
        return s;
    }

/////////////////////////////////////////////////////////
//  Listeners routunes
//
    public void addListener(ColorListener toAdd) {
        changeListeners.add(toAdd);
    }

    public void removeListener(ColorListener toRemove) {
        changeListeners.remove(toRemove);
    }

    public void removeAllListeners() {
        for (ColorListener cl : changeListeners) {
            changeListeners.remove(cl);
        }
    }

    public void updateColor(int clr) {
        for (ColorListener cl : changeListeners) {
            if (cl != initiator) {
                cl.updateColor(clr);
            }
        }
    }

/////////////////////////////////////////////////////////
//  Calculating routunes
//
    private void calculateHSBfromRGB() {
        int cmax = (rgbR > rgbG) ? rgbR : rgbG;
        if (rgbB > cmax) {
            cmax = rgbB;
        }
        int cmin = (rgbR < rgbG) ? rgbR : rgbG;
        if (rgbB < cmin) {
            cmin = rgbB;
        }

        hsbB = ((float) cmax) / 255.0f;
        hsbS = (cmax != 0) ? ((float) (cmax - cmin) / cmax) : 0.0f;

        if (hsbS == 0) {
            hsbH = 0;
        } else {
            float redc = (float) (cmax - rgbR) / (cmax - cmin);
            float greenc = (float) (cmax - rgbG) / (cmax - cmin);
            float bluec = (float) (cmax - rgbB) / (cmax - cmin);
            if (rgbR == cmax) {
                hsbH = bluec - greenc;
            } else if (rgbG == cmax) {
                hsbH = 2.0f + redc - bluec;
            } else {
                hsbH = 4.0f + greenc - redc;
            }
            hsbH = hsbH / 6.0f;
            if (hsbH < 0) {
                hsbH = hsbH + 1.0f;
            }
        }
    }

    private void calculateRGBfromHSB() {
        if (hsbS == 0) {
            rgbR = rgbG = rgbB = (int) (hsbB * 255.0f + 0.5f);
        } else {
            float h1 = (hsbH - (float) Math.floor(hsbH)) * 6.0f;
            float f = h1 - (float) java.lang.Math.floor(h1);
            float p = hsbB * (1.0f - hsbS);
            float q = hsbB * (1.0f - hsbS * f);
            float t = hsbB * (1.0f - (hsbS * (1.0f - f)));
            switch ((int) h1) {
                case 0:
                    rgbR = (int) (hsbB * 255.0f + 0.5f);
                    rgbG = (int) (t * 255.0f + 0.5f);
                    rgbB = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    rgbR = (int) (q * 255.0f + 0.5f);
                    rgbG = (int) (hsbB * 255.0f + 0.5f);
                    rgbB = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    rgbR = (int) (p * 255.0f + 0.5f);
                    rgbG = (int) (hsbB * 255.0f + 0.5f);
                    rgbB = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    rgbR = (int) (p * 255.0f + 0.5f);
                    rgbG = (int) (q * 255.0f + 0.5f);
                    rgbB = (int) (hsbB * 255.0f + 0.5f);
                    break;
                case 4:
                    rgbR = (int) (t * 255.0f + 0.5f);
                    rgbG = (int) (p * 255.0f + 0.5f);
                    rgbB = (int) (hsbB * 255.0f + 0.5f);
                    break;
                case 5:
                    rgbR = (int) (hsbB * 255.0f + 0.5f);
                    rgbG = (int) (p * 255.0f + 0.5f);
                    rgbB = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
    }

    private void calculateHSLfromRGB() {
        int cmax = (rgbR > rgbG) ? rgbR : rgbG;
        if (rgbB > cmax) {
            cmax = rgbB;
        }
        int cmin = (rgbR < rgbG) ? rgbR : rgbG;
        if (rgbB < cmin) {
            cmin = rgbB;
        }

        hslL = (float) (cmax + cmin) / (255 * 2);
        hslS = (float) (cmax - cmin) / (255 - Math.abs(255 - (cmax + cmin)));

        if (cmax == cmin) {
            hslH = 0.0f;
        } else if (cmax == rgbR) {
            hslH = (float) (rgbG - rgbB) / (6 * (cmax - cmin));
            if (rgbG < rgbB) {
                hslH = hslH + 1.0f;
            }
        } else if (cmax == rgbG) {
            hslH = ((float) (rgbB - rgbR) / (6 * (cmax - cmin))) + 1.0f / 3.0f;
        } else {
            hslH = ((float) (rgbR - rgbG) / (6 * (cmax - cmin))) + 2.0f / 3.0f;
        }
    }

    private void calculateRGBfromHSL() {
        float q, p;
        if (hslL < 0.5f) {
            q = hslL * (1.0f + hslS);
        } else {
            q = hslL + hslS - (hslL * hslS);
        }
        p = 2.0f * hslL - q;

        Function<Float, Float> colorComponent = (Float tc) -> {
            if (tc < 0.0f) {
                tc = tc + 1.0f;
            } else if (tc > 1.0f) {
                tc = tc - 1.0f;
            }
            if (tc < (float) 1 / 6) {
                return p + ((q - p) * 6.0f * tc);
            } else if (tc < 0.5f) {
                return q;
            } else if (tc < (float) 2 / 3) {
                return p + ((q - p) * 6.0f * (2.0f / 3.0f - tc));
            } else {
                return p;
            }
        };

        rgbR = Math.round(colorComponent.apply(hslH + 1.0f / 3.0f) * 255.0f);
        rgbG = Math.round(colorComponent.apply(hslH) * 255.0f);
        rgbB = Math.round(colorComponent.apply(hslH - 1.0f / 3.0f) * 255.0f);
    }

/////////////////////////////////////////////////////////
//  Additional convertion routines
//
    public static int HSBtoColor(float h, float s, float b) {
        int red = 0, green = 0, blue = 0;
        if (s == 0) {
            red = green = blue = (int) (b * 255.0f + 0.5f);
        } else {
            float h1 = (h - (float) Math.floor(h)) * 6.0f;
            float f = h1 - (float) java.lang.Math.floor(h1);
            float p = b * (1.0f - s);
            float q = b * (1.0f - s * f);
            float t = b * (1.0f - (s * (1.0f - f)));
            switch ((int) h1) {
                case 0:
                    red = (int) (b * 255.0f + 0.5f);
                    green = (int) (t * 255.0f + 0.5f);
                    blue = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    red = (int) (q * 255.0f + 0.5f);
                    green = (int) (b * 255.0f + 0.5f);
                    blue = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    red = (int) (p * 255.0f + 0.5f);
                    green = (int) (b * 255.0f + 0.5f);
                    blue = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    red = (int) (p * 255.0f + 0.5f);
                    green = (int) (q * 255.0f + 0.5f);
                    blue = (int) (b * 255.0f + 0.5f);
                    break;
                case 4:
                    red = (int) (t * 255.0f + 0.5f);
                    green = (int) (p * 255.0f + 0.5f);
                    blue = (int) (b * 255.0f + 0.5f);
                    break;
                case 5:
                    red = (int) (b * 255.0f + 0.5f);
                    green = (int) (p * 255.0f + 0.5f);
                    blue = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        return 0xff000000
                | ((red & 0xff) << 16)
                | ((green & 0xff) << 8)
                | (blue & 0xff);
    } // HSBtoColor

    public static int HSLtoColor(float h, float s, float l) {
        float q, p;
        if (l < 0.5f) {
            q = l * (1.0f + s);
        } else {
            q = l + s - (l * s);
        }
        p = 2.0f * l - q;

        Function<Float, Float> colorComponent = (Float tc) -> {
            if (tc < 0.0f) {
                tc = tc + 1.0f;
            } else if (tc > 1.0f) {
                tc = tc - 1.0f;
            }
            if (tc < (float) 1 / 6) {
                return p + ((q - p) * 6.0f * tc);
            } else if (tc < 0.5f) {
                return q;
            } else if (tc < (float) 2 / 3) {
                return p + ((q - p) * 6.0f * (2.0f / 3.0f - tc));
            } else {
                return p;
            }
        };

        int red = Math.round(colorComponent.apply(h + 1.0f / 3.0f) * 255.0f);
        int green = Math.round(colorComponent.apply(h) * 255.0f);
        int blue = Math.round(colorComponent.apply(h - 1.0f / 3.0f) * 255.0f);

        return 0xff000000
                | ((red & 0xff) << 16)
                | ((green & 0xff) << 8)
                | (blue & 0xff);
    } // HSLtoColor

} // class lnColorChanger
