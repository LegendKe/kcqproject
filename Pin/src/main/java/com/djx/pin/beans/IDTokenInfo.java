package com.djx.pin.beans;

import java.util.List;

/**
 * Created by lenovo on 2016/6/23.
 */
public class IDTokenInfo {
    public int size;
    public List<IDToken> list;

    public class IDToken{
        public String id;
        public String token;
    }
}
