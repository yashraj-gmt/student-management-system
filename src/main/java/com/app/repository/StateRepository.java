package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entity.State;

public interface StateRepository extends JpaRepository<State, Long> {
	
}