/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package network.nerve.quotation.rpc.querier.impl;

import io.nuls.core.core.annotation.Component;
import network.nerve.quotation.model.bo.Chain;
import network.nerve.quotation.rpc.querier.Querier;
import network.nerve.quotation.util.HttpRequestUtil;

import java.math.BigDecimal;
import java.util.Map;

;

/**
 * @author: Loki
 * @date: 2020/7/31
 */
@Component
public class BitzQuerier implements Querier {

    private final String CMD = "/Market/ticker?symbol=";

    @Override
    public BigDecimal tickerPrice(Chain chain, String baseurl, String anchorToken) {
        String symbol = (anchorToken.replace("-", "_")).toLowerCase();
        String url = baseurl + CMD + symbol;
        try {
            Map<String, Object> res = HttpRequestUtil.httpRequest(chain, url);
            if(null == res){
                return null;
            }
            Long code = Long.parseLong(res.get("status").toString());
            if (code != 200L) {
                chain.getLogger().error("Bitz 调用{}接口, 获取价格失败, anchorToken:{}, status:{}, msg:{}",
                        url,
                        anchorToken,
                        code,
                        res.get("msg").toString());
                return null;
            }
            Map<String, Object> data = (Map<String, Object>) res.get("data");
            chain.getLogger().info("BitZ 获取到交易对[{}]价格:{}", symbol.toUpperCase(), data.get("now"));
            return new BigDecimal(data.get("now").toString());
        } catch (Throwable e) {
            chain.getLogger().error("BitZ, 调用接口 {}, anchorToken:{} 获取价格失败", url, anchorToken);
            return null;
        }
    }
}