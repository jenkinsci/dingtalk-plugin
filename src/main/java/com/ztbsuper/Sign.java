package com.ztbsuper;

import java.io.Serializable;
import java.util.Objects;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.export.Exported;

/**
 * @Author: margo
 * @Date: 2019/11/7 15:06
 * @Description:
 */
public class Sign implements Serializable {

    private static final long serialVersionUID = 4049360052113386765L;

    private String sign;

    @DataBoundConstructor
    public Sign(String sign) {
        this.sign = sign;
    }

    @Exported
    public String getSign() {
        return sign;
    }

    @DataBoundSetter
    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sign)) {
            return false;
        }
        Sign sign1 = (Sign) o;
        return getSign().equals(sign1.getSign());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSign());
    }
}
