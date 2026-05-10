/*
 *
 * * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import ch.hsr.geohash.GeoHash;
import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.globals.GlobalConstants;
import com.crio.qeats.models.ItemEntity;
import com.crio.qeats.models.RestaurantEntity;
import com.crio.qeats.repositories.RestaurantRepository;
import com.crio.qeats.utils.GeoLocation;
import com.crio.qeats.utils.GeoUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Provider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;


@Service
@Primary
public class RestaurantRepositoryServiceImpl implements RestaurantRepositoryService {



  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  @Autowired
  private RestaurantRepository restaurantRepository;

  private boolean isOpenNow(LocalTime time, RestaurantEntity res) {
    LocalTime openingTime = LocalTime.parse(res.getOpensAt());
    LocalTime closingTime = LocalTime.parse(res.getClosesAt());

    return time.isAfter(openingTime) && time.isBefore(closingTime);
  }


  // TODO: CRIO_TASK_MODULE_NOSQL
  // Objectives:
  // 1. Implement findAllRestaurantsCloseby.
  // 2. Remember to keep the precision of GeoHash in mind while using it as a key.
  // Check RestaurantRepositoryService.java file for the interface contract.
  public List<Restaurant> findAllRestaurantsCloseBy(Double latitude, Double longitude,
      LocalTime currentTime, Double servingRadiusInKms) {

    // Create GeoJSON point for query
    // Query query = new Query();
    // query.addCriteria(Criteria.where("location")
    //     .nearSphere(new org.springframework.data.geo.Point(longitude, latitude))
    //     .maxDistance(servingRadiusInKms / 6371.0)); // convert km to radians

    // List<RestaurantEntity> candidateRestaurants = mongoTemplate.find(query, RestaurantEntity.class);

    // // Filter by opening hours
    // List<Restaurant> nearbyOpenRestaurants =
    //     candidateRestaurants.stream().filter(res -> isOpenNow(currentTime, res))
    //         .map(res -> modelMapperProvider.get().map(res, Restaurant.class))
    //         .collect(Collectors.toList());

    // return nearbyOpenRestaurants;
     // ✅ Fetch all restaurants using repository (required by test)
    List<RestaurantEntity> allRestaurants = restaurantRepository.findAll();

    // ✅ Filter by open status & distance
    List<Restaurant> nearbyOpenRestaurants = allRestaurants.stream()
        .filter(r -> isRestaurantCloseByAndOpen(r, currentTime, latitude, longitude, servingRadiusInKms))
        .map(r -> modelMapperProvider.get().map(r, Restaurant.class))
        .collect(Collectors.toList());

    return nearbyOpenRestaurants;
  }



  // TODO: CRIO_TASK_MODULE_NOSQL
  // Objective:
  // 1. Check if a restaurant is nearby and open. If so, it is a candidate to be returned.
  // NOTE: How far exactly is "nearby"?

  /**
   * Utility method to check if a restaurant is within the serving radius at a given time.
   * 
   * @return boolean True if restaurant falls within serving radius and is open, false otherwise
   */
  private boolean isRestaurantCloseByAndOpen(RestaurantEntity restaurantEntity,
      LocalTime currentTime, Double latitude, Double longitude, Double servingRadiusInKms) {
    if (isOpenNow(currentTime, restaurantEntity)) {
      return GeoUtils.findDistanceInKm(latitude, longitude, restaurantEntity.getLatitude(),
          restaurantEntity.getLongitude()) < servingRadiusInKms;
    }

    return false;
  }

  @Override
  public List<ItemEntity> getMenu(String restaurantId) {
    Query query = new Query(Criteria.where("restaurantId").is(restaurantId));
    return mongoTemplate.find(query, ItemEntity.class);
  }



} 

