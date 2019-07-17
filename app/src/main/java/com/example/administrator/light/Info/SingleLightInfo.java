package com.example.administrator.light.Info;

import java.util.List;

/**
 * Created by chish on 2016/8/10.
 */
public class SingleLightInfo {

    /**
     * DevNo : 101
     * DevX : 119.290056
     * DevY : 26.132935
     * stat : 0
     * rod_num : 1-1
     * rod_num_0x : 257
     * flag : true
     * alarm_info :
     * rod_name :
     * I1 : 0.39
     * I2 : 0
     * I3 : 8.6
     * I4 : 0
     * post_G_name :
     * Uid : 11211
     * single_state3 : 1灯手动开灯,2灯对时不成功全开,#1100
     * update_dtm : /Date(1460259646000)/
     * rod_real : 156-233
     * tmp_char1 :
     * tmp_char2 :
     * rod_pro_no1 :
     * rod_pro_no2 :
     * rod_sit1 :
     * rod_sit2 :
     * DevX2 : 0
     * DevY2 : 0
     */

    private List<SingleMapTableVoltViewBean> single_map_table_volt_view;

    public List<SingleMapTableVoltViewBean> getSingle_map_table_volt_view() {
        return single_map_table_volt_view;
    }

    public void setSingle_map_table_volt_view(List<SingleMapTableVoltViewBean> single_map_table_volt_view) {
        this.single_map_table_volt_view = single_map_table_volt_view;
    }

    public static class SingleMapTableVoltViewBean {
        private int DevNo;
        private double DevX;
        private double DevY;
        private int stat;
        private String rod_num;
        private int rod_num_0x;
        private boolean flag;
        private String alarm_info;
        private String rod_name;
        private double I1;
        private int I2;
        private double I3;
        private int I4;
        private String post_G_name;
        private int Uid;
        private String single_state3;
        private String update_dtm;
        private String rod_real;
        private String tmp_char1;
        private String tmp_char2;
        private String rod_pro_no1;
        private String rod_pro_no2;
        private String rod_sit1;
        private String rod_sit2;
        private int DevX2;
        private int DevY2;

        public int getDevNo() {
            return DevNo;
        }

        public void setDevNo(int DevNo) {
            this.DevNo = DevNo;
        }

        public double getDevX() {
            return DevX;
        }

        public void setDevX(double DevX) {
            this.DevX = DevX;
        }

        public double getDevY() {
            return DevY;
        }

        public void setDevY(double DevY) {
            this.DevY = DevY;
        }

        public int getStat() {
            return stat;
        }

        public void setStat(int stat) {
            this.stat = stat;
        }

        public String getRod_num() {
            return rod_num;
        }

        public void setRod_num(String rod_num) {
            this.rod_num = rod_num;
        }

        public int getRod_num_0x() {
            return rod_num_0x;
        }

        public void setRod_num_0x(int rod_num_0x) {
            this.rod_num_0x = rod_num_0x;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public String getAlarm_info() {
            return alarm_info;
        }

        public void setAlarm_info(String alarm_info) {
            this.alarm_info = alarm_info;
        }

        public String getRod_name() {
            return rod_name;
        }

        public void setRod_name(String rod_name) {
            this.rod_name = rod_name;
        }

        public double getI1() {
            return I1;
        }

        public void setI1(double I1) {
            this.I1 = I1;
        }

        public int getI2() {
            return I2;
        }

        public void setI2(int I2) {
            this.I2 = I2;
        }

        public double getI3() {
            return I3;
        }

        public void setI3(double I3) {
            this.I3 = I3;
        }

        public int getI4() {
            return I4;
        }

        public void setI4(int I4) {
            this.I4 = I4;
        }

        public String getPost_G_name() {
            return post_G_name;
        }

        public void setPost_G_name(String post_G_name) {
            this.post_G_name = post_G_name;
        }

        public int getUid() {
            return Uid;
        }

        public void setUid(int Uid) {
            this.Uid = Uid;
        }

        public String getSingle_state3() {
            return single_state3;
        }

        public void setSingle_state3(String single_state3) {
            this.single_state3 = single_state3;
        }

        public String getUpdate_dtm() {
            return update_dtm;
        }

        public void setUpdate_dtm(String update_dtm) {
            this.update_dtm = update_dtm;
        }

        public String getRod_real() {
            return rod_real;
        }

        public void setRod_real(String rod_real) {
            this.rod_real = rod_real;
        }

        public String getTmp_char1() {
            return tmp_char1;
        }

        public void setTmp_char1(String tmp_char1) {
            this.tmp_char1 = tmp_char1;
        }

        public String getTmp_char2() {
            return tmp_char2;
        }

        public void setTmp_char2(String tmp_char2) {
            this.tmp_char2 = tmp_char2;
        }

        public String getRod_pro_no1() {
            return rod_pro_no1;
        }

        public void setRod_pro_no1(String rod_pro_no1) {
            this.rod_pro_no1 = rod_pro_no1;
        }

        public String getRod_pro_no2() {
            return rod_pro_no2;
        }

        public void setRod_pro_no2(String rod_pro_no2) {
            this.rod_pro_no2 = rod_pro_no2;
        }

        public String getRod_sit1() {
            return rod_sit1;
        }

        public void setRod_sit1(String rod_sit1) {
            this.rod_sit1 = rod_sit1;
        }

        public String getRod_sit2() {
            return rod_sit2;
        }

        public void setRod_sit2(String rod_sit2) {
            this.rod_sit2 = rod_sit2;
        }

        public int getDevX2() {
            return DevX2;
        }

        public void setDevX2(int DevX2) {
            this.DevX2 = DevX2;
        }

        public int getDevY2() {
            return DevY2;
        }

        public void setDevY2(int DevY2) {
            this.DevY2 = DevY2;
        }
    }
}
