package com.spillhuset.oddjob.Enums;

public enum GuildRank {
    a(1,1.1),
    b(2,1.2),
    c(3,1.3);
    private final double claimsMultiplier;
    private final double interestRate;

    GuildRank(double claimsMultiplier,double interestRate) {
        this.claimsMultiplier = claimsMultiplier;
        this.interestRate = interestRate;
    }

    public double getClaims() {
        return claimsMultiplier;
    }

    public double getInterest() {
        return interestRate;
    }
}
