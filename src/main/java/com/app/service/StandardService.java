package com.app.service;

import com.app.entity.Standard;

import java.util.List;
import java.util.Optional;

public interface StandardService {

    List<Standard> getAllStandards();

    Optional<Standard> getStandardById(long id);

    Standard saveStandard(Standard standard);

    Standard updateStandard(Standard standard);

    void deleteStandard(long id);


}
