package com.example.mezereon.Home.Model;

/**
 * Created by Mezereon on 2017/8/23.
 */

public class Order {
    private int id;
    private String name;// 名字
    private String phone;// 联系电话
    private String time;// 预定的时间点
    private int routeType;// 0代表南湖到浑南, 1代表浑南到南湖
    private String station;// 站点名字
    private String isFinished;// 是否上车
    private String number;// 工号

    public Order(int id, String name, String phone, String time, int routeType, String station, String isFinished, String number) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.time = time;
        this.routeType = routeType;
        this.station = station;
        this.isFinished = isFinished;
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRouteType() {
        return routeType;
    }

    public void setRouteType(int routeType) {
        this.routeType = routeType;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(String isFinished) {
        this.isFinished = isFinished;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
