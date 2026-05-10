
/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.services;

import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.exchanges.GetRestaurantsRequest;
import com.crio.qeats.exchanges.GetRestaurantsResponse;
import com.crio.qeats.models.ItemEntity;
import com.crio.qeats.repositoryservices.RestaurantRepositoryService;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import com.crio.qeats.repositoryservices.RestaurantRepositoryService;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RestaurantServiceImpl implements RestaurantService {

  private final Double peakHoursServingRadiusInKms = 3.0;
  private final Double normalHoursServingRadiusInKms = 5.0;
  @Autowired
  private RestaurantRepositoryService restaurantRepositoryService;


  // TODO: CRIO_TASK_MODULE_RESTAURANTSAPI - Implement findAllRestaurantsCloseby.
  // Check RestaurantService.java file for the interface contract.
  @Override
public GetRestaurantsResponse findAllRestaurantsCloseBy(
        GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) {

    // Validate request
    if (getRestaurantsRequest == null
        || getRestaurantsRequest.getLatitude() == null
        || getRestaurantsRequest.getLongitude() == null) {
      return new GetRestaurantsResponse(Collections.emptyList());
    }

    double latitude = getRestaurantsRequest.getLatitude();
    double longitude = getRestaurantsRequest.getLongitude();

    // Validate latitude and longitude ranges
    if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
      return new GetRestaurantsResponse(Collections.emptyList());
    }

    // Always use IST timezone to avoid UTC mismatch on remote servers
    if (currentTime == null) {
      currentTime = LocalTime.now(ZoneId.of("Asia/Kolkata"));
    }

    // Determine serving radius based on peak hours
    double servingRadius = isPeakHour(currentTime)
        ? peakHoursServingRadiusInKms
        : normalHoursServingRadiusInKms;

    // Fetch restaurants from repository
    List<Restaurant> restaurants = restaurantRepositoryService
        .findAllRestaurantsCloseBy(latitude, longitude, currentTime, servingRadius);

    if (restaurants == null) {
      restaurants = Collections.emptyList();
    }

    return new GetRestaurantsResponse(restaurants);
  }

  // --------------------------------------------------------------------------
  // Helper: Determine peak hours (inclusive of boundary hours)
  // --------------------------------------------------------------------------
  private boolean isPeakHour(LocalTime time) {
    int hour = time.getHour();

    // Peak hours: 8–10 AM, 1–2 PM, 7–9 PM
    return (hour >= 8 && hour <= 10)
        || (hour >= 13 && hour <= 14)
        || (hour >= 19 && hour <= 21);
  }

  // --------------------------------------------------------------------------
  // Menu retrieval
  // --------------------------------------------------------------------------
  @Override
  public List<ItemEntity> getMenu(String restaurantId) {
    if (restaurantId == null || restaurantId.isEmpty()) {
      log.warn("getMenu called with empty restaurantId");
      return Collections.emptyList();
    }
    return restaurantRepositoryService.getMenu(restaurantId);
  }
}

