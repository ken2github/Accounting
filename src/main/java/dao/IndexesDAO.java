package dao;

import java.util.List;

import model2.Metric;

public interface IndexesDAO {

	List<Metric> findAverages(IndexQuery query, int firt_year, int firts_month, int last_year, int last_month);

	List<Metric> findBalances(IndexQuery query);

	List<Metric> findFlows(IndexQuery query);
}
