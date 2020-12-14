package lab3.com.company.neophite.controller.command.impl;

import lab3.com.company.neophite.controller.command.Command;
import lab3.com.company.neophite.model.entity.Station;
import lab3.com.company.neophite.model.service.StationService;

import javax.servlet.http.HttpServletRequest;

public class AddStationCommand implements Command {
    private StationService stationService;

    public AddStationCommand(StationService stationService) {
        this.stationService = stationService;
    }

    @Override
    public String execute(HttpServletRequest request) {
        String newStation = request.getParameter("station_name");
        Station station = new Station(newStation);
        stationService.addStation(station);
        return "/getAdminPage";
    }
}
