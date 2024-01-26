package vttp2023.batch4.paf.assessment.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {

	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;

	private final String ATTR_COLLECTION_NAME = "listings";

	/*
	 * db.listings.distinct("address.suburb");
	 */
	public List<String> getSuburbs(String country) {
		List<String> distinctSuburbs = template.getCollection(ATTR_COLLECTION_NAME)
				.distinct("address.suburb", String.class)
				.into(new ArrayList<>());
		List<String> cleanSuburbsString = distinctSuburbs.stream().filter(s -> s != null && !s.isEmpty())
				.collect(Collectors.toList());
		return cleanSuburbsString;
	}

	/*
	 * db.listings.find({
	 * "address.suburb": "Avalon",
	 * accommodates: {$gte: 2},
	 * price: { $lte: 1000} ,
	 * min_nights: {$lte: 1}},
	 * {"_id": 1,
	 * "name": 1,
	 * "accommodates": 1,
	 * "price":1})
	 * .sort({price: -1});
	 */
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
		Query q = Query.query(Criteria.where("address.suburb").is(suburb).and("accommodates")
				.gte(persons).and("price").lte(priceRange).and("min_nights").lte(duration))
				.with(Sort.by(Sort.Direction.DESC, "price"));
		q.fields().include("_id").include("name").include("accommodates").include("price");
		List<AccommodationSummary> listings = template.find(q, Document.class, ATTR_COLLECTION_NAME)
				.stream().peek(d -> d.put("price", d.get("price", Number.class).floatValue()))
				.map(d -> createAccommodationSummaryFromDoc(d))
				.collect(Collectors.toList());
				System.out.println(listings);
		return listings;
	}

	public AccommodationSummary createAccommodationSummaryFromDoc (Document d){
		AccommodationSummary a = new AccommodationSummary();
		a.setId(d.get("_id").toString());
		a.setName(d.get("name").toString());
		a.setAccomodates(d.getInteger("accommodates"));
		a.setPrice(Float.parseFloat(d.get("price").toString()));
        return a;
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}
