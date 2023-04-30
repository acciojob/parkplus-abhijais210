package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Autowired
    ParkingLotServiceImpl parkingLotService;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        User user;
        try {
            user = userRepository3.findById(userId).get();
        }catch (Exception e){
            throw new Exception("Cannot make reservation");
        }

        ParkingLot parkingLot;
        try {
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        }catch (Exception e){
            throw new Exception("Cannot make reservation");
        }

        Spot spot = null;
        switch (numberOfWheels){
            case 2:
                spot = getMinAvailableSpot(2,parkingLotId);
                break;
            case 4:
                spot = getMinAvailableSpot(4,parkingLotId);
                break;
            default:
                spot = getMinAvailableSpot(numberOfWheels,parkingLotId);
        }
        //if no spot available
        if(spot == null)
            throw new Exception("Cannot make reservation");

        //now we have spot with minimum price
        Reservation reservation = new Reservation();
        reservation.setSpot(spot);
        reservation.setUser(user);
        reservation.setNumberOfHours(timeInHours);

        user.getReservationList().add(reservation);
        spot.getReservationList().add(reservation);
        spot.setOccupied(true);

        spotRepository3.save(spot);
        return reservation;
    }
    public Spot getMinAvailableSpot(int numberOfWheels,int parkingLotId) throws Exception {
        //get list pf all spot in that parkingLot
        List<Spot> spotList = spotRepository3.findAllByParkingLotId(parkingLotId);
        if(spotList.isEmpty())//if no spot in that parkingLot
            throw new Exception("Cannot make reservation");

        //get spot type
        int minPrice = Integer.MAX_VALUE;
        Spot spot = null;
        SpotType spotType = parkingLotService.getSpotType(numberOfWheels);

        //get the spot with minimum pricePerHour
        for(Spot s : spotList){
            if(spotType.equals(SpotType.TWO_WHEELER)) {
                if (s.getOccupied() && s.getPricePerHour() < minPrice){
                  spot = s;
                  minPrice = s.getPricePerHour();
                }
            } else if (spotType.equals(SpotType.FOUR_WHEELER)) {
                if (s.getOccupied() && !(s.getSpotType().equals(SpotType.TWO_WHEELER)) && s.getPricePerHour() < minPrice){
                    spot = s;
                    minPrice = s.getPricePerHour();
                }
            }else {
                if (s.getOccupied() && s.getSpotType().equals(SpotType.OTHERS) && s.getPricePerHour() < minPrice){
                    spot = s;
                    minPrice = s.getPricePerHour();
                }
            }
        }
        return  spot;
    }
}
