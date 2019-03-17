package com.example.nissan.reimbursement;

public class HistoryList
{
    private String type;
    private String status;
    private String currentApprover;
    private String amount;
    private String req;
    private String role;

    public HistoryList(String type, String status, String currentApprover, String amount,String req, String role) {
        this.type = type;
        this.role = role;
        this.status = status;
        this.currentApprover = currentApprover;
        this.amount = amount;
        this.req=req;
    }

    @Override
    public String toString() {
        return "HistoryList{" +
                "type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", currentApprover='" + currentApprover + '\'' +
                ", amount='" + amount + '\'' +
                ", req='" + req + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getType() {

        return type;
    }

    public String getReq() {

        return req;
    }

    public String getStatus() {
        return status;
    }

    public String getCurrentApprover() {
        return currentApprover;
    }

    public String getAmount() {
        return amount;
    }

}
