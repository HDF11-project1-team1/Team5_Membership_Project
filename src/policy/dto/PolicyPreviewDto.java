package policy.dto;

public class PolicyPreviewDto {

    private final int branchId;
    private final String branchName;
    private final Integer brandId;
    private final String brandName;
    private final Integer paymentId;
    private final String paymentName;
    private final Integer membershipId;
    private final String membershipName;
    private final Integer loungeId;
    private final String loungeName;
    private final String currentValue;

    public PolicyPreviewDto(int branchId, String branchName, Integer brandId, String brandName,
                            Integer paymentId, String paymentName, Integer membershipId, String membershipName,
                            Integer loungeId, String loungeName, String currentValue) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.brandId = brandId;
        this.brandName = brandName;
        this.paymentId = paymentId;
        this.paymentName = paymentName;
        this.membershipId = membershipId;
        this.membershipName = membershipName;
        this.loungeId = loungeId;
        this.loungeName = loungeName;
        this.currentValue = currentValue;
    }

    public int getBranchId() {
        return branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public Integer getMembershipId() {
        return membershipId;
    }

    public String getMembershipName() {
        return membershipName;
    }

    public Integer getLoungeId() {
        return loungeId;
    }

    public String getLoungeName() {
        return loungeName;
    }

    public String getCurrentValue() {
        return currentValue;
    }
}
