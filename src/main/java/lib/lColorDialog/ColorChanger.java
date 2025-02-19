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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/////////////////////////////////////////////////////////
//         
//         lnColorChanger
//         
/////////////////////////////////////////////////////////

/**
 * The color changer. It changes the current color, calculates new
 * values for all color components and then broadcast new values to all other
 * controls.
 */
@SuppressWarnings("SpellCheckingInspection")
public class ColorChanger {

    /**
     * The List of color listeners.
     */
    private final List<ColorListener> colorListeners = new ArrayList<>();

    /**
     * A sender of the color changing. Used to not update a color in this control.
     */
    private Object invoker = null;

    /**
     * The current color as an integer value.
     */
    private int clr = -1;

    /**
     * The RGB color model. A RED component.
     */
    private int rgbR = 0;

    /**
     * The RGB color model. A GREEN component.
     */
    private int rgbG = 0;

    /**
     * The RGB color model. A BLUE component.
     */
    private int rgbB = 0;

    /**
     * The HSB/HSV color model. A HUE component.
     */
    private float hsbH = 0.0f;

    /**
     * The HSB/HSV color model. A SATURATION component.
     */
    private float hsbS = 0.0f;

    /**
     * The HSB/HSV color model. A BRIGHTNESS component.
     */
    private float hsbB = 0.0f;

    /**
     * The HSL color model. A HUE component.
     */
    private float hslH = 0.0f;

    /**
     * The HSL color model. A SATURATION component.
     */
    private float hslS = 0.0f;

    /**
     * The HSL color model. A LIGHTNESS component.
     */
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

    /**
     * Sets a new HUE value to HSB/HSV color model.
     *
     * @param obj color change sender. The component that changes color.
     * @param h   new Hue value, must be from 0 to 1.
     */
    public void setHSBhue(Object obj, float h) {
        setHSB(obj, h, hsbS, hsbB);
    }

    /**
     * Sets a new SATURATION value to HSB/HSV color model.
     *
     * @param obj color change sender. The component that changes color.
     * @param s   new saturation value, must be from 0 to 1.
     */
    public void setHSBsat(Object obj, float s) {
        setHSB(obj, hsbH, s, hsbB);
    }

    /**
     * Sets a new BRIGHTNESS value to HSB/HSV color model.
     *
     * @param obj color change sender. The component that changes color.
     * @param b   new brightness value, must be from 0 to 1.
     */
    public void setHSBbri(Object obj, float b) {
        setHSB(obj, hsbH, hsbS, b);
    }

    /**
     * Sets a new HUE value to HSL color model.
     *
     * @param obj color change sender. The component that changes color.
     * @param h   new Hue value, must be from 0 to 1.
     */
    public void setHSLhue(Object obj, float h) {
        setHSL(obj, h, hslS, hslL);
    }

    /**
     * Sets a new SATURATION value to HSL color model.
     *
     * @param obj color change sender. The component that changes color.
     * @param s   new saturation value, must be from 0 to 1.
     */
    public void setHSLsat(Object obj, float s) {
        setHSL(obj, hslH, s, hslL);
    }

    /**
     * Sets a new LIGHTNESS value to HSL color model.
     *
     * @param obj color change sender. The component that changes color.
     * @param l   new lightness value, must be from 0 to 1.
     */
    public void setHSLlight(Object obj, float l) {
        setHSL(obj, hslH, hslS, l);
    }

    /**
     * Sets a new RED value to RGB color model.
     *
     * @param obj color change sender. The component that changes color.
     * @param r   new red value, must be from 0 to 255.
     */
    public void setRed(Object obj, int r) {
        setRGB(obj, r, rgbG, rgbB);
    }

    /**
     * Sets a new GREEN value to RGB color model.
     *
     * @param obj color change sender. The component that changes color.
     * @param g   new green value, must be from 0 to 255.
     */
    public void setGreen(Object obj, int g) {
        setRGB(obj, rgbR, g, rgbB);
    }

    /**
     * Sets a new BLUE value to RGB color model.
     *
     * @param obj color change sender. The component that changes color.
     * @param b   new blue value, must be from 0 to 255.
     */
    public void setBlue(Object obj, int b) {
        setRGB(obj, rgbR, rgbG, b);
    }

    /**
     * Sets a color by its components in the HSB/HSV color model.
     *
     * @param obj color change sender. The component that changes color.
     * @param h   new hue value, must be from 0 to 1.
     * @param s   new saturation value, must be from 0 to 1.
     * @param b   new brightness value, must be from 0 to 1.
     */
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
            invoker = obj;
            updateColor();
            invoker = null;
        }
    }

    /**
     * Sets a color by its components in the HSL color model.
     *
     * @param obj color change sender. The component that changes color.
     * @param h   new hue value, must be from 0 to 1.
     * @param s   new saturation value, must be from 0 to 1.
     * @param l   new lightness value, must be from 0 to 1.
     */
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
            invoker = obj;
            updateColor();
            invoker = null;
        }
    }

    /**
     * Sets a color by its components in the RGB color model.
     *
     * @param obj color change sender. The component that changes color.
     * @param r   new red value, must be from 0 to 255.
     * @param g   new green value, must be from 0 to 255.
     * @param b   new green value, must be from 0 to 255.
     */
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
            invoker = obj;
            updateColor();
            invoker = null;
        }
    }

    /**
     * Sets a color by integer RGB value.
     *
     * @param obj color change sender. The component that changes color.
     * @param rgb color's value 0xRRGGBB.
     */
    public void setRGB(Object obj, int rgb) {
        setRGB(obj,
                (rgb >> 16) & 0xff,
                (rgb >> 8) & 0xff,
                (rgb) & 0xff);
    }

/////////////////////////////////////////////////////////
//  Components getters
//

    /**
     * Gets the HUE component of the HSB/HSV color cmodel.
     *
     * @return Hue of the HSB/HSV.
     */
    public float getHSBhue() {
        return hsbH;
    }

    /**
     * Gets the SATURATION component of the HSB/HSV color cmodel.
     *
     * @return Saturation of the HSB/HSV.
     */
    public float getHSBsat() {
        return hsbS;
    }

    /**
     * Gets the BRIGHTNESS component of the HSB/HSV color cmodel.
     *
     * @return brightness of the HSB/HSV.
     */
    public float getHSBbri() {
        return hsbB;
    }

    /**
     * Gets the HUE component of the HSL color cmodel.
     *
     * @return Hue of the HSL.
     */
    public float getHSLhue() {
        return hslH;
    }

    /**
     * Gets the SATURATION component of the HSL color cmodel.
     *
     * @return Saturation of the HSL.
     */
    public float getHSLsat() {
        return hslS;
    }

    /**
     * Gets the LIGHTNESS component of the HSL color cmodel.
     *
     * @return Lightness of the HSL.
     */
    public float getHSLlight() {
        return hslL;
    }

    /**
     * Gets the RED component of the RGB color cmodel.
     *
     * @return Red of the RGB.
     */
    public int getRed() {
        return rgbR;
    }

    /**
     * Gets the GREEN component of the RGB color cmodel.
     *
     * @return Green of the RGB.
     */
    public int getGreen() {
        return rgbG;
    }

    /**
     * Gets the BLUE component of the RGB color cmodel.
     *
     * @return blue of the RGB.
     */
    public int getBlue() {
        return rgbB;
    }

    /**
     * Gets the color in the java.awt.Color type.
     *
     * @return current color.
     */
    public Color getColor() {
        return new Color(clr);
    }

    /**
     * Gets the color in the integer format.
     *
     * @return current color.
     */
    public int getColorInt() {
        return clr;
    }

/////////////////////////////////////////////////////////
//  Listeners routines
//

    /**
     * Adds a new listener of the color change.
     *
     * @param toAdd an object that implements ColorListener interface.
     */
    public void addListener(ColorListener toAdd) {
        colorListeners.add(toAdd);
    }

    /**
     * Removes the color listener from the list.
     *
     * @param toRemove an object that implements ColorListener interface.
     */
    public void removeListener(ColorListener toRemove) {
        colorListeners.remove(toRemove);
    }

    /**
     * Removes the all the color listeners.
     */
    @SuppressWarnings("unused")
    public void removeAllListeners() {
        for (ColorListener cl : colorListeners) {
            colorListeners.remove(cl);
        }
    }

    /**
     * Notifies all color listeners that the current color has changed.
     */
    public void updateColor() {
        for (ColorListener cl : colorListeners) {
            if (cl != invoker) {
                cl.updateColor();
            }
        }
    }

/////////////////////////////////////////////////////////
//  Calculating routines
//

    /**
     * Calculates HSB/HSV color components when RGB values are known.
     */
    private void calculateHSBfromRGB() {
        int cMax = Math.max(Math.max(rgbR, rgbG), rgbB);
        int cMin = Math.min(Math.min(rgbR, rgbG), rgbB);

        hsbB = ((float) cMax) / 255.0f;
        hsbS = (cMax != 0) ? ((float) (cMax - cMin) / cMax) : 0.0f;

        if (hsbS == 0) {
            hsbH = 0;
        } else {
            float redc = (float) (cMax - rgbR) / (cMax - cMin);
            float greenc = (float) (cMax - rgbG) / (cMax - cMin);
            float bluec = (float) (cMax - rgbB) / (cMax - cMin);
            if (rgbR == cMax) {
                hsbH = bluec - greenc;
            } else if (rgbG == cMax) {
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

    /**
     * Calculates RGB color components when HSB/HSV values are known.
     */
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

    /**
     * Calculates HSL color components when RGB values are known.
     */
    private void calculateHSLfromRGB() {
        int cMax = Math.max(Math.max(rgbR, rgbG), rgbB);
        int cMin = Math.min(Math.min(rgbR, rgbG), rgbB);

        hslL = (float) (cMax + cMin) / (255 * 2);
        hslS = (float) (cMax - cMin) / (255 - Math.abs(255 - (cMax + cMin)));

        if (cMax == cMin) {
            hslH = 0.0f;
        } else if (cMax == rgbR) {
            hslH = (float) (rgbG - rgbB) / (6 * (cMax - cMin));
            if (rgbG < rgbB) {
                hslH = hslH + 1.0f;
            }
        } else if (cMax == rgbG) {
            hslH = ((float) (rgbB - rgbR) / (6 * (cMax - cMin))) + 1.0f / 3.0f;
        } else {
            hslH = ((float) (rgbR - rgbG) / (6 * (cMax - cMin))) + 2.0f / 3.0f;
        }
    }

    /**
     * Calculates RGB color components when HSL values are known.
     */
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
//  Additional conversion routines
//

    /**
     * Calculates RGB color value from HSB/HSV color model components.
     *
     * @param h Hue component from HSB/HSV. Must be from 0 to 1.
     * @param s Saturation component from HSB/HSV. Must be from 0 to 1.
     * @param b Brightness component from HSB/HSV. Must be from 0 to 1.
     * @return an integer color value 0xRRGGBB type.
     */
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

    /**
     * Calculates RGB color value from HSL color model components.
     *
     * @param h Hue component from HSL. Must be from 0 to 1.
     * @param s Saturation component from HSL. Must be from 0 to 1.
     * @param l Lightness component from HSL. Must be from 0 to 1.
     * @return an integer color value 0xRRGGBB type.
     */
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
