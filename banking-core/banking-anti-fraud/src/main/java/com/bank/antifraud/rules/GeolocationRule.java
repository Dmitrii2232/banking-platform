package com.bank.antifraud.rules;

import com.bank.antifraud.models.Transaction;
import com.bank.antifraud.models.FraudCheckResult;
import com.bank.antifraud.models.GeoLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class GeolocationRule implements FraudRule {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${fraud.rules.geo.impossible-distance-km:1000}")
    private double impossibleDistanceKm;

    @Value("${fraud.rules.geo.impossible-time-minutes:60}")
    private long impossibleTimeMinutes;

    public GeolocationRule(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override public String getRuleName() { return "GEOLOCATION"; }
    @Override public boolean isEnabled() { return true; }
    @Override public int getPriority() { return 2; }

    @Override
    public FraudCheckResult evaluate(Transaction transaction) {
        if (transaction.getGeoLocation() == null) return FraudCheckResult.pass();

        String key = "geo:last:" + transaction.getClientId();
        GeoLocation lastLocation = (GeoLocation) redisTemplate.opsForValue().get(key);

        if (lastLocation != null) {
            double distanceKm = calculateDistance(
                lastLocation.latitude(), lastLocation.longitude(),
                transaction.getGeoLocation().latitude(), transaction.getGeoLocation().longitude());
            long minutesBetween = Duration.between(lastLocation.timestamp(), transaction.getTimestamp()).toMinutes();

            if (distanceKm > impossibleDistanceKm && minutesBetween < impossibleTimeMinutes) {
                return FraudCheckResult.fail(getRuleName(),
                    String.format("Невозможное перемещение: %.1f км за %d мин", distanceKm, minutesBetween));
            }
        }

        redisTemplate.opsForValue().set(key, transaction.getGeoLocation(), Duration.ofHours(24));
        return FraudCheckResult.pass();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDist = Math.toRadians(lat2 - lat1);
        double lonDist = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDist / 2) * Math.sin(latDist / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDist / 2) * Math.sin(lonDist / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}