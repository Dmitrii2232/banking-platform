package com.bank.antifraud.models;

import java.time.LocalDateTime;

public record GeoLocation(double latitude, double longitude, LocalDateTime timestamp) implements java.io.Serializable {}