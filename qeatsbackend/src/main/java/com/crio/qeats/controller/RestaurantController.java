/*
 *
 * * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.controller;

import com.crio.qeats.exchanges.GetRestaurantsRequest;
import com.crio.qeats.exchanges.GetRestaurantsResponse;
import com.crio.qeats.models.ItemEntity;
import com.crio.qeats.services.RestaurantService;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import com.crio.qeats.services.RestaurantService;
import java.time.LocalTime;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(RestaurantController.RESTAURANT_API_ENDPOINT)

public class RestaurantController {

  public static final String RESTAURANT_API_ENDPOINT = "/qeats/v1";
  public static final String RESTAURANTS_API = "/restaurants";
  public static final String MENU_API = "/menu";
  public static final String CART_API = "/cart";
  public static final String CART_ITEM_API = "/cart/item";
  public static final String CART_CLEAR_API = "/cart/clear";
  public static final String POST_ORDER_API = "/order";
  public static final String GET_ORDERS_API = "/orders";

  @Autowired
  private RestaurantService restaurantService;

  /**
   * Get all nearby restaurants given latitude and longitude. Example: GET
   * /qeats/v1/restaurants?latitude=12.934533&longitude=77.626579
   */
  @GetMapping(RESTAURANTS_API)
  public ResponseEntity<GetRestaurantsResponse> getRestaurants(
      @Valid GetRestaurantsRequest getRestaurantsRequest) {

    log.info("getRestaurants called with {}", getRestaurantsRequest);

    GetRestaurantsResponse getRestaurantsResponse =
        restaurantService.findAllRestaurantsCloseBy(getRestaurantsRequest, LocalTime.now());

    log.info("getRestaurants returned {}", getRestaurantsResponse);

    return ResponseEntity.ok().body(getRestaurantsResponse);
  }

  /**
   * Get menu for a given restaurant ID. Example: GET /qeats/v1/menu?restaurantId=11
   */
  @GetMapping(MENU_API)
  public ResponseEntity<?> getMenu(@RequestParam(name = "restaurantId") String restaurantId) {
    log.info("getMenu called with restaurantId={}", restaurantId);

    if (restaurantId == null || restaurantId.isEmpty()) {
      return ResponseEntity.badRequest().body("restaurantId is required");
    }

    List<ItemEntity> menuResponse = restaurantService.getMenu(restaurantId);
    if (menuResponse == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(menuResponse);
  }

}

