package net.codejava;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    
    @Autowired
    private PlayerRepository playerRepository;
    
    public Player saveScore(Player player) {
        return playerRepository.save(player);
    }
    
    
    public Page<Player> getLeaderboard(Pageable pageable){
    	return playerRepository.getLeaderboard(pageable);
    }
}
