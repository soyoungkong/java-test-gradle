package com.mc.testexample.robot;


import java.util.Optional;

public interface PartService {
    Optional<Part> findById(Long partId);
    void validate(Long partId);

    void notify(Robot robot);

    void notify(Part part);
}
