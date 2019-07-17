package com.example.administrator.light.Info;

import java.util.List;

/**
 * Created by chish on 2016/7/29.
 */
public class ConInfo {

    private List<String> DevNo;
    private List<String> DevName;
    private List<String> linkflag;
    private List<String> DevXDevY;

    public List<String> getDevNo() {
        return DevNo;
    }

    public void setDevNo(List<String> DevNo) {
        this.DevNo = DevNo;
    }

    public List<String> getDevName() {
        return DevName;
    }

    public void setDevName(List<String> DevName) {
        this.DevName = DevName;
    }

    public List<String> getLinkflag() {
        return linkflag;
    }

    public void setLinkflag(List<String> linkflag) {
        this.linkflag = linkflag;
    }

    public List<String> getDevXDevY() {
        return DevXDevY;
    }

    public void setDevXDevY(List<String> DevXDevY) {
        this.DevXDevY = DevXDevY;
    }
}
