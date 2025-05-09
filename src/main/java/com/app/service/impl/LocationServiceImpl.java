package com.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entity.City;
import com.app.entity.State;
import com.app.repository.CityRepository;
import com.app.repository.StateRepository;
import com.app.service.LocationService;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private StateRepository stateRepo;

    @Autowired
    private CityRepository cityRepo;

    @Override
    public List<State> getAllStates() {
        return stateRepo.findAll();
    }

    @Override
    public List<City> getCitiesByStateId(Long stateId) {
        return cityRepo.findByState_Id(stateId);
    }

}
