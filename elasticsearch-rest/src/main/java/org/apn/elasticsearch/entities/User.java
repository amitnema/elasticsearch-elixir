package org.apn.elasticsearch.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class User {
    @JsonProperty("User-ID")
	private long userId;
    @JsonProperty("Location")
	private String location;
    @JsonProperty("Age")
	private long age;
}
