package com.zhoubi.graindepot.bean;

import java.util.Date;

/**
 * Created by Administrator on 2019/1/31/031.
 */
public class InoutBean extends Inout{
    private String grainname;//粮食品种
    private String grainattrname;//粮食性质
    private Double wdeduratex;//水分二次扣率
    private Double ideduratex;//杂质二次扣率
    private Double odeduratex;//其他二次扣率
    private Double extradedu;//增加扣量

    public String getGrainname() {
        return grainname;
    }

    public void setGrainname(String grainname) {
        this.grainname = grainname;
    }

    public String getGrainattrname() {
        return grainattrname;
    }

    public void setGrainattrname(String grainattrname) {
        this.grainattrname = grainattrname;
    }

    public Double getWdeduratex() {
        return wdeduratex;
    }

    public void setWdeduratex(Double wdeduratex) {
        this.wdeduratex = wdeduratex;
    }

    public Double getIdeduratex() {
        return ideduratex;
    }

    public void setIdeduratex(Double ideduratex) {
        this.ideduratex = ideduratex;
    }

    public Double getOdeduratex() {
        return odeduratex;
    }

    public void setOdeduratex(Double odeduratex) {
        this.odeduratex = odeduratex;
    }

    @Override
    public Double getExtradedu() {
        return extradedu;
    }

    @Override
    public void setExtradedu(Double extradedu) {
        this.extradedu = extradedu;
    }
}
