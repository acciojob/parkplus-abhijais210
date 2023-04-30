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
        Reservation reservation = new Reservation();

        if(userRepository3.findById(userId) == null)
            throw new Exception("Cannot make reservation");

       User user = userRepository3.findById(userId).get();

        if(parkingLotRepository3.findById(parkingLotId) == null)
            throw new Exception("Cannot make reservation");

        ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();

        Spot spot = null;

        List<Spot> spotList = parkingLot.getSpotList();
        int minPrice = Integer.MAX_VALUE;

        //get the spot with minimum pricePerHour
        for(Spot s : spotList) {
            if (!s.getOccupied()) {
                if (s.getSpotType().equals(SpotType.TWO_WHEELER)) {
                    if (numberOfWheels <= 2) {
                        if (s.getPricePerHour() < minPrice) {
                            spot = s;
                            minPrice = s.getPricePerHour();
                        }
                    }
                } else if (s.getSpotType().equals(SpotType.FOUR_WHEELER)) {
                    if (numberOfWheels <= 4) {
                        if (s.getPricePerHour() < minPrice) {
                            spot = s;
                            minPrice = s.getPricePerHour();
                        }
                    }
                } else {
                    if (s.getPricePerHour() < minPrice) {
                        spot = s;
                        minPrice = s.getPricePerHour();
                    }
                }
            }
        }

        if(spot == null) //if no spot available
            throw new Exception("Cannot make reservation");

        //now we have spot with minimum price

        reservation.setSpot(spot);
        reservation.setUser(user);
        reservation.setNumberOfHours(timeInHours);

        user.getReservationList().add(reservation);
        spot.getReservationList().add(reservation);
        spot.setOccupied(true);

        userRepository3.save(user);
        spotRepository3.save(spot);
        return reservation;
    }
}
