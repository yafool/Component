/*
 *   Copyright (C) 2019 yafool Individual developer
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.yafool.component;

import com.yafool.component.signer.MustSigned;
import com.yafool.component.signer.SignFormat;

/**
 * @Package: com.yafool.component
 * @ClassName: com.yafool.component.ExampleSignBean.java
 * @Description: TODO
 * @CreateDate: 2019/4/26 3:05 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/26 3:05 PM
 */
@SignFormat(maxSize = 4)
public class ExampleSignBean {
    public String exNoSignAAA;

    @MustSigned(index = 2)
    public String exSignBBB;

    public String exNoSignCCC;

    @MustSigned(index = 3)
    public String exSignDDD;

    public String exNoSignEEE;

    @MustSigned(index = 1)
    public String exSignFFF;

    public String exNoSignGGG;
    public String exNoSignHHH;

    @MustSigned(index = 0)
    public String exSignLLL;

    public String getExNoSignAAA() {
        return exNoSignAAA;
    }

    public void setExNoSignAAA(String exNoSignAAA) {
        this.exNoSignAAA = exNoSignAAA;
    }

    public String getExSignBBB() {
        return exSignBBB;
    }

    public void setExSignBBB(String exSignBBB) {
        this.exSignBBB = exSignBBB;
    }

    public String getExNoSignCCC() {
        return exNoSignCCC;
    }

    public void setExNoSignCCC(String exNoSignCCC) {
        this.exNoSignCCC = exNoSignCCC;
    }

    public String getExSignDDD() {
        return exSignDDD;
    }

    public void setExSignDDD(String exSignDDD) {
        this.exSignDDD = exSignDDD;
    }

    public String getExNoSignEEE() {
        return exNoSignEEE;
    }

    public void setExNoSignEEE(String exNoSignEEE) {
        this.exNoSignEEE = exNoSignEEE;
    }

    public String getExSignFFF() {
        return exSignFFF;
    }

    public void setExSignFFF(String exSignFFF) {
        this.exSignFFF = exSignFFF;
    }

    public String getExNoSignGGG() {
        return exNoSignGGG;
    }

    public void setExNoSignGGG(String exNoSignGGG) {
        this.exNoSignGGG = exNoSignGGG;
    }

    public String getExNoSignHHH() {
        return exNoSignHHH;
    }

    public void setExNoSignHHH(String exNoSignHHH) {
        this.exNoSignHHH = exNoSignHHH;
    }

    public String getExSignLLL() {
        return exSignLLL;
    }

    public void setExSignLLL(String exSignLLL) {
        this.exSignLLL = exSignLLL;
    }
}
