package it.unibg.jarfin.analytics_service.dto;

import java.util.Map;

public class FinancialReportDTO {
    private Double totalBalance;
    private Double totalIncomes;
    private Double totalExpenses;
    private Map<String, Double> breakdownByCategory;
    private String financialAdvice;
    private Double projectedMonthlyExpenses;
    private Double savingsRate;
	private String alertLevel;
    
    
	public Double getTotalBalance() {
		return totalBalance;
	}
	public void setTotalBalance(Double totalBalance) {
		this.totalBalance = totalBalance;
	}
	public Double getTotalIncomes() {
		return totalIncomes;
	}
	public void setTotalIncomes(Double totalIncomes) {
		this.totalIncomes = totalIncomes;
	}
	public Double getTotalExpenses() {
		return totalExpenses;
	}
	public void setTotalExpenses(Double totalExpenses) {
		this.totalExpenses = totalExpenses;
	}
	public Map<String, Double> getBreakdownByCategory() {
		return breakdownByCategory;
	}
	public void setBreakdownByCategory(Map<String, Double> breakdownByCategory) {
		this.breakdownByCategory = breakdownByCategory;
	}
	public String getFinancialAdvice() {
		return financialAdvice;
	}
	public void setFinancialAdvice(String financialAdvice) {
		this.financialAdvice = financialAdvice;
	}
    public Double getProjectedMonthlyExpenses() {
		return projectedMonthlyExpenses;
	}
	public void setProjectedMonthlyExpenses(Double projectedMonthlyExpenses) {
		this.projectedMonthlyExpenses = projectedMonthlyExpenses;
	}
	public Double getSavingsRate() {
		return savingsRate;
	}
	public void setSavingsRate(Double savingsRate) {
		this.savingsRate = savingsRate;
	}
	public String getAlertLevel() {
		return alertLevel;
	}
	public void setAlertLevel(String alertLevel) {
		this.alertLevel = alertLevel;
	}
}

