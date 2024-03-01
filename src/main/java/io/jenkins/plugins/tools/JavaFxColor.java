package io.jenkins.plugins.tools;

import java.awt.Color;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

class JavaFxColor {

  private static final int PARSE_COMPONENT = 0; // percent, or clamped to [0,255] => [0,1]
  private static final int PARSE_PERCENT = 1; // clamped to [0,100]% => [0,1]
  private static final int PARSE_ANGLE = 2; // clamped to [0,360]
  private static final int PARSE_ALPHA = 3; // clamped to [0.0,1.0]

  public static Color web(String colorString) {
    return web(colorString, 1.0f);
  }

  public static Color web(String colorString, float opacity) {
    if (colorString == null) {
      throw new NullPointerException(
          "The color components or name must be specified");
    }
    if (colorString.isEmpty()) {
      throw new IllegalArgumentException("Invalid color specification");
    }

    String color = colorString.toLowerCase(Locale.ROOT);

    if (color.startsWith("#")) {
      color = color.substring(1);
    } else if (color.startsWith("0x")) {
      color = color.substring(2);
    } else if (color.startsWith("rgb")) {
      if (color.startsWith("(", 3)) {
        return parseRGBColor(color, 4, false, opacity);
      } else if (color.startsWith("a(", 3)) {
        return parseRGBColor(color, 5, true, opacity);
      }
    } else if (color.startsWith("hsl")) {
      if (color.startsWith("(", 3)) {
        return parseHSLColor(color, 4, false, opacity);
      } else if (color.startsWith("a(", 3)) {
        return parseHSLColor(color, 5, true, opacity);
      }
    } else {
      Color namedColor = Color.getColor(colorString);
      if (namedColor != null) {
        if (opacity == 1.0) {
          return namedColor;
        } else {
          return color(
              namedColor.getRed(),
              namedColor.getGreen(),
              namedColor.getBlue(),
              opacity);
        }
      }
    }

    int len = color.length();

    try {
      int r;
      int g;
      int b;
      int a;

      if (len == 3) {
        r = Integer.parseInt(color.substring(0, 1), 16);
        g = Integer.parseInt(color.substring(1, 2), 16);
        b = Integer.parseInt(color.substring(2, 3), 16);
        return color(r / 15.0f, g / 15.0f, b / 15.0f, opacity);
      } else if (len == 4) {
        r = Integer.parseInt(color.substring(0, 1), 16);
        g = Integer.parseInt(color.substring(1, 2), 16);
        b = Integer.parseInt(color.substring(2, 3), 16);
        a = Integer.parseInt(color.substring(3, 4), 16);
        return color(r / 15.0f, g / 15.0f, b / 15.0f,
            opacity * a / 15.0f);
      } else if (len == 6) {
        r = Integer.parseInt(color.substring(0, 2), 16);
        g = Integer.parseInt(color.substring(2, 4), 16);
        b = Integer.parseInt(color.substring(4, 6), 16);
        return rgb(r, g, b, opacity);
      } else if (len == 8) {
        r = Integer.parseInt(color.substring(0, 2), 16);
        g = Integer.parseInt(color.substring(2, 4), 16);
        b = Integer.parseInt(color.substring(4, 6), 16);
        a = Integer.parseInt(color.substring(6, 8), 16);
        return rgb(r, g, b, opacity * a / 255.0f);
      }
    } catch (NumberFormatException ignored) {
    }

    throw new IllegalArgumentException("Invalid color specification");
  }

  public static Color color(float red, float green, float blue,
      float opacity) {
    return new Color(red, green, blue, opacity);
  }

  public static Color rgb(int red, int green, int blue, float opacity) {
    checkRGB(red, green, blue);
    return color(
        red / 255.0f,
        green / 255.0f,
        blue / 255.0f,
        opacity);
  }

  private static Color parseRGBColor(String color, int roff,
      boolean hasAlpha, float a) {
    try {
      int rend = color.indexOf(',', roff);
      int gend = rend < 0 ? -1 : color.indexOf(',', rend + 1);
      int bend = gend < 0 ? -1 : color.indexOf(hasAlpha ? ',' : ')', gend + 1);
      int aend = hasAlpha ? (bend < 0 ? -1 : color.indexOf(')', bend + 1)) : bend;
      if (aend >= 0) {
        float r = parseComponent(color, roff, rend, PARSE_COMPONENT);
        float g = parseComponent(color, rend + 1, gend, PARSE_COMPONENT);
        float b = parseComponent(color, gend + 1, bend, PARSE_COMPONENT);
        if (hasAlpha) {
          a *= parseComponent(color, bend + 1, aend, PARSE_ALPHA);
        }
        return color(r, g, b, a);
      }
    } catch (NumberFormatException ignored) {
    }

    throw new IllegalArgumentException("Invalid color specification");
  }

  private static float parseComponent(String color, int off, int end, int type) {
    color = color.substring(off, end).trim();
    if (color.endsWith("%")) {
      if (type > PARSE_PERCENT) {
        throw new IllegalArgumentException("Invalid color specification");
      }
      type = PARSE_PERCENT;
      color = color.substring(0, color.length() - 1).trim();
    } else if (type == PARSE_PERCENT) {
      throw new IllegalArgumentException("Invalid color specification");
    }
    float c = ((type == PARSE_COMPONENT)
        ? Integer.parseInt(color)
        : Float.parseFloat(color));
    switch (type) {
      case PARSE_ALPHA:
        return (c < 0.0f) ? 0.0f : (Math.min(c, 1.0f));
      case PARSE_PERCENT:
        return (c <= 0.0f) ? 0.0f : ((c >= 100.0f) ? 1.0f : (c / 100.0f));
      case PARSE_COMPONENT:
        return (c <= 0.0f) ? 0.0f : ((c >= 255.0f) ? 1.0f : (c / 255.0f));
      case PARSE_ANGLE:
        return ((c < 0.0f)
            ? ((c % 360.0f) + 360.0f)
            : ((c > 360.0f)
                ? (c % 360.0f)
                : c));
    }

    throw new IllegalArgumentException("Invalid color specification");
  }

  private static Color parseHSLColor(String color, int hoff,
      boolean hasAlpha, float a) {
    try {
      int hend = color.indexOf(',', hoff);
      int send = hend < 0 ? -1 : color.indexOf(',', hend + 1);
      int lend = send < 0 ? -1 : color.indexOf(hasAlpha ? ',' : ')', send + 1);
      int aend = hasAlpha ? (lend < 0 ? -1 : color.indexOf(')', lend + 1)) : lend;
      if (aend >= 0) {
        float h = parseComponent(color, hoff, hend, PARSE_ANGLE);
        float s = parseComponent(color, hend + 1, send, PARSE_PERCENT);
        float l = parseComponent(color, send + 1, lend, PARSE_PERCENT);
        if (hasAlpha) {
          a *= parseComponent(color, lend + 1, aend, PARSE_ALPHA);
        }
        return hsb(h, s, l, a);
      }
    } catch (NumberFormatException ignored) {
    }

    throw new IllegalArgumentException("Invalid color specification");
  }

  public static Color hsb(float hue, float saturation, float brightness) {
    return hsb(hue, saturation, brightness, 1.0f);
  }

  public static Color hsb(float hue, float saturation, float brightness, float opacity) {
    checkSB(saturation, brightness);
    float[] rgb = hsb2Rgb(hue, saturation, brightness);
    return color(rgb[0], rgb[1], rgb[2], opacity);
  }

  public static float[] getHsb(Color color) {
    return rgb2Hsb(
        color.getRed() / 255.0f,
        color.getGreen() / 255.0f,
        color.getBlue() / 255.0f
    );
  }

  public static float[] getRgb(Color color) {
    return color.getColorComponents(null);
  }

  public static String getHex(Color color) {
    return "#" +
        decimal2Hex(
            color.getRed()
        ) +
        decimal2Hex(
            color.getGreen()
        )
        +
        decimal2Hex(
            color.getBlue()
        );
  }

  private static String decimal2Hex(int value) {
    return StringUtils.leftPad(
        Integer.toHexString(value),
        2,
        "0"
    );
  }

  private static float[] hsb2Rgb(float hue, float saturation, float brightness) {
    // normalize the hue
    float normalizedHue = ((hue % 360) + 360) % 360;
    hue = normalizedHue / 360f;

    float r = 0, g = 0, b = 0;
    if (saturation == 0) {
      r = g = b = brightness;
    } else {
      float h = (float) ((hue - Math.floor(hue)) * 6.0);
      float f = (float) (h - Math.floor(h));
      float p = brightness * (1.0f - saturation);
      float q = brightness * (1.0f - saturation * f);
      float t = brightness * (1.0f - (saturation * (1.0f - f)));
      switch ((int) h) {
        case 0:
          r = brightness;
          g = t;
          b = p;
          break;
        case 1:
          r = q;
          g = brightness;
          b = p;
          break;
        case 2:
          r = p;
          g = brightness;
          b = t;
          break;
        case 3:
          r = p;
          g = q;
          b = brightness;
          break;
        case 4:
          r = t;
          g = p;
          b = brightness;
          break;
        case 5:
          r = brightness;
          g = p;
          b = q;
          break;
        default:
      }
    }
    float[] f = new float[3];
    f[0] = r;
    f[1] = g;
    f[2] = b;
    return f;
  }

  private static float[] rgb2Hsb(float r, float g, float b) {
    float hue, saturation, brightness;
    float[] hsbvals = new float[3];
    float cmax = (r > g) ? r : g;
    if (b > cmax) {
      cmax = b;
    }
    float cmin = (r < g) ? r : g;
    if (b < cmin) {
      cmin = b;
    }

    brightness = cmax;
    if (cmax != 0) {
      saturation = (cmax - cmin) / cmax;
    } else {
      saturation = 0;
    }

    if (saturation == 0) {
      hue = 0;
    } else {
      float redc = (cmax - r) / (cmax - cmin);
      float greenc = (cmax - g) / (cmax - cmin);
      float bluec = (cmax - b) / (cmax - cmin);
      if (r == cmax) {
        hue = bluec - greenc;
      } else if (g == cmax) {
        hue = 2.0f + redc - bluec;
      } else {
        hue = 4.0f + greenc - redc;
      }
      hue = hue / 6.0f;
      if (hue < 0) {
        hue = hue + 1.0f;
      }
    }
    hsbvals[0] = hue * 360;
    hsbvals[1] = saturation;
    hsbvals[2] = brightness;
    return hsbvals;
  }

  private static void checkSB(float saturation, float brightness) {
    if (saturation < 0.0 || saturation > 1.0) {
      throw new IllegalArgumentException(
          "Color.hsb's saturation parameter (" + saturation + ") expects values 0.0-1.0");
    }
    if (brightness < 0.0 || brightness > 1.0) {
      throw new IllegalArgumentException(
          "Color.hsb's brightness parameter (" + brightness + ") expects values 0.0-1.0");
    }
  }

  private static void checkRGB(int red, int green, int blue) {
    if (red < 0 || red > 255) {
      throw new IllegalArgumentException(
          "Color.rgb's red parameter (" + red + ") expects color values 0-255");
    }
    if (green < 0 || green > 255) {
      throw new IllegalArgumentException(
          "Color.rgb's green parameter (" + green + ") expects color values 0-255");
    }
    if (blue < 0 || blue > 255) {
      throw new IllegalArgumentException(
          "Color.rgb's blue parameter (" + blue + ") expects color values 0-255");
    }
  }
}