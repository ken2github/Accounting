package dao;

import java.util.List;

public interface IndexesDAO2 {

	List<MonthAverageFlowMetric> findAverages(int year);

	List<BalanceMetric> findBalances(int year);

	List<FlowMetric> findFlows(int year);
}
