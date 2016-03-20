package aqar.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
public class Apartment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	@JsonIgnore
	private String city;
	private String cityRegion;
	private String district;
	@JsonIgnore
	private String adNumber;
	@JsonIgnore
	private LocalDate adDate;
	private String adMobile;
	@JsonIgnore
	private String propertyType;
	private Long price;
	@JsonIgnore
	private String advertiser;
	private Integer area;
	@JsonIgnore
	private String payMethod;
	private Long numOfRooms;
	private Long floorNumber;
	private Long wcNumber;
	private Long buildYear;
	private String categoryOfFinishes;
	@Column(length = 7000)
	private String description;
	private Double latitude;
	private Double longitude;
	@JsonIgnore
	@Column(length = 2000)
	@ElementCollection(fetch = FetchType.LAZY)
	private List<String> imageUrls = new ArrayList<>();
	private String refUrl;
}
