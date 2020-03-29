package io.jenkins.plugins.tools;

import java.util.ArrayList;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * antd 颜色工具
 * <p>
 * {@link <a href="https://3x.ant.design/docs/spec/colors-cn">色彩</a>}
 * <p>
 * {@see <a href="https://github.com/ant-design/ant-design-colors">ant-design-colors</a>}
 *
 * @author liuwei
 * @date 2020/3/29 15:01
 */
@SuppressWarnings("unused")
public class AntdColor {

  private ArrayList<String> pattens;

  public AntdColor(String color) {
    this.pattens = generate(color);
  }

  public String get(Level level) {
    return pattens.get(level.value);
  }

  @Override
  public String toString() {
    return get(Level.PRIMARY);
  }

  public void show() {
    System.out.println(pattens.toString());
  }

  public static enum Level {
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

    private int value;

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
   * 灰色
   */
  public static final AntdColor GREY = new AntdColor("#666666");

  /**
   * 色相阶梯
   */
  private static final int hueStep = 2;

  /**
   * 饱和度阶梯，浅色部分
   */
  private static final int saturationStep = 16;

  /**
   * 饱和度阶梯，深色部分
   */
  private static final int saturationStep2 = 5;

  /**
   * 亮度阶梯，浅色部分
   */
  private static final int brightnessStep1 = 5;

  /**
   * 亮度阶梯，深色部分
   */
  private static final int brightnessStep2 = 15;

  /**
   * 浅色数量，主色上
   */
  private static final int lightColorCount = 5;

  /**
   * 深色数量，主色下
   */
  private static final int darkColorCount = 4;

  @Data
  @AllArgsConstructor
  private static final class Hsv {

    private double h;
    private double s;
    private double v;
  }

  private static double getHue(Hsv hsv, int i, boolean light) {
    double hue;
    // 根据色相不同，色相转向不同
    if (Math.round(hsv.h) >= 60 && Math.round(hsv.h) <= 240) {
      hue = light ? Math.round(hsv.h) - hueStep * i : Math.round(hsv.h) + hueStep * i;
    } else {
      hue = light ? Math.round(hsv.h) + hueStep * i : Math.round(hsv.h) - hueStep * i;
    }
    if (hue < 0) {
      hue += 360;
    } else if (hue >= 360) {
      hue -= 360;
    }
    return hue;
  }

  private static double getSaturation(Hsv hsv, int i, boolean light) {
    // grey color don't change saturation
    if (hsv.h == 0 && hsv.s == 0) {
      return hsv.s;
    }
    double saturation;
    if (light) {
      saturation = Math.round(hsv.s * 100) - saturationStep * i;
    } else if (i == darkColorCount) {
      saturation = Math.round(hsv.s * 100) + saturationStep;
    } else {
      saturation = Math.round(hsv.s * 100) + saturationStep2 * i;
    }
    // 边界值修正
    if (saturation > 100) {
      saturation = 100;
    }
    // 第一格的 s 限制在 6-10 之间
    if (light && i == lightColorCount && saturation > 10) {
      saturation = 10;
    }
    if (saturation < 6) {
      saturation = 6;
    }
    return Math.max(0, Math.min(saturation / 100, 1));
  }

  private static double getValue(Hsv hsv, int i, boolean light) {
    double value;
    if (light) {
      value = Math.round(hsv.v * 100) + brightnessStep1 * i;
    } else {
      value = Math.round(hsv.v * 100) - brightnessStep2 * i;
    }
    return Math.max(0, Math.min(value / 100, 1));
  }

  private static String getHexColor(Color color) {
    return "#" + color.toString().substring(2, 8);
  }

  public static ArrayList<String> generate(String colorString) {
    ArrayList<String> patterns = new ArrayList<>();
    Color color = Color.web(colorString);
    Hsv hsv = new Hsv(color.getHue(), color.getSaturation(), color.getBrightness());
    for (int i = lightColorCount; i > 0; i -= 1) {
      patterns.add(
          getHexColor(
              Color.hsb(
                  getHue(hsv, i, true),
                  getSaturation(hsv, i, true),
                  getValue(hsv, i, true)
              )
          )
      );
    }

    patterns.add(
        getHexColor(color)
    );

    for (int i = 1; i <= darkColorCount; i += 1) {
      patterns.add(
          getHexColor(
              Color.hsb(
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
