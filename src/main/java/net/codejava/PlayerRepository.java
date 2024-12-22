package net.codejava;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

@Repository
public class PlayerRepository {
    private static final String LEADERBOARD_CACHE_KEY = "leaderboard";
    private static final long CACHE_TTL = 3600; // 1 hour in seconds
    
    private final DynamoDBMapper dynamoDBMapper;
    private final RedisTemplate<String, List<Player>> redisTemplate;
    
    public PlayerRepository(DynamoDBMapper dynamoDBMapper, RedisTemplate<String, List<Player>> redisTemplate) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.redisTemplate = redisTemplate;
    }

    public Player save(Player newPlayer) {
        try {
            // Query using GSI on playerName
            DynamoDBQueryExpression<Player> queryExpression = new DynamoDBQueryExpression<Player>()
                .withIndexName("playerName-index")
                .withConsistentRead(false)
                .withKeyConditionExpression("playerName = :playerName")
                .withExpressionAttributeValues(
                    Map.of(":playerName", new AttributeValue(newPlayer.getPlayerName()))
                );

            List<Player> existingPlayers = dynamoDBMapper.query(Player.class, queryExpression);
            
            Player savedPlayer;
            if (!existingPlayers.isEmpty()) {
                Player existingPlayer = existingPlayers.get(0);
                if (newPlayer.getTimeToFinish() < existingPlayer.getTimeToFinish()) {
                    existingPlayer.setTimeToFinish(newPlayer.getTimeToFinish());
                    dynamoDBMapper.save(existingPlayer);
                    savedPlayer = existingPlayer;
                } else {
                    savedPlayer = existingPlayer;
                }
            } else {
                dynamoDBMapper.save(newPlayer);
                savedPlayer = newPlayer;
            }
            
            // Invalidate cache after save
            redisTemplate.delete(LEADERBOARD_CACHE_KEY);
            
            return savedPlayer;
            
        } catch (AmazonDynamoDBException e) {
            throw new RuntimeException("Error saving player data", e);
        }
    }

    public Page<Player> getLeaderboard(Pageable pageable) {
        try {
            // Try to get from cache first
            List<Player> sortedPlayers = getCachedLeaderboard();
            
            if (sortedPlayers == null) {
                // If not in cache, get from DynamoDB
                sortedPlayers = getAndCacheLeaderboard();
            }
            
            // Apply pagination
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), sortedPlayers.size());
            
            return new PageImpl<>(
                sortedPlayers.subList(start, end),
                pageable,
                sortedPlayers.size()
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving leaderboard", e);
        }
    }
    
    private List<Player> getCachedLeaderboard() {
        return redisTemplate.opsForValue().get(LEADERBOARD_CACHE_KEY);
    }
    
    private List<Player> getAndCacheLeaderboard() {
        // Scan all items from DynamoDB
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Player> players = dynamoDBMapper.scan(Player.class, scanExpression);
        
        // Sort by timeToFinish
        List<Player> sortedPlayers = new ArrayList<>(players);
        sortedPlayers.sort(Comparator.comparing(Player::getTimeToFinish));
        
        // Cache the sorted list
        redisTemplate.opsForValue().set(LEADERBOARD_CACHE_KEY, sortedPlayers, CACHE_TTL, TimeUnit.SECONDS);
        
        return sortedPlayers;
    }
}
