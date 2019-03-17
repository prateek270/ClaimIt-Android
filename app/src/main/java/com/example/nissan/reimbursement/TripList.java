package com.example.nissan.reimbursement;

public class TripList {
    private String name;
    private int amount;

    public TripList(){

    }

    public  TripList(String name,int amount){
        this.name=name;
        this.amount=amount;

    }

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "HistoryList{" +
                "name='" + name + '\'' +
                "amount='" + 0 + '\'' +
                '}';
    }
}
