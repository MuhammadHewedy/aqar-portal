package crawler.aqarmap.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface ApartmentRepo extends JpaRepository<Apartment, Long>, QueryDslPredicateExecutor<Apartment>,
		QuerydslBinderCustomizer<QApartment> {

	@SuppressWarnings("unchecked")
	@Override
	default void customize(QuerydslBindings bindings, QApartment root) {

		bindings.bind(root.adNumber).first((path, value) -> path.contains(value));
		bindings.bind(root.numOfRooms).first((path, value) -> path.goe(value));
		bindings.bind(root.adDate).first((path, value) -> path.goe(value));
	}
}
