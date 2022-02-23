package io.jenkins.plugins.tools;

import java.awt.Color;
import java.util.ArrayList;
import lombok.Data;

/**
 * antd 颜色工具
 * <p>
 * https://3x.ant.design/docs/spec/colors-cn
 * <p>
 * https://github.com/ant-design/ant-design-colors
 *
 * @author liuwei
 */
@SuppressWarnings("unused")
public class AntdColor {

  private final ArrayList<String> pattens;

  public AntdColor(String color) {
    this.pattens = generate(color);
  }

  public String[] values() {
    return pattens.toArray(
        new String[LIGHT_COLOR_COUNT + DARK_COLOR_COUNT + 1]
    );
  }

  public String get(Level level) {
    return pattens.get(level.value - 1);
  }

  @Override
  public String toString() {
    return get(Level.PRIMARY);
  }

  public enum Level {
    /**
     * 颜色等级
     */
    ONE(1),
    TOW(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    PRIMARY(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10);

    private final int value;

    Level(int value) {
      this.value = value;
    }
  }

  /**
   * Dust Red / 薄暮
   * <p>
   * 斗志、奔放
   */
  public static final AntdColor RED = new AntdColor("#F5222D");

  /**
   * Volcano / 火山
   * <p>
   * 醒目、澎湃
   */
  public static final AntdColor VOLCANO = new AntdColor("#FA541C");

  /**
   * Sunset Orange / 日暮
   * <p>
   * 温暖、欢快
   */
  public static final AntdColor ORANGE = new AntdColor("#FA8C16");

  /**
   * Calendula Gold / 金盏花
   * <p>
   * 活力、积极
   */
  public static final AntdColor GOLD = new AntdColor("#FAAD14");

  /**
   * Sunrise Yellow / 日出
   * <p>
   * 出生、阳光
   */
  public static final AntdColor YELLOW = new AntdColor("#FADB14");

  /**
   * Lime / 青柠
   * <p>
   * 自然、生机
   */
  public static final AntdColor LIME = new AntdColor("#A0D911");

  /**
   * Polar Green / 极光绿
   * <p>
   * 健康、创新
   */
  public static final AntdColor GREEN = new AntdColor("#52C41A");

  /**
   * Cyan / 明青
   * <p>
   * 希望、坚强
   */
  public static final AntdColor CYAN = new AntdColor("#13C2C2");

  /**
   * Daybreak Blue / 拂晓蓝
   * <p>
   * 包容、科技、普惠
   */
  public static final AntdColor BLUE = new AntdColor("#1890FF");

  /**
   * Geek Blue / 极客蓝
   * <p>
   * 探索、钻研
   */
  public static final AntdColor GEEK_BLUE = new AntdColor("#2F54EB");

  /**
   * Golden Purple / 酱紫
   * <p>
   * 优雅、浪漫
   */
  public static final AntdColor PURPLE = new AntdColor("#722ED1");

  /**
   * Magenta / 法式洋红
   * <p>
   * 明快、感性
   */
  public static final AntdColor MAGENTA = new AntdColor("#EB2F96");

  /**
   * 黑色
   */
  public static final AntdColor BLACK = new AntdColor("#333");

  /**
   * 灰色
   */
  public static final AntdColor GREY = new AntdColor("#666666");

  /**
   * 色相阶梯
   */
  private static final int HUE_STEP = 2;

  /**
   * 饱和度阶梯，浅色部分
   */
  private static final int LIGHT_SATURATION_STEP = 16;

  /**
   * 饱和度阶梯，深色部分
   */
  private static final int DARK_SATURATION_STEP = 5;

  /**
   * 亮度阶梯，浅色部分
   */
  private static final int LIGHT_BRIGHTNESS_STEP = 5;

  /**
   * 亮度阶梯，深色部分
   */
  private static final int DARK_BRIGHTNESS_STEP = 15;

  /**
   * 浅色数量，主色上
   */
  private static final int LIGHT_COLOR_COUNT = 5;

  /**
   * 深色数量，主色下
   */
  private static final int DARK_COLOR_COUNT = 4;

  @Data
  private static final class Hsv {

    private float h;
    private float s;
    private float v;

    public Hsv(Color color) {
      //  hsb 跟 hsv 是同一个概念
      float[] hsb = JavaFxColor.getHsb(color);
      this.h = hsb[0];
      this.s = hsb[1];
      this.v = hsb[2];
    }
  }

  private static float getHue(Hsv hsv, int i, boolean light) {
    float hue;
    // 根据色相不同，色相转向不同
    if (Math.round(hsv.h) >= 60 && Math.round(hsv.h) <= 240) {
      hue = light ? Math.round(hsv.h) - HUE_STEP * i : Math.round(hsv.h) + HUE_STEP * i;
    } else {
      hue = light ? Math.round(hsv.h) + HUE_STEP * i : Math.round(hsv.h) - HUE_STEP * i;
    }
    if (hue < 0) {
      hue += 360;
    } else if (hue >= 360) {
      hue -= 360;
    }
    return hue;
  }

  private static float getSaturation(Hsv hsv, int i, boolean light) {
    // grey color don't change saturation
    if (hsv.h == 0 && hsv.s == 0) {
      return hsv.s;
    }
    float saturation;
    if (light) {
      saturation = Math.round(hsv.s * 100) - LIGHT_SATURATION_STEP * i;
    } else if (i == DARK_COLOR_COUNT) {
      saturation = Math.round(hsv.s * 100) + LIGHT_SATURATION_STEP;
    } else {
      saturation = Math.round(hsv.s * 100) + DARK_SATURATION_STEP * i;
    }
    // 边界值修正
    if (saturation > 100) {
      saturation = 100;
    }
    // 第一格的 s 限制在 6-10 之间
    if (light && i == LIGHT_COLOR_COUNT && saturation > 10) {
      saturation = 10;
    }
    if (saturation < 6) {
      saturation = 6;
    }
    return Math.max(0, Math.min(saturation / 100, 1));
  }

  private static float getValue(Hsv hsv, int i, boolean light) {
    float value;
    if (light) {
      value = Math.round(hsv.v * 100) + LIGHT_BRIGHTNESS_STEP * i;
    } else {
      value = Math.round(hsv.v * 100) - DARK_BRIGHTNESS_STEP * i;
    }
    return Math.max(0, Math.min(value / 100, 1));
  }

  public static ArrayList<String> generate(String colorString) {
    ArrayList<String> patterns = new ArrayList<>();
    Color color = JavaFxColor.web(colorString);
    Hsv hsv = new Hsv(color);
    for (int i = LIGHT_COLOR_COUNT; i > 0; i -= 1) {
      patterns.add(
          JavaFxColor.getHex(
              JavaFxColor.hsb(
                  getHue(hsv, i, true),
                  getSaturation(hsv, i, true),
                  getValue(hsv, i, true)
              )
          )
      );
    }

    patterns.add(
        JavaFxColor.getHex(color)
    );

    for (int i = 1; i <= DARK_COLOR_COUNT; i += 1) {
      patterns.add(
          JavaFxColor.getHex(
              JavaFxColor.hsb(
                  getHue(hsv, i, false),
                  getSaturation(hsv, i, false),
                  getValue(hsv, i, false)
              )
          )
      );
    }

    return patterns;
  }

}
