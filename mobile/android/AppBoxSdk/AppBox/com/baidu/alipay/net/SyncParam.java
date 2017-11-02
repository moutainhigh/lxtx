package com.baidu.alipay.net;

public class SyncParam {
    private String Refer = "";
    private String Reason = "";
    private String Stat = "1";
    private String Guardssubmit = "";

    public String CreatParam() {
        return "refer=" + Refer + "&reason=" + Reason + "&Stat=" + Stat
                + "&Guardssubmit=" + Guardssubmit;
    }

    public String getRefer() {
        return Refer;
    }

    public void setRefer(String refer) {
        Refer = refer;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getStat() {
        return Stat;
    }

    public void setStat(String stat) {
        Stat = stat;
    }

    public String getGuardssubmit() {
        return Guardssubmit;
    }

    public void setGuardssubmit(String guardssubmit) {
        Guardssubmit = guardssubmit;
    }

}
