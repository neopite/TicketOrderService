package lab2_5.com.company.neophite.model.dao.impl;

import lab2_5.com.company.neophite.model.dao.TrainRouteDAO;
import lab2_5.com.company.neophite.model.entity.TrainRoute;
import lab2_5.com.company.neophite.model.mapper.impl.TrainRouteMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainRouteDAOImpl extends TrainRouteDAO {

    private TrainRouteMapper trainRouteMapper = new TrainRouteMapper();

    private final String table = "trains_route";
    private final String CREATE = "insert into " + table+
            " (station_start,start_date,station_end,end_date) values(?,?,?,?)";
    private final String  FIND_ALL = "select *,s.name as start_station,s2.name as end_station , s.id_station as id_start , s2.id_station as id_end  from trains_route left join stations s on trains_route.station_start = s.id_station left join stations s2 on trains_route.station_end = s2.id_station ";
    private final String FIND_BY_TRAIN_ROUTE_ID = FIND_ALL + " where id_train_route=? and trains_route.isActive=true";
    private final String FIND_BY_FIRST_STATION = FIND_ALL + " where station_start=? and trains_route.isActive=true";
    private final String FIND_BY_END_STATION = FIND_ALL + " where station_end=? and trains_route.isActive=true";
    private final String FIND_ROUTES_BETWEEN_TWO_STATION = FIND_ALL +" where station_start=? and station_end=? and (start_date>=? and start_date<=?) and trains_route.isActive=true";
    private final String FIND_ALL_ROUTES_BY_STATION = FIND_ALL +" where (station_start=? or station_end=?) and trains_route.isActive=true";
    private final String DELETE_ROUTE_BY_ID = "update " + table + " set isActive=false where id_train_route=?";
    private final String DELETE_ROUTES_BY_STATION_ID = "update " + table+ " set isActive=false where station_start=? or station_end=?";
    private final String GET_ALL_TRAIN_ROUTES = FIND_ALL+ " where trains_route.isActive=true";

    public TrainRouteDAOImpl(Connection connection) {
        super(connection);
    }

    public List<TrainRoute> getTrainRoutesByFirstStation(long name) {
        List<TrainRoute> listOfTraintRoutes = new ArrayList<TrainRoute>();
        try (PreparedStatement preparedStatement = getStatement(FIND_BY_FIRST_STATION)) {
            preparedStatement.setLong(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
               TrainRoute trainRoute = trainRouteMapper.extractEntityFromTheRS(resultSet);
                listOfTraintRoutes.add(trainRoute);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listOfTraintRoutes;
    }

    public List<TrainRoute> getTrainRoutesBySecondStation(long name) {
        List<TrainRoute> listOfTraintRoutes = new ArrayList<TrainRoute>();
        try (PreparedStatement preparedStatement = getStatement(FIND_BY_END_STATION)) {
            preparedStatement.setLong(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TrainRoute trainRoute = trainRouteMapper.extractEntityFromTheRS(resultSet);
                listOfTraintRoutes.add(trainRoute);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listOfTraintRoutes;
    }

    @Override
    public List<TrainRoute> getTrainRoutesBetweenTwoStations(long first, long second,Timestamp from,Timestamp to) {
        List<TrainRoute> listOfRoutes = new ArrayList<>();
        try(PreparedStatement preparedStatement = getStatement(FIND_ROUTES_BETWEEN_TWO_STATION)){
            preparedStatement.setLong(1,first);
            preparedStatement.setLong(2,second);
            preparedStatement.setTimestamp(3,from);
            preparedStatement.setTimestamp(4,to);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                TrainRoute trainRoute = trainRouteMapper.extractEntityFromTheRS(resultSet);
                listOfRoutes.add(trainRoute);
            }
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        return listOfRoutes;
    }

    @Override
    public boolean deleteAllRoutesWithStationId(long stationId) {
        int isExecuted = 0;
        try(PreparedStatement preparedStatement = getStatement(DELETE_ROUTES_BY_STATION_ID)) {
            preparedStatement.setLong(1, stationId);
            preparedStatement.setLong(2, stationId);
            isExecuted = preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return isExecuted != 0;
    }

    @Override
    public List<TrainRoute> getAllRoutesByStation(long stationId) {
        List<TrainRoute> listOfRoutes = new ArrayList<>();
        try(PreparedStatement preparedStatement = getStatement(FIND_ALL_ROUTES_BY_STATION)){
            preparedStatement.setLong(1 , stationId);
            preparedStatement.setLong(2 , stationId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                TrainRoute trainRoute = trainRouteMapper.extractEntityFromTheRS(resultSet);
                listOfRoutes.add(trainRoute);
            }
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        return listOfRoutes;
    }


    public TrainRoute create(TrainRoute trainRoute) {
        try (PreparedStatement preparedStatement = getStatement(CREATE)) {
            preparedStatement.setLong(1, trainRoute.getStartStation().getId());
            preparedStatement.setTimestamp(2,trainRoute.getStartDate());
            preparedStatement.setLong(3, trainRoute.getFinishStation().getId());
            preparedStatement.setTimestamp(4, trainRoute.getFinishDate());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return trainRoute;
    }

    public TrainRoute findByKey(Long key) {
        TrainRoute trainRoute = null;
        try (PreparedStatement preparedStatement = getStatement(FIND_BY_TRAIN_ROUTE_ID)) {
            preparedStatement.setLong(1, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                 trainRoute = trainRouteMapper.extractEntityFromTheRS(resultSet);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return trainRoute;
    }

    @Override
    public boolean deleteByKey(Long key) {
        boolean isExecuted = false;
        try(
                PreparedStatement preparedStatement = getStatement(DELETE_ROUTE_BY_ID)) {
            preparedStatement.setLong(1, key);
            isExecuted = preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return isExecuted;
    }

    public List<TrainRoute> getAll() {
        List<TrainRoute> listOTraintRoutes = new ArrayList<TrainRoute>();
        try(        PreparedStatement preparedStatement = getStatement(GET_ALL_TRAIN_ROUTES)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TrainRoute trainRoute = trainRouteMapper.extractEntityFromTheRS(resultSet);

                listOTraintRoutes.add(trainRoute);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listOTraintRoutes;
    }
}
