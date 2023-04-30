package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Autowired
    ParkingLotRepository parkingLotRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    ReservationRepository reservationRepository;
    @Override
    public ParkingLot addParkingLot(String name, String address) {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setAddress(address);
        parkingLot.setName(name);

        return parkingLotRepository1.save(parkingLot);
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId).get();
        Spot spot = new Spot();
        spot.setOccupied(false);
        spot.setParkingLot(parkingLot);
        spot.setPricePerHour(pricePerHour);
        spot.setSpotType(getSpotType(numberOfWheels));

        parkingLot.getSpotList().add(spot);
        return spotRepository1.save(spot);
    }
    public SpotType getSpotType(int numberOfWheels){
        if(numberOfWheels == 2)
            return SpotType.TWO_WHEELER;
        if(numberOfWheels == 4)
            return SpotType.FOUR_WHEELER;

        return SpotType.OTHERS;
    }

    @Override
    public void deleteSpot(int spotId) {
        spotRepository1.deleteById(spotId);
    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId).get();
        Spot spot = spotRepository1.findById(spotId).get();
        spot.setPricePerHour(pricePerHour);
        return spotRepository1.save(spot);
    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        parkingLotRepository.deleteById(parkingLotId);
    }
}
