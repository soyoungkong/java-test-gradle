package com.mc.testexample.robot;


import java.util.Optional;

public class RobotService {
    private final PartService partService;
    private final RobotRepository repository;

    public RobotService(PartService partService, RobotRepository repository) {
        // assert를 사용해서 null 체크
        assert partService != null;
        assert repository != null;
        this.partService = partService;
        this.repository = repository;
    }

    public Robot createNewRobot(Long partId, Robot robot){
        Optional<Part> part = partService.findById(partId);
        robot.setPart(part.orElseThrow(()-> new IllegalArgumentException("Parts doesn't exist for id :" + partId)));
        return repository.save(robot);
    }
}