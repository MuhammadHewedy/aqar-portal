package crawler.aqarmap.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface ApartmentRepo extends JpaRepository<Apartment, Long>, QueryDslPredicateExecutor<Apartment> {

	Long countByAdNumber(String adNumber);
}
