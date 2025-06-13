package com.app.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.app.entity.City;
import com.app.entity.State;

@Service
public interface LocationService {

    List<State> getAllStates();

    List<City> getCitiesByStateId(Long stateId);


}
