package com.app.service;

import java.util.List;

import com.app.entity.City;
import com.app.entity.State;

public interface LocationService {
    List<State> getAllStates();
    List<City> getCitiesByStateId(Long stateId);
}
