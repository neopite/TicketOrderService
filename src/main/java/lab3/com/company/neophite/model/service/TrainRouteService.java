package lab3.com.company.neophite.model.service;

import lab3.com.company.neophite.model.dao.DAOFactory;
import lab3.com.company.neophite.model.dao.TrainRouteDAO;
import lab3.com.company.neophite.model.dao.TrainTripDAO;
import lab3.com.company.neophite.model.dao.connection.BasicConnectionPool;
import lab3.com.company.neophite.model.dao.impl.TrainRouteDAOImpl;
import lab3.com.company.neophite.model.entity.TrainRoute;
import lab3.com.company.neophite.model.entity.TrainTrip;
import lab3.com.company.neophite.model.exception.StationNotFoundException;
import lab3.com.company.neophite.model.exception.TrainRouteNotFoundException;
import lab3.com.company.neophite.model.exception.TrainTripNotFoundException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrainRouteService {
    private final Connection transactionConnection;
    private DAOFactory daoFactory ;
    private BasicConnectionPool basicConnectionPool;

    public TrainRouteService(DAOFactory daoFactory) {
        this.transactionConnection = BasicConnectionPool.getInstance().getConnection();
        this.daoFactory = daoFactory;
        this.basicConnectionPool = BasicConnectionPool.getInstance();
    }

    public TrainRoute addTrainRoute(TrainRoute trainRoute) {
        try(TrainRouteDAO trainRouteDAO = daoFactory.createTrainRouteDAO(basicConnectionPool.getConnection())) {
            return trainRouteDAO.create(trainRoute);
        }
    }

    public List<TrainRoute> getAllRoutes(){
        try (TrainRouteDAO trainTripDAO = daoFactory.createTrainRouteDAO(basicConnectionPool.getConnection())){
                return trainTripDAO.getAll();
            }
    }

    public void deleteTrainRoute(long trainRoute, Date from , Date to) {
        try(TrainRouteDAO trainRouteDAO = DAOFactory.getDaoFactory().createTrainRouteDAO(transactionConnection);
            TrainTripDAO trainTripDAO = DAOFactory.getDaoFactory().createTrainTripDAO(transactionConnection)
        ) {
            transactionConnection.setAutoCommit(false);

            List<TrainTrip> trainTrips = trainTripDAO.findTrainTripsByRoute(trainRoute);
            if(trainTrips.isEmpty()){
                trainRouteDAO.deleteByKey(trainRoute,from,to);
                transactionConnection.commit();
                transactionConnection.setAutoCommit(true);
                return;
            }
            boolean trainRouteIstrue = trainRouteDAO.deleteAllRoutesWithStationId(trainRoute);
            if (!trainRouteIstrue) {
                throw new TrainRouteNotFoundException("Train Routes with id : " + trainRoute + "  not found");
            }
            boolean isDeleted = trainTripDAO.deleteAllTrainTripsByRouteId(trainRoute);
            if (!isDeleted) {
                throw new TrainTripNotFoundException("Train trip with id of route : " + trainRoute + " not found");
            }


        } catch (SQLException | StationNotFoundException | TrainRouteNotFoundException | TrainTripNotFoundException exception) {
            try {
                transactionConnection.rollback();
            } catch (SQLException throwables) {
                exception.printStackTrace();
            }
        }
    }

}
