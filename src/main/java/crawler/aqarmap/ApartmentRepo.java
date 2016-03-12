package crawler.aqarmap;

import org.springframework.data.repository.CrudRepository;

public interface ApartmentRepo extends CrudRepository<Apartment, Long> {

	Long countByAdNumber(String adNumber);
}
