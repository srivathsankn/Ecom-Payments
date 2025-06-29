package com.srivath.payment.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.web.bind.annotation.RequestParam;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private Long orderId;
    private String paymentLink;
    private Double amount;
    private String paymentDate; //for Part Payments
    private String paymentAdditionalInfo; // for Part Payments
    private Double paidAmount; // for Part Payments
    private String partPaymentMethod; // for Part Payments
    private String status;
    private String razorpayPaymentLinkReferenceId;
    private String razorpayPaymentId;
    private String razorpayPaymentLinkId;
    private String razorpayPaymentLinkStatus;
    private String razorpaySignature;

    public Payment() {
    }

    public Payment (Payment copy)
    {
        this.userName = copy.userName;
        this.userEmail = copy.userEmail;
        this.userPhone = copy.userPhone;
        this.orderId = copy.orderId;
        this.paymentLink = copy.paymentLink;
        this.amount = copy.amount;
        this.paymentDate = copy.paymentDate;
        this.paymentAdditionalInfo = copy.paymentAdditionalInfo;
        this.paidAmount = copy.paidAmount;
        this.partPaymentMethod = copy.partPaymentMethod;
        this.status = copy.status;
        this.razorpayPaymentLinkReferenceId = copy.razorpayPaymentLinkReferenceId;
        this.razorpayPaymentId = copy.razorpayPaymentId;
        this.razorpayPaymentLinkId = copy.razorpayPaymentLinkId;
        this.razorpayPaymentLinkStatus = copy.razorpayPaymentLinkStatus;
        this.razorpaySignature = copy.razorpaySignature;
    }

    public Payment(Long paymentId, Long orderId, String paymentMethod, Double amount, String status) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentLink = paymentMethod;
        this.amount = amount;
        this.status = status;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPaymentLink() {
        return paymentLink;
    }

    public void setPaymentLink(String paymentLink) {
        this.paymentLink = paymentLink;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentAdditionalInfo() {
        return paymentAdditionalInfo;
    }

    public void setPaymentAdditionalInfo(String paymentAdditionalInfo) {
        this.paymentAdditionalInfo = paymentAdditionalInfo;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPartPaymentMethod() {
        return partPaymentMethod;
    }

    public void setPartPaymentMethod(String partPaymentMethod) {
        this.partPaymentMethod = partPaymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRazorpayPaymentLinkReferenceId() {
        return razorpayPaymentLinkReferenceId;
    }

    public void setRazorpayPaymentLinkReferenceId(String razorpayPaymentLinkReferenceId) {
        this.razorpayPaymentLinkReferenceId = razorpayPaymentLinkReferenceId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpayPaymentLinkId() {
        return razorpayPaymentLinkId;
    }

    public void setRazorpayPaymentLinkId(String razorpayPaymentLinkId) {
        this.razorpayPaymentLinkId = razorpayPaymentLinkId;
    }

    public String getRazorpayPaymentLinkStatus() {
        return razorpayPaymentLinkStatus;
    }

    public void setRazorpayPaymentLinkStatus(String razorpayPaymentLinkStatus) {
        this.razorpayPaymentLinkStatus = razorpayPaymentLinkStatus;
    }

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", orderId=" + orderId +
                ", paymentLink='" + paymentLink + '\'' +
                ", amount=" + amount +
                ", paymentDate='" + paymentDate + '\'' +
                ", paymentAdditionalInfo='" + paymentAdditionalInfo + '\'' +
                ", paidAmount=" + paidAmount +
                ", partPaymentMethod='" + partPaymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", razorpayPaymentLinkReferenceId='" + razorpayPaymentLinkReferenceId + '\'' +
                ", razorpayPaymentId='" + razorpayPaymentId + '\'' +
                ", razorpayPaymentLinkId='" + razorpayPaymentLinkId + '\'' +
                ", razorpayPaymentLinkStatus='" + razorpayPaymentLinkStatus + '\'' +
                ", razorpaySignature='" + razorpaySignature + '\'' +
                '}';
    }
}
