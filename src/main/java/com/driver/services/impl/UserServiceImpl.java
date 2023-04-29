package com.driver.services.impl;

import com.driver.controllers.ReservationController;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.model.User;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    UserRepository userRepository4;
    @Autowired
    ReservationRepository reservationRepository;
    @Override
    public void deleteUser(Integer userId) {
        //first we delete all the reservation history of this user
            Optional<User> optionalUser = userRepository4.findById(userId);
            if(optionalUser.isPresent()) {
                User user = optionalUser.get();
                List<Reservation> reservationList = user.getReservationList();
                for (Reservation reservation : reservationList) {
                    Spot spot = reservation.getSpot();
                    spot.setOccupied(false);//update the availability status
                    spot.getReservationList().remove(reservation);//remove from spot
                }
                userRepository4.delete(user);
            }
    }

    @Override
    public User updatePassword(Integer userId, String password) {
        User user =  userRepository4.findById(userId).get();
        user.setPassword(password);
        return userRepository4.save(user);
    }

    @Override
    public void register(String name, String phoneNumber, String password) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);

        userRepository4.save(user);
    }
}
