package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.City;

public interface CityRepository extends JpaRepository<City, Long> {
	List<City> findByState_Id(Long stateId);

}

