package com.app.service.impl;

import com.app.entity.Standard;
import com.app.repository.StandardRepository;
import com.app.service.StandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StandardServiceImpl implements StandardService {

    @Autowired
    private StandardRepository standardRepository;

    @Override
    public List<Standard> getAllStandards() {
        return standardRepository.findAll();
    }

    @Override
    public Optional<Standard> getStandardById(long id) {
        return standardRepository.findById(id);
    }

    @Override
    public Standard saveStandard(Standard standard) {
        return standardRepository.save(standard);
    }

    @Override
    public Standard updateStandard(Standard standard) {
        return standardRepository.save(standard);
    }

    @Override
    public void deleteStandard(long id) {
        standardRepository.deleteById(id);
    }
}
