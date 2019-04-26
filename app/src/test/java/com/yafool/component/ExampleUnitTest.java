package com.yafool.component;

import com.yafool.component.signer.SignUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    public static void main(String[] args) {
        ExampleSignBean bean = new ExampleSignBean();
        bean.setExNoSignAAA("aaa");
        bean.setExSignBBB("bbb");
        bean.setExNoSignCCC("ccc");
        bean.setExSignDDD("ddd");
        bean.setExNoSignEEE("eee");
        bean.setExSignFFF("fff");
        bean.setExNoSignGGG("ggg");
        bean.setExNoSignHHH("hhh");
        bean.setExSignLLL("lll");

        String sign = SignUtils.sign(bean, "dsahfhaihwheb==");
    }
}