package inha.git.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * RedisProvider는 Redis를 사용하기 위한 기능을 제공하는 서비스 클래스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RedisProvider {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${jwt.refresh-token.expiration}")
    private Long refreshExpiration;

    /**
     * 키에 대한 만료 시간을 설정하는 메서드.
     *
     * @param key 키
     */
    public void expireValues(String key) {
        redisTemplate.expire(key, refreshExpiration, TimeUnit.MILLISECONDS);
    }

    /**
     * 키와 값을 설정하는 메서드.
     *
     * @param key 키
     * @param value 값
     */
    public void setValueOps(String key, String value) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    /**
     * 키에 대한 값을 가져오는 메서드.
     *
     * @param key 키
     * @return 키에 대한 값
     */
    @Transactional(readOnly = true)
    public String getValueOps(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * 키에 대한 값을 삭제하는 메서드.
     *
     * @param key 키
     */
    public void deleteValueOps(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 키와 값을 설정하고 만료 시간을 설정하는 메서드.
     *
     * @param key 키
     * @param value 값
     * @param duration 만료 시간
     */
    public void setDataExpire(String key,String value,long duration){
        ValueOperations<String,Object> valueOperations= redisTemplate.opsForValue();
        Duration expireDuration=Duration.ofSeconds(duration);
        valueOperations.set(key,value,expireDuration);
    }
}
