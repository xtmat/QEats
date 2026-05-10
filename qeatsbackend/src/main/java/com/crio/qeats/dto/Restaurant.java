
/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: CRIO_TASK_MODULE_SERIALIZATION
//  Implement Restaurant class.
// Complete the class such that it produces the following JSON during serialization.
// {
//  "restaurantId": "10",
//  "name": "A2B",
//  "city": "Hsr Layout",
//  "imageUrl": "www.google.com",
//  "latitude": 20.027,
//  "longitude": 30.0,
//  "opensAt": "18:00",
//  "closesAt": "23:00",
//  "attributes": [
//    "Tamil",
//    "South Indian"
//  ]
// }
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

  @NotNull
  @JsonProperty("restaurantId")
  private String restaurantId;

  @NotNull
  @JsonProperty("name")
  private String name;

  @NotNull
  @JsonProperty("city")
  private String city;

  @NotNull
  @JsonProperty("imageUrl")
  private String imageUrl;

  @NotNull
  @JsonProperty("latitude")
  private Double latitude;

  @NotNull
  @JsonProperty("longitude")
  private Double longitude;

  @NotNull
  @JsonProperty("opensAt")
  private String opensAt;

  @NotNull
  @JsonProperty("closesAt")
  private String closesAt;

  @JsonProperty("attributes")
  private List<String> attributes;

  // Internal field - will not appear in JSON
  @JsonIgnore
  private Double distanceInKm;
}

