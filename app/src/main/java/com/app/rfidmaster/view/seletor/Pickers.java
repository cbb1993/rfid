package com.app.rfidmaster.view.seletor;

import java.io.Serializable;

/**
 * @author: cbb
 * @date: 2020/12/20 16:24
 * @description:
 */

public class Pickers implements Serializable {

    private static final long serialVersionUID = 1L;

    private String showContent;
    private int index;

    public String getShowContent() {
        return showContent;
    }

    public int getIndex() {
        return index;
    }

    public Pickers(String showContent, int index) {
        super();
        this.showContent = showContent;
        this.index = index;
    }

    public Pickers() {
        super();
    }

}

